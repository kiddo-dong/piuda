package project.piuda.domain.chat.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.piuda.domain.chat.application.dto.*;
import project.piuda.domain.chat.domain.*;
import project.piuda.domain.user.domain.User;
import project.piuda.domain.user.domain.UserRepository;
import project.piuda.global.exception.BusinessException;
import project.piuda.global.exception.ForbiddenException;
import project.piuda.global.exception.NotFoundException;
import project.piuda.global.infrastructure.FcmService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final FcmService fcmService;

    @Transactional
    public ChatRoomResponse createOrGetRoom(String userEmail, String targetNickname) {
        User me = getUser(userEmail);
        User target = userRepository.findByNickname(targetNickname)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
        if (me.getId().equals(target.getId())) {
            throw new BusinessException("자기 자신과 채팅할 수 없습니다.");
        }

        // user1은 항상 ID가 낮은 쪽 — unique constraint 중복 방지
        User user1 = me.getId() < target.getId() ? me : target;
        User user2 = me.getId() < target.getId() ? target : me;

        ChatRoom room = chatRoomRepository.findByUser1AndUser2(user1, user2)
                .orElseGet(() -> chatRoomRepository.save(
                        ChatRoom.builder().user1(user1).user2(user2).build()));

        long unread = chatMessageRepository.countByChatRoomAndSenderNotAndIsReadFalse(room, me);
        return new ChatRoomResponse(room, me, unread);
    }

    public List<ChatRoomResponse> getMyRooms(String userEmail) {
        User me = getUser(userEmail);
        return chatRoomRepository.findAllByUserOrderByLastMessage(me).stream()
                .map(room -> {
                    long unread = chatMessageRepository.countByChatRoomAndSenderNotAndIsReadFalse(room, me);
                    return new ChatRoomResponse(room, me, unread);
                })
                .collect(Collectors.toList());
    }

    public MessagePageResponse getMessages(Long roomId, String userEmail, Long cursor, int size) {
        ChatRoom room = getRoom(roomId);
        User me = getUser(userEmail);
        validateMember(room, me);

        List<ChatMessage> messages = (cursor == null)
                ? chatMessageRepository.findByChatRoomOrderByCreatedAtDesc(room, PageRequest.of(0, size + 1))
                : chatMessageRepository.findByChatRoomAndIdLessThanOrderByCreatedAtDesc(room, cursor, PageRequest.of(0, size + 1));

        boolean hasNext = messages.size() > size;
        List<ChatMessage> content = hasNext ? messages.subList(0, size) : messages;
        Long nextCursor = hasNext ? content.get(content.size() - 1).getId() : null;

        return new MessagePageResponse(
                content.stream().map(m -> new ChatMessageResponse(m, me)).collect(Collectors.toList()),
                hasNext,
                nextCursor);
    }

    @Transactional
    public ChatMessageResponse sendMessage(Long roomId, String senderEmail, String content) {
        ChatRoom room = getRoom(roomId);
        User sender = getUser(senderEmail);
        validateMember(room, sender);

        ChatMessage message = chatMessageRepository.save(
                ChatMessage.builder().chatRoom(room).sender(sender).content(content).build());
        room.updateLastMessage(content);

        User recipient = room.getOtherUser(sender);
        fcmService.send(recipient.getFcmToken(), sender.getNickname(), content);

        return new ChatMessageResponse(message, sender);
    }

    @Transactional
    public void markAsRead(Long roomId, String userEmail) {
        ChatRoom room = getRoom(roomId);
        User me = getUser(userEmail);
        validateMember(room, me);
        chatMessageRepository.markAllAsRead(room, me);
    }

    private void validateMember(ChatRoom room, User user) {
        if (!room.getUser1().getId().equals(user.getId()) && !room.getUser2().getId().equals(user.getId())) {
            throw new ForbiddenException("채팅방 접근 권한이 없습니다.");
        }
    }

    private ChatRoom getRoom(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 채팅방입니다."));
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
    }
}
