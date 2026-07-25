package com.hostel.web.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreatePaymentRequest {
    @NotNull(message = "Tenant ID is required")
    private Long tenantId;

    @NotNull(message = "Amount is required")
    private Double amount;

    @NotNull(message = "Month is required")
    private String month;

    private String paymentMethod;
}
