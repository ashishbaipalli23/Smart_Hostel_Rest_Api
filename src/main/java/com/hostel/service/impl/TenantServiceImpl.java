package com.hostel.service.impl;

import com.hostel.web.request.TenantProfileUpdateDto;
import com.hostel.web.response.TenantDocumentResponseDto;
import com.hostel.models.UserEntity;
import com.hostel.models.TenantDocument;
import com.hostel.utils.DocumentStatus;
import com.hostel.utils.DocumentType;
import com.hostel.repository.UserRepository;
import com.hostel.repository.TenantDocumentRepository;
import com.hostel.service.ITenantService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TenantServiceImpl implements ITenantService {

    private final UserRepository userRepository;
    private final TenantDocumentRepository documentRepository;
    private final FileStorageService fileStorageService;

    public TenantServiceImpl(UserRepository userRepository,
                             TenantDocumentRepository documentRepository,
                             FileStorageService fileStorageService) {
        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    @Transactional
    public UserEntity updateProfile(Long tenantId, TenantProfileUpdateDto dto) {
        log.info("Updating profile for tenant ID: {}", tenantId);
        UserEntity user = userRepository.findById(tenantId)
                .orElseThrow(() -> {
                    log.error("Tenant with ID {} not found", tenantId);
                    return new RuntimeException("User not found");
                });

        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getPhoneNumber() != null) user.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getAddress() != null) user.setAddress(dto.getAddress());
        if (dto.getCity() != null) user.setCity(dto.getCity());
        if (dto.getState() != null) user.setState(dto.getState());
        if (dto.getPincode() != null) user.setPincode(dto.getPincode());
        if (dto.getAadhaarNumber() != null) user.setAadhaarNumber(dto.getAadhaarNumber());

        log.debug("Successfully updated profile fields for tenant ID: {}", tenantId);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public TenantDocumentResponseDto uploadDocument(Long tenantId, String documentType, MultipartFile file) throws Exception {
        log.info("Initiating upload logic for tenant ID: {}, documentType: {}", tenantId, documentType);
        
        UserEntity tenant = userRepository.findById(tenantId)
                .orElseThrow(() -> {
                    log.error("Tenant not found with ID: {}", tenantId);
                    return new RuntimeException("Tenant not found");
                });

        // Validate Enum before file operation to prevent storing files on bad requests
        DocumentType type;
        try {
            type = DocumentType.valueOf(documentType.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Invalid document base type requested: {}", documentType);
            throw new IllegalArgumentException("Invalid document type: " + documentType);
        }

        log.debug("Document type valid, mapping to storage service.");
        String savedFilename = fileStorageService.storeFile(file, tenantId);
        log.info("Encrypted file stored on filesystem as: {}", savedFilename);

        TenantDocument document = new TenantDocument();
        document.setTenant(tenant);
        document.setFilePath(savedFilename);
        document.setDocumentType(type);
        document.setStatus(DocumentStatus.PENDING);

        TenantDocument savedDoc = documentRepository.save(document);
        log.info("Tenant Document meta-data efficiently saved in database with ID: {}", savedDoc.getId());
        return mapToDto(savedDoc);
    }

    @Override
    public List<TenantDocumentResponseDto> getTenantDocuments(Long tenantId) {
        return documentRepository.findByTenantId(tenantId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TenantDocumentResponseDto verifyDocument(Long documentId, DocumentStatus newStatus) {
        TenantDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        document.setStatus(newStatus);
        return mapToDto(documentRepository.save(document));
    }

    @Override
    public byte[] downloadDocument(Long documentId) throws Exception {
        TenantDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        return fileStorageService.loadFileAndDecrypt(document.getFilePath());
    }

    @Override
    public TenantDocument getDocumentById(Long documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));
    }

    private TenantDocumentResponseDto mapToDto(TenantDocument doc) {
        TenantDocumentResponseDto dto = new TenantDocumentResponseDto();
        dto.setId(doc.getId());
        dto.setDocumentType(doc.getDocumentType().name());
        dto.setStatus(doc.getStatus().name());
        dto.setUploadedAt(doc.getUploadedAt());
        return dto;
    }
}
