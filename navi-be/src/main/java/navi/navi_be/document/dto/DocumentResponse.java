package navi.navi_be.document.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import navi.navi_be.document.entity.Document;
import navi.navi_be.document.model.DocumentCategory;

@Getter
@Builder
public class DocumentResponse {

    private final Long id;
    private final String title;
    private final DocumentCategory category;
    private final String filePath;
    private final LocalDateTime createdAt;

    public static DocumentResponse from(Document document) {
        return DocumentResponse.builder()
                .id(document.getId())
                .title(document.getTitle())
                .category(document.getCategory())
                .filePath(document.getFilePath())
                .createdAt(document.getCreatedAt())
                .build();
    }
}
