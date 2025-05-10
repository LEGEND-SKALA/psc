package navi.navi_be.dashboard.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import navi.navi_be.common.response.ApiResponse;
import navi.navi_be.dashboard.dto.DailyMessageCountResponse;
import navi.navi_be.dashboard.dto.DepartmentMessageCountResponse;
import navi.navi_be.dashboard.service.DashboardService;

@RestController
@RequestMapping("/dashboard/messages")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/daily-count")
    public ResponseEntity<ApiResponse<List<DailyMessageCountResponse>>> getDailyCount() {
        return ResponseEntity.ok(
                new ApiResponse<>(0, "일별 메시지 수 조회 성공", dashboardService.getDailyMessageCount()));
    }

    @GetMapping("/department-count")
    public ResponseEntity<ApiResponse<List<DepartmentMessageCountResponse>>> getDeptCount() {
        return ResponseEntity.ok(
                new ApiResponse<>(0, "부서별 메시지 수 조회 성공", dashboardService.getDepartmentMessageCount()));
    }

    @GetMapping("/total-count")
    public ResponseEntity<ApiResponse<Long>> getTotalCount() {
        return ResponseEntity.ok(
                new ApiResponse<>(0, "총 메시지 수 조회 성공", dashboardService.getTotalMessageCount()));
    }
}
