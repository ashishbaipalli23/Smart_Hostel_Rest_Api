package com.hostel.web.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TenantDocumentResponseDto {
    private Long id;
    private String documentType;
    private String status;
    private LocalDateTime uploadedAt;
}
