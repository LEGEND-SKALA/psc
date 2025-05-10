package navi.navi_be.chatreview.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ChatReviewRequest {
    
    @NotNull(message = "채팅 메시지 ID는 필수입니다.")
    private Long messageId;

    @Min(value = 1, message = "별점은 최소 1점 이상이어야 합니다.")
    @Max(value = 5, message = "별점은 최대 5점 이하이어야 합니다.")
    private int rating;
}
