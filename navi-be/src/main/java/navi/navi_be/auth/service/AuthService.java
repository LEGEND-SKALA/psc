package navi.navi_be.auth.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import navi.navi_be.auth.dto.AuthResponse;
import navi.navi_be.auth.dto.LoginRequest;
import navi.navi_be.auth.dto.SignupRequest;
import navi.navi_be.auth.entity.User;
import navi.navi_be.auth.repository.UserRepository;
import navi.navi_be.common.exception.AuthException;
import navi.navi_be.common.util.JwtUtil;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public void signup(SignupRequest request) {
        if (userRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new AuthException("이미 등록된 사번입니다.");
        }

        User user = new User(request);
        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmployeeId(request.getEmployeeId())
            .orElseThrow(() -> new AuthException("사번 또는 비밀번호가 일치하지 않습니다."));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new AuthException("사번 또는 비밀번호가 일치하지 않습니다.");
        }
        
        String token = jwtUtil.generateToken(user.getEmployeeId());

        // 실제로는 JWT 생성
        return new AuthResponse(token, "Bearer");
    }
}
