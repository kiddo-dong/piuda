package project.piuda.domain.chat.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateChatRoomRequest {

    @NotBlank(message = "대화 상대 닉네임을 입력해주세요.")
    private String targetNickname;
}
