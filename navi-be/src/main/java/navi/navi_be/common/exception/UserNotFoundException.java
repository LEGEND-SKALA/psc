package navi.navi_be.common.exception;

public class UserNotFoundException extends NotFoundException{
    public UserNotFoundException(Long userId) {
        super("사용자를 찾을 수 없습니다. (ID: " + userId + ")");
    }
}
