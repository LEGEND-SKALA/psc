package navi.navi_be.chatreview.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import navi.navi_be.auth.entity.User;
import navi.navi_be.auth.repository.UserRepository;
import navi.navi_be.chat.entity.ChatMessage;
import navi.navi_be.chat.repository.ChatMessageRepository;
import navi.navi_be.chatreview.dto.ChatReviewRequest;
import navi.navi_be.chatreview.dto.ChatReviewResponse;
import navi.navi_be.chatreview.entity.ChatReview;
import navi.navi_be.chatreview.repository.ChatReviewRepository;
import navi.navi_be.common.util.JwtUtil;

@Service
@RequiredArgsConstructor
public class ChatReviewService {

    private final ChatReviewRepository chatReviewRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    /**
     * 리뷰 저장
     */
    @Transactional
    public void createReview(String authorizationHeader, ChatReviewRequest request) {
        // 1. 토큰 추출
        String token = authorizationHeader.replace("Bearer ", "").trim();

        // 2. JWT에서 employeeId 추출
        String employeeId = jwtUtil.extractEmployeeId(token);

        // 3. DB에서 사용자 조회
        User user = userRepository.findByEmployeeId(employeeId)
            .orElseThrow(() -> new IllegalArgumentException("로그인된 사용자를 찾을 수 없습니다."));

        // 4. 채팅 메시지 조회
        ChatMessage message = chatMessageRepository.findById(request.getMessageId())
            .orElseThrow(() -> new IllegalArgumentException("해당 메시지를 찾을 수 없습니다."));

        // 5. 리뷰 저장
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
