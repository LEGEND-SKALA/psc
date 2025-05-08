package navi.navi_be.chat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import navi.navi_be.auth.entity.User;
import navi.navi_be.chat.entity.ChatMessage;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 사용자 기준 전체 메시지 조회 (소프트 삭제되지 않은 것만)
    List<ChatMessage> findByUserAndDeletedAtIsNullOrderByCreatedAtAsc(User user);

    // ID 기준 조회 (삭제되지 않은 메시지)
    Optional<ChatMessage> findByIdAndDeletedAtIsNull(Long id);
}
