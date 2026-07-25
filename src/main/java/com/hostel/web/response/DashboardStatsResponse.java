package com.hostel.web.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {
    private long totalTenants;
    private long totalRooms;
    private long totalBeds;
    private long occupiedBeds;
    private long availableBeds;
    private String occupancyRate;
    private long pendingPayments;
    private double pendingAmount;
    private long openComplaints;
    private long inProgressComplaints;
    private long todayVisitors;
}
