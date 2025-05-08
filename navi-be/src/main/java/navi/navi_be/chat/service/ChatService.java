package navi.navi_be.chat.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import navi.navi_be.auth.entity.User;
import navi.navi_be.chat.dto.ChatMessageRequest;
import navi.navi_be.chat.entity.ChatMessage;
import navi.navi_be.chat.model.DeviceType;
import navi.navi_be.chat.model.SenderType;
import navi.navi_be.chat.repository.ChatMessageRepository;
import navi.navi_be.common.exception.NotFoundException;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;

    // 메시지 저장
    public ChatMessage saveMessage(User user, ChatMessageRequest request) {
        ChatMessage message = ChatMessage.builder()
                .user(user)
                .role(DeviceType.valueOf(request.getRole()))
                .sender(SenderType.valueOf(request.getSender()))
                .message(request.getMessage())
                .build();

        return chatMessageRepository.save(message);
    }

    // 사용자 기준 채팅 이력 조회
    @Transactional(readOnly = true)
    public List<ChatMessage> getMessagesByUser(User user) {
        return chatMessageRepository.findByUserAndDeletedAtIsNullOrderByCreatedAtAsc(user);
    }

    // 메시지 소프트 삭제
    @Transactional
    public void softDeleteMessage(Long id) {
        ChatMessage message = chatMessageRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("메시지를 찾을 수 없습니다."));

        message.softDelete(); // deletedAt 설정
    }
}
