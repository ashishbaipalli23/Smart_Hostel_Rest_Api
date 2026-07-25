package com.hostel.web.request;

import com.hostel.enums.DocumentStatus;
import lombok.Data;

@Data
public class DocumentVerificationRequestDto {
    private DocumentStatus status;
}
