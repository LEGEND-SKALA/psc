package navi.navi_be.chatreview.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import navi.navi_be.auth.entity.User;
import navi.navi_be.chat.entity.ChatMessage;
import navi.navi_be.chat.repository.ChatMessageRepository;
import navi.navi_be.chatreview.dto.ChatReviewRequest;
import navi.navi_be.chatreview.dto.ChatReviewResponse;
import navi.navi_be.chatreview.entity.ChatReview;
import navi.navi_be.chatreview.repository.ChatReviewRepository;

@Service
@RequiredArgsConstructor
public class ChatReviewService {

    private final ChatReviewRepository chatReviewRepository;
    private final ChatMessageRepository chatMessageRepository;

    /**
     * 리뷰 저장
     */
    @Transactional
    public void createReview(User user, ChatReviewRequest request) {
        ChatMessage message = chatMessageRepository.findById(request.getMessageId())
            .orElseThrow(() -> new IllegalArgumentException("해당 메시지를 찾을 수 없습니다."));

        ChatReview review = ChatReview.builder()
            .user(user)
            .message(message)
            .rating(request.getRating())
            .build();

        chatReviewRepository.save(review);
    }

    /**
     * 사용자별 리뷰 목록 조회 (관리자용)
     */
    @Transactional(readOnly = true)
    public List<ChatReviewResponse> getReviewsByUserId(Long userId) {
        return chatReviewRepository.findByUserId(userId).stream()
            .map(review -> ChatReviewResponse.builder()
                .messageId(review.getMessage().getId())
                .content(review.getMessage().getMessage())
                .rating(review.getRating())
                .createdAt(review.getCreatedAt())
                .build())
            .collect(Collectors.toList());
    }
}
