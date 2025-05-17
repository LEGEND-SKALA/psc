package navi.navi_be.external;

import java.io.File;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpMethod;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FastApiClient {

    private final WebClient fastapiWebClient;

    public void uploadToVectorDB(File file, String category) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", new FileSystemResource(file))
               .contentType(MediaType.APPLICATION_PDF);
        builder.part("category", category);

        String response = fastapiWebClient.post()
                .uri("/add")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        log.info("✅ FastAPI 응답: {}", response);
    }
    
    public void deleteFromVectorDB(String filename, String category) {
        var requestBody = new java.util.HashMap<String, String>();
        requestBody.put("filename", filename);
        requestBody.put("category", category);

        String response = fastapiWebClient.method(HttpMethod.DELETE)
                .uri("/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody) // 이제 사용 가능
                .retrieve()
                .bodyToMono(String.class)
                .block();

        log.info("🗑️ FastAPI 삭제 응답: {}", response);
    }
    
    public String chatWithLLM(String messageText) {
        var formData = new org.springframework.util.LinkedMultiValueMap<String, String>();
        formData.add("message_text", messageText);

        String response = fastapiWebClient.post()
                .uri("/chat")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        log.info("🤖 FastAPI LLM 응답: {}", response);
        return response;
    }

}
