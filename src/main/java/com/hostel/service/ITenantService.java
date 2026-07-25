package com.hostel.service;

import com.hostel.web.request.TenantProfileUpdateDto;
import com.hostel.web.response.TenantDocumentResponseDto;
import com.hostel.models.UserEntity;
import com.hostel.enums.DocumentStatus;
import com.hostel.models.TenantDocument;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ITenantService {

    List<UserEntity> getAllTenants();

    UserEntity getTenantById(Long id);

    UserEntity updateProfile(Long tenantId, TenantProfileUpdateDto dto);

    TenantDocumentResponseDto uploadDocument(Long tenantId, String documentType, MultipartFile file) throws Exception;

    List<TenantDocumentResponseDto> getTenantDocuments(Long tenantId);

    TenantDocumentResponseDto verifyDocument(Long documentId, DocumentStatus newStatus);

    byte[] downloadDocument(Long documentId) throws Exception;

    TenantDocument getDocumentById(Long documentId);
}
