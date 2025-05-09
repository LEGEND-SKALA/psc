package navi.navi_be.useradmin.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import navi.navi_be.auth.entity.User;
import navi.navi_be.auth.model.UserRole;
import navi.navi_be.auth.repository.UserRepository;
import navi.navi_be.chat.repository.ChatMessageRepository;
import navi.navi_be.common.exception.UserNotFoundException;
import navi.navi_be.useradmin.dto.UserListResponse;

@Service
@RequiredArgsConstructor
public class AdminUserService {
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;

    public List<UserListResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserListResponse(
                        user.getId(),                
                        user.getEmployeeId(),        
                        user.getName(),              
                        user.getEmail(),                 
                        user.getDepartment().name(),
                        user.getRole().name()
                ))
                .collect(Collectors.toList());
    }

    public void updateUserRole(Long userId, String role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        try {
            UserRole roleEnum = UserRole.valueOf(role.toUpperCase());
            user.setRole(roleEnum);
            userRepository.save(user);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("유효하지 않은 역할입니다: " + role);
        }
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        chatMessageRepository.deleteByUser(user); // 메시지 먼저 삭제
        userRepository.delete(user);              // 그 다음 유저 삭제
    }
}
