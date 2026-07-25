package com.hostel.service;

import com.hostel.web.response.DashboardStatsResponse;
import com.hostel.web.response.HostelOccupancyResponse;
import com.hostel.web.response.PaymentAnalyticsResponse;
import com.hostel.web.response.StaffDashboardStatsResponse;
import java.util.List;

public interface IDashboardService {
    DashboardStatsResponse getDashboardStats();
    List<HostelOccupancyResponse> getOccupancyStats();
    PaymentAnalyticsResponse getPaymentAnalytics();
    StaffDashboardStatsResponse getStaffDashboardStats(String staffUsername);
}
