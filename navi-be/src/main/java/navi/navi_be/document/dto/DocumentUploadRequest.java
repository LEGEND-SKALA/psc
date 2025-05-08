package navi.navi_be.document.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import navi.navi_be.document.model.DocumentCategory;

@Getter
@AllArgsConstructor
public class DocumentUploadRequest {

    @NotNull(message = "파일은 필수입니다.")
    private final MultipartFile file;

    @NotNull(message = "제목은 필수입니다.")
    private final String title;

    @NotNull(message = "카테고리는 필수입니다.")
    private final DocumentCategory category;

}
