package com.hostel.web.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterVisitorRequest {
    @NotBlank(message = "Visitor name is required")
    private String name;

    @NotBlank(message = "Visitor phone is required")
    private String phone;

    private String purpose;

    @NotNull(message = "Tenant ID is required")
    private Long tenantId;

    private String roomNumber;
}
