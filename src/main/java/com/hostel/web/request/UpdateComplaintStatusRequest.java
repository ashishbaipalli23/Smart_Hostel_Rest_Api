package com.hostel.web.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateComplaintStatusRequest {
    @NotBlank(message = "Status is required")
    private String status;

    private String comment;
}
