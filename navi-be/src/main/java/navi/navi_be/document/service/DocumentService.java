package navi.navi_be.document.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

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
    private final WebClient webClient;  // ✅ WebClient 주입받음

    @Value("${document.upload.dir}")
    private String uploadDir;

    @Value("${http://10.250.72.251:8000}") // 예: http://192.168.0.10:8000
    private String fastapiServerUrl;

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

        // ✅ FastAPI에 /add 요청
        try {
    byte[] bytes = file.getBytes();  // MultipartFile에서 byte 배열 추출

    MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
    bodyBuilder.part("file", new ByteArrayResource(bytes) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();  // 파일명 설정
                }
            })
            .contentType(MediaType.APPLICATION_PDF);
    bodyBuilder.part("category", request.getCategory());

    webClient.post()
            .uri(fastapiServerUrl + "/add")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
            .retrieve()
            .bodyToMono(String.class)
            .doOnNext(response -> log.info("FastAPI 응답: {}", response))
            .block();

} catch (IOException e) {
    throw new RuntimeException("FastAPI 호출 중 오류", e);
}
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