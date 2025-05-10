package navi.navi_be.chat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import navi.navi_be.auth.entity.User;
import navi.navi_be.chat.dto.DailyCountProjection;
import navi.navi_be.chat.dto.DepartmentCountProjection;
import navi.navi_be.chat.entity.ChatMessage;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 사용자 기준 전체 메시지 조회 (소프트 삭제되지 않은 것만)
    List<ChatMessage> findByUserAndDeletedAtIsNullOrderByCreatedAtAsc(User user);

    // ID 기준 조회 (삭제되지 않은 메시지)
    Optional<ChatMessage> findByIdAndDeletedAtIsNull(Long id);

    @Modifying
    @Transactional
    void deleteByUser(User user);

    @Query("SELECT DATE(m.createdAt) AS date, COUNT(m) AS count " +
       "FROM ChatMessage m " +
       "WHERE m.deletedAt IS NULL " +
       "GROUP BY DATE(m.createdAt) " +
       "ORDER BY DATE(m.createdAt)")
    List<DailyCountProjection> countMessagesGroupedByDate();

    @Query(value = 
        "SELECT u.department AS department, COUNT(m.id) AS count " +
        "FROM chat_message m " +
        "JOIN users u ON m.user_id = u.id " +
        "WHERE m.deleted_at IS NULL " +
        "GROUP BY u.department",
        nativeQuery = true)
    List<DepartmentCountProjection> countMessagesGroupedByDepartment();

    // 누적 메시지 수
    Long countByDeletedAtIsNull();
}
