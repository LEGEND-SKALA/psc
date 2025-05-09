package navi.navi_be.common.exception;

public class ChatNotFoundException extends NotFoundException {
    public ChatNotFoundException(Long chatId) {
        super("해당 채팅을 찾을 수 없습니다: " + chatId);
    }
}