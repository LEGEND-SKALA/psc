package navi.navi_be.useradmin.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import navi.navi_be.common.response.ApiResponse;
import navi.navi_be.useradmin.dto.UpdateUserRoleRequest;
import navi.navi_be.useradmin.dto.UserListResponse;
import navi.navi_be.useradmin.service.AdminUserService;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserListResponse>> getAllUsers() {
        return ResponseEntity.ok(adminUserService.getAllUsers());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{userId}")
    public ResponseEntity<ApiResponse<String>> updateUserRole(
        @PathVariable Long userId,
        @RequestBody UpdateUserRoleRequest request) {
        adminUserService.updateUserRole(userId, request.getRole());
        return ResponseEntity.ok(
            new ApiResponse<>(200, "사용자 역할이 성공적으로 변경되었습니다.", null)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long userId) {
        adminUserService.deleteUser(userId);
        return ResponseEntity.ok(
            new ApiResponse<>(200, "사용자가 성공적으로 삭제되었습니다.", null)
        );
    }
}
