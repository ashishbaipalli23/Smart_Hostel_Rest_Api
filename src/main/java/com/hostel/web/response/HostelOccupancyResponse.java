package com.hostel.web.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HostelOccupancyResponse {
    private String hostelName;
    private long capacity;
    private long occupied;
    private long available;
}
