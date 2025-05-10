package navi.navi_be.chatreview.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ChatReviewResponse {
    
    private final Long messageId;
    private final String content;
    private final int rating;
    private final LocalDateTime createdAt;
}
