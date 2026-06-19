package project.piuda.domain.chat.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import project.piuda.domain.chat.application.ChatService;
import project.piuda.domain.chat.application.dto.*;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Tag(name = "Chat", description = "1:1 채팅 REST API")
@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @Operation(summary = "채팅방 생성 또는 조회",
            description = "대상 닉네임으로 채팅방을 생성하거나, 이미 있으면 기존 방을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "성공")
    @PostMapping
    public ResponseEntity<ChatRoomResponse> createOrGetRoom(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CreateChatRoomRequest request) {
        return ResponseEntity.ok(chatService.createOrGetRoom(userDetails.getUsername(), request.getTargetNickname()));
    }

    @Operation(summary = "내 채팅방 목록 조회",
            description = "최근 메시지 순으로 채팅방 목록과 읽지 않은 메시지 수를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "성공")
    @GetMapping
    public ResponseEntity<List<ChatRoomResponse>> getMyRooms(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(chatService.getMyRooms(userDetails.getUsername()));
    }

    @Operation(summary = "메시지 내역 조회",
            description = "커서 기반 페이징으로 메시지를 최신순으로 반환합니다. cursor 없으면 가장 최신부터 조회.")
    @ApiResponse(responseCode = "200", description = "성공")
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<MessagePageResponse> getMessages(
            @Parameter(description = "채팅방 ID") @PathVariable Long roomId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "이전 페이지의 마지막 메시지 ID") @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "30") int size) {
        return ResponseEntity.ok(chatService.getMessages(roomId, userDetails.getUsername(), cursor, size));
    }

    @Operation(summary = "이미지/파일 전송",
            description = "이미지(IMAGE) 또는 파일(FILE)을 S3에 업로드하고 채팅 메시지로 저장합니다. " +
                          "저장 후 /topic/chat/{roomId}로 브로드캐스트됩니다.")
    @ApiResponse(responseCode = "200", description = "전송 성공")
    @PostMapping(value = "/{roomId}/files", consumes = "multipart/form-data")
    public ResponseEntity<List<ChatMessageResponse>> sendFiles(
            @Parameter(description = "채팅방 ID") @PathVariable Long roomId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestPart("files") List<MultipartFile> files) throws IOException {
        List<ChatMessageResponse> responses = chatService.sendFiles(roomId, userDetails.getUsername(), files);
        responses.forEach(r -> messagingTemplate.convertAndSend("/topic/chat/" + roomId, r));
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "읽음 처리",
            description = "채팅방의 읽지 않은 메시지를 모두 읽음으로 처리하고, 상대방에게 읽음 이벤트를 전송합니다.")
    @ApiResponse(responseCode = "200", description = "성공")
    @PatchMapping("/{roomId}/read")
    public ResponseEntity<Void> markAsRead(
            @Parameter(description = "채팅방 ID") @PathVariable Long roomId,
            @AuthenticationPrincipal UserDetails userDetails) {
        chatService.markAsRead(roomId, userDetails.getUsername());
        messagingTemplate.convertAndSend("/topic/chat/" + roomId + "/read",
                Map.of("roomId", roomId, "readerNickname", userDetails.getUsername()));
        return ResponseEntity.ok().build();
    }
}
