package navi.navi_be.chat.dto;

import lombok.Data;

@Data
public class ChatMessageRequest {
    private String role;     // "PC" or "KIOSK"
    private String sender;   // "USER" or "AGENT"
    private String message;
}