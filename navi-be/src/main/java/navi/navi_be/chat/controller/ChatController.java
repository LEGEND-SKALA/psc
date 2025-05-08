package navi.navi_be.chat.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import navi.navi_be.auth.entity.User;
import navi.navi_be.auth.repository.UserRepository;
import navi.navi_be.chat.dto.ChatMessageRequest;
import navi.navi_be.chat.dto.ChatMessageResponse;
import navi.navi_be.chat.entity.ChatMessage;
import navi.navi_be.chat.service.ChatService;
import navi.navi_be.common.exception.AuthException;
import navi.navi_be.common.response.ApiResponse;
import navi.navi_be.common.util.JwtUtil;

@RestController
@RequestMapping("/chat/messages")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    // 메시지 저장
    @PostMapping
    public ResponseEntity<ApiResponse<ChatMessageResponse>> saveMessage(
        @RequestHeader("Authorization") String authorizationHeader,
        @RequestBody ChatMessageRequest request
    ) {
        String token = authorizationHeader.replace("Bearer ", "");
        String employeeId = jwtUtil.extractEmployeeId(token);
        User user = userRepository.findByEmployeeId(employeeId)
            .orElseThrow(() -> new AuthException("유효하지 않은 사용자입니다."));

        ChatMessage message = chatService.saveMessage(user, request);
        return ResponseEntity.ok(
            new ApiResponse<>(0, "메시지가 저장되었습니다.", ChatMessageResponse.from(message))
        );
    }

    // 채팅 이력 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> getMessages(
            @AuthenticationPrincipal User user
    ) {
        List<ChatMessage> messages = chatService.getMessagesByUser(user);
        List<ChatMessageResponse> responseList = messages.stream()
                .map(ChatMessageResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                new ApiResponse<>(0, "채팅 이력을 조회했습니다.", responseList)
        );
    }

    // 메시지 삭제 (소프트 딜리트)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMessage(@PathVariable Long id) {
        chatService.softDeleteMessage(id);
        return ResponseEntity.ok(
                new ApiResponse<>(0, "메시지가 삭제되었습니다.", null)
        );
    }
}
