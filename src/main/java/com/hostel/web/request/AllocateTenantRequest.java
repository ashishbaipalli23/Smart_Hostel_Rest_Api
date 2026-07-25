package com.hostel.web.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AllocateTenantRequest {

    private String hostelCode;

    private String tenantUsername;

    private String bedNumber;

    private BigDecimal monthlyRent;

    private BigDecimal depositAmount; // Advance Amount

    private LocalDate checkInDate;

    // ID fields passed by frontend UI
    private Long tenantId;

    private Long bedId;
}