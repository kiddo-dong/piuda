package project.piuda.domain.chat.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MessagePageResponse {
    private final List<ChatMessageResponse> messages;
    private final boolean hasNext;
    private final Long nextCursor;
}
