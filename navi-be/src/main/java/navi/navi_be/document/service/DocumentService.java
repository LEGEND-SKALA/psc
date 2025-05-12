package navi.navi_be.document.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import navi.navi_be.document.dto.DocumentResponse;
import navi.navi_be.document.dto.DocumentUploadRequest;
import navi.navi_be.document.entity.Document;
import navi.navi_be.document.model.DocumentStatus;
import navi.navi_be.document.repository.DocumentRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;

    @Value("${document.upload.dir}")
    private String uploadDir;


    // 문서 업로드 + FastAPI로 전달
    public void uploadDocument(DocumentUploadRequest request) {
        MultipartFile file = request.getFile();
        String fileName = generateUniqueFileName(file.getOriginalFilename());

        File dest = new File(uploadDir, fileName);
        try {
            dest.getParentFile().mkdirs();
            file.transferTo(dest);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }

        // DB 저장
        Document document = Document.builder()
                .title(file.getOriginalFilename())
                .category(request.getCategory())
                .security(request.getSecurityLevel())
                .filePath(dest.getAbsolutePath())
                .status(DocumentStatus.ACTIVE)
                .build();
        documentRepository.save(document);       
    }

    public List<DocumentResponse> getActiveDocuments() {
        return documentRepository.findByStatus(DocumentStatus.ACTIVE).stream()
                .map(DocumentResponse::from)
                .collect(Collectors.toList());
    }

    public void deleteDocumentById(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 문서를 찾을 수 없습니다."));
        document.softDelete();
        documentRepository.save(document);
    }

    private String generateUniqueFileName(String originalFileName) {
        String ext = "";
        int i = originalFileName.lastIndexOf(".");
        if (i != -1) ext = originalFileName.substring(i);
        return UUID.randomUUID().toString() + ext;
    }
}