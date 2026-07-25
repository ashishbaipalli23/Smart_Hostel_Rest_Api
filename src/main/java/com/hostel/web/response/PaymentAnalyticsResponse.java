package com.hostel.web.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentAnalyticsResponse {
    private double totalCollected;
    private double totalPending;
    private long paidCount;
    private long pendingCount;
}
