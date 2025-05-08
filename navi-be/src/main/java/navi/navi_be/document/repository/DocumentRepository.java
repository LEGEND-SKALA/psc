package navi.navi_be.document.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import navi.navi_be.document.entity.Document;
import navi.navi_be.document.model.DocumentStatus;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    // 상태가 ACTIVE인 문서만 조회
    List<Document> findByStatus(DocumentStatus status);
}