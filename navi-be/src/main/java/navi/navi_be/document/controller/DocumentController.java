package navi.navi_be.document.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import navi.navi_be.common.response.ApiResponse;
import navi.navi_be.document.dto.DocumentResponse;
import navi.navi_be.document.dto.DocumentUploadRequest;
import navi.navi_be.document.model.DocumentCategory;
import navi.navi_be.document.model.DocumentSecurityLevel;
import navi.navi_be.document.service.DocumentService;

@Slf4j
@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    // 문서 업로드
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> uploadDocument(
        @RequestPart("file") MultipartFile file,
        @RequestPart("category") String category,
        @RequestPart("security") String security
    ) {
        DocumentCategory enumCategory;
        DocumentSecurityLevel enumSecurityLevel;

        try {
            enumCategory = DocumentCategory.valueOf(category.toUpperCase());
        }   catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(1001, "유효하지 않은 카테고리입니다.", null));
        }
        try {
            enumSecurityLevel = DocumentSecurityLevel.valueOf(security.toUpperCase());
        }   catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(1001, "유효하지 않은 보안등급입니다.", null));
        }

        DocumentUploadRequest request = new DocumentUploadRequest(file, enumCategory, enumSecurityLevel);
        documentService.uploadDocument(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ApiResponse<>(0, "문서 업로드에 성공했습니다.", null));
        }

    // 문서 목록 조회
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<DocumentResponse>>> getDocuments() {
        List<DocumentResponse> documents = documentService.getActiveDocuments();
        return ResponseEntity.ok(new ApiResponse<>(0, "문서 목록을 조회했습니다.", documents));
    }

    // 문서 삭제 (소프트 딜리트)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocumentById(id);
        return ResponseEntity.ok(new ApiResponse<>(0, "문서가 삭제되었습니다.", null));
    }
}
