package com.hostel.service.impl;

import com.hostel.enums.Roles;
import com.hostel.web.request.TenantProfileUpdateDto;
import com.hostel.web.response.TenantDocumentResponseDto;
import com.hostel.models.UserEntity;
import com.hostel.models.TenantDocument;
import com.hostel.enums.DocumentStatus;
import com.hostel.enums.DocumentType;
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
    public List<UserEntity> getAllTenants() {
        List<UserEntity> tenants = userRepository.findByRole(Roles.TENANT);
        if (tenants.isEmpty()) {
            return userRepository.findAll();
        }
        return tenants;
    }

    @Override
    public UserEntity getTenantById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant not found with ID: " + id));
    }

    @Override
    @Transactional
    public UserEntity updateProfile(Long tenantId, TenantProfileUpdateDto dto) {
        log.info("Updating profile for tenant ID: {}", tenantId);
        UserEntity user = getTenantById(tenantId);

        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getPhoneNumber() != null) user.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getAddress() != null) user.setAddress(dto.getAddress());
        if (dto.getCity() != null) user.setCity(dto.getCity());
        if (dto.getState() != null) user.setState(dto.getState());
        if (dto.getPincode() != null) user.setPincode(dto.getPincode());
        if (dto.getAadhaarNumber() != null) user.setAadhaarNumber(dto.getAadhaarNumber());

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public TenantDocumentResponseDto uploadDocument(Long tenantId, String documentType, MultipartFile file) throws Exception {
        log.info("Initiating upload logic for tenant ID: {}, documentType: {}", tenantId, documentType);
        
        UserEntity tenant = getTenantById(tenantId);

        DocumentType type;
        String docTypeStr = documentType != null ? documentType.toUpperCase() : "AADHAR";
        if (docTypeStr.contains("AADHAR")) {
            type = DocumentType.AADHAR;
        } else if (docTypeStr.contains("PAN")) {
            type = DocumentType.PAN;
        } else if (docTypeStr.contains("LICENSE")) {
            type = DocumentType.DRIVING_LICENSE;
        } else if (docTypeStr.contains("PASSPORT")) {
            type = DocumentType.PASSPORT;
        } else {
            try {
                type = DocumentType.valueOf(docTypeStr);
            } catch (Exception e) {
                type = DocumentType.OTHER;
            }
        }

        String savedFilename = fileStorageService.storeFile(file, tenantId);

        TenantDocument document = new TenantDocument();
        document.setTenant(tenant);
        document.setFilePath(savedFilename);
        document.setDocumentType(type);
        document.setStatus(DocumentStatus.PENDING);

        TenantDocument savedDoc = documentRepository.save(document);
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
