package navi.navi_be.chatreview.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import navi.navi_be.chatreview.dto.ChatReviewRequest;
import navi.navi_be.chatreview.dto.ChatReviewResponse;
import navi.navi_be.chatreview.service.ChatReviewService;

@RestController
@RequestMapping("/chat/reviews")
@RequiredArgsConstructor
public class ChatReviewController {

    private final ChatReviewService chatReviewService;

    /**
     * ⭐ 사용자 리뷰 등록 API
     * POST /chat/reviews
     */
    @PostMapping
    public ResponseEntity<String> createReview(
            @RequestHeader("Authorization") String authorizationHeader,
            @Validated @RequestBody ChatReviewRequest request) {

        chatReviewService.createReview(authorizationHeader, request);
        return ResponseEntity.ok("리뷰가 성공적으로 등록되었습니다.");
    }

    /**
     * 📄 관리자용 특정 사용자 리뷰 조회 API
     * GET /chat/reviews/user/{id}
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{id}")
    public ResponseEntity<List<ChatReviewResponse>> getReviewsByUser(@PathVariable Long id) {
        List<ChatReviewResponse> responses = chatReviewService.getReviewsByUserId(id);
        return ResponseEntity.ok(responses);
    }
}
