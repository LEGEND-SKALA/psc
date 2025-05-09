package navi.navi_be.useradmin.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import navi.navi_be.auth.entity.User;
import navi.navi_be.auth.model.UserRole;
import navi.navi_be.auth.repository.UserRepository;
import navi.navi_be.common.exception.UserNotFoundException;
import navi.navi_be.useradmin.dto.UserListResponse;

@Service
@RequiredArgsConstructor
public class AdminUserService {
    private final UserRepository userRepository;

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

    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        userRepository.deleteById(userId);
    }
}
