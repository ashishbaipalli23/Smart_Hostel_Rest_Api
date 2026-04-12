package com.hostel.web.request;

import com.hostel.utils.DocumentStatus;
import lombok.Data;

@Data
public class DocumentVerificationRequestDto {
    private DocumentStatus status;
}
