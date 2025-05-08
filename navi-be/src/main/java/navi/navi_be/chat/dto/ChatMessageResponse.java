package navi.navi_be.chat.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import navi.navi_be.chat.entity.ChatMessage;

@Data
@AllArgsConstructor
public class ChatMessageResponse {
    private Long id;
    private String message;
    private String sender;
    private String role;
    private LocalDateTime createdAt;

    public static ChatMessageResponse from(ChatMessage message) {
        return new ChatMessageResponse(
                message.getId(),
                message.getMessage(),
                message.getSender().name(),
                message.getRole().name(),
                message.getCreatedAt()
        );
    }
}
