package navi.navi_be.chatreview.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import navi.navi_be.chatreview.entity.ChatReview;

public interface ChatReviewRepository extends JpaRepository<ChatReview, Long> {
    
    // 특정 사용자의 리뷰 전체 조회
    List<ChatReview> findByUserId(Long userId);
}