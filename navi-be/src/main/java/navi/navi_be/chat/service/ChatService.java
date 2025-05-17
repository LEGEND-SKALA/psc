package navi.navi_be.chat.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import navi.navi_be.auth.entity.User;
import navi.navi_be.chat.dto.ChatMessageRequest;
import navi.navi_be.chat.dto.ChatMessageResponse;
import navi.navi_be.chat.entity.ChatMessage;
import navi.navi_be.chat.model.DeviceType;
import navi.navi_be.chat.model.SenderType;
import navi.navi_be.chat.repository.ChatMessageRepository;
import navi.navi_be.common.exception.ChatNotFoundException;
import navi.navi_be.external.FastApiClient;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final FastApiClient fastApiClient;

    // 메시지 저장
    public ChatMessageResponse saveMessage(User user, ChatMessageRequest request) {
        ChatMessage userMessage = ChatMessage.builder()
                .user(user)
                .role(DeviceType.valueOf(request.getRole()))
                .sender(SenderType.valueOf(request.getSender()))
                .message(request.getMessage())
                .build();

        chatMessageRepository.save(userMessage);

        // 2. FastAPI에 메시지 전송 → LLM 응답 받기
        String llmResponse = "사내 편의시설에 대한 정보는 제공된 문서에는 명시되어 있지 않습니다. 그러나 일반적으로 많은 회사에서는 근로자들의 편의를 위해 다양한 시설을 운영하고 있습니다. 이러한 편의시설에는 다음과 같은 것들이 포함될 수 있습니다:\n\n1. **휴게실**: 근로자들이 휴식을 취할 수 있는 공간으로, 소파나 테이블, 음료 자판기 등이 마련되어 있을 수 있습니다.\n\n2. **식당 또는 카페테리아**: 근로자들이 식사를 할 수 있는 공간으로, 점심시간에 이용할 수 있는 식사 서비스가 제공될 수 있습니다.\n\n3. **운동시설**: 일부 회사에서는 직원들의 건강을 위해 헬스장이나 운동 공간을 제공하기도 합니다.\n\n4. **회의실**: 팀 회의나 프레젠테이션을 위한 공간으로, 필요한 장비가 갖춰져 있을 수 있습니다.\n\n5. **주차 공간**: 차량을 이용하는 근로자들을 위한 주차 공간이 제공될 수 있습니다.\n\n6. **자전거 보관소**: 자전거로 출퇴근하는 근로자들을 위한 안전한 자전거 보관 공간이 있을 수 있습니다.\n\n이 외에도 회사마다 다양한 편의시설이 있을 수 있으니, 구체적인 시설에 대한 정보는 인사팀이나 시설 관리 부서에 문의하시는 것이 좋습니다. 이용 방법이나 절차에 대해서도 해당 부서에서 자세히 안내받을 수 있습니다. \n\n편의시설을 이용할 때는 회사의 규정이나 이용 시간 등을 확인하여 원활하게 이용하시기 바랍니다.";
        // String llmResponse = fastApiClient.chatWithLLM(request.getMessage());

        // 3. LLM 응답도 메시지로 저장
        ChatMessage aiMessage = ChatMessage.builder()
                .user(user)
                .role(DeviceType.PC)        // 또는 PC, KIOSK 등
                .sender(SenderType.AGENT)      // 또는 USER, AGENT 등
                .message(llmResponse)
                .build();
        chatMessageRepository.save(aiMessage);

        return ChatMessageResponse.from(aiMessage);
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
                .orElseThrow(() -> new ChatNotFoundException(id));
        message.softDelete(); // deletedAt 설정
    }
}
