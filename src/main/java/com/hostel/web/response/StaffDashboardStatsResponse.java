package com.hostel.web.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffDashboardStatsResponse {
    private long totalAssignedCount;
    private long completedCount;
    private long inProgressCount;
    private long openCount;
}
