package com.hostel.controller;

import com.hostel.service.IDashboardService;
import com.hostel.web.response.APIResponse;
import com.hostel.web.response.DashboardStatsResponse;
import com.hostel.web.response.HostelOccupancyResponse;
import com.hostel.web.response.PaymentAnalyticsResponse;
import com.hostel.web.response.StaffDashboardStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final IDashboardService dashboardService;

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<APIResponse<DashboardStatsResponse>> getStats() {
        DashboardStatsResponse stats = dashboardService.getDashboardStats();
        APIResponse<DashboardStatsResponse> apiResponse = APIResponse.<DashboardStatsResponse>builder()
                .timeStamp(LocalDateTime.now())
                .msg("Dashboard operational stats")
                .data(stats)
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/staff/stats")
    @PreAuthorize("hasAnyRole('ROLE_STAFF', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<APIResponse<StaffDashboardStatsResponse>> getStaffStats(Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "Staff";
        StaffDashboardStatsResponse stats = dashboardService.getStaffDashboardStats(username);
        APIResponse<StaffDashboardStatsResponse> apiResponse = APIResponse.<StaffDashboardStatsResponse>builder()
                .timeStamp(LocalDateTime.now())
                .msg("Staff operational maintenance stats")
                .data(stats)
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/occupancy")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<APIResponse<List<HostelOccupancyResponse>>> getOccupancy() {
        List<HostelOccupancyResponse> occupancy = dashboardService.getOccupancyStats();
        APIResponse<List<HostelOccupancyResponse>> apiResponse = APIResponse.<List<HostelOccupancyResponse>>builder()
                .timeStamp(LocalDateTime.now())
                .msg("Hostel occupancy stats")
                .data(occupancy)
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/payments")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<APIResponse<PaymentAnalyticsResponse>> getPaymentAnalytics() {
        PaymentAnalyticsResponse analytics = dashboardService.getPaymentAnalytics();
        APIResponse<PaymentAnalyticsResponse> apiResponse = APIResponse.<PaymentAnalyticsResponse>builder()
                .timeStamp(LocalDateTime.now())
                .msg("Payment analytics")
                .data(analytics)
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
