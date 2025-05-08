package navi.navi_be.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import navi.navi_be.auth.dto.AuthResponse;
import navi.navi_be.auth.dto.LoginRequest;
import navi.navi_be.auth.dto.SignupRequest;
import navi.navi_be.auth.service.AuthService;
import navi.navi_be.common.response.ApiResponse;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@RequestBody SignupRequest request) {
        authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(new ApiResponse<>(0, "회원가입이 완료되었습니다.", null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(new ApiResponse<>(0, "로그인에 성공했습니다", response));
    }

    @PostMapping("/logout")
    // JWT 기반 인증 시스템은 서버가 상태를 기억하지 않기 때문에 클라이언트 측에서 토큰을 제거하는 방식으로 처리
    public ResponseEntity<ApiResponse<Void>> logout() {
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                             .body(new ApiResponse<>(0, "로그아웃 되었습니다.", null));
    }
}
