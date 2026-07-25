package com.hostel.controller;

import com.hostel.web.request.DocumentVerificationRequestDto;
import com.hostel.web.request.TenantProfileUpdateDto;
import com.hostel.web.response.APIResponse;
import com.hostel.web.response.TenantDocumentResponseDto;
import com.hostel.models.TenantDocument;
import com.hostel.models.UserEntity;
import com.hostel.service.ITenantService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class TenantController {

    private final ITenantService tenantService;

    public TenantController(ITenantService tenantService) {
        this.tenantService = tenantService;
    }

    @GetMapping("/tenants")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<APIResponse<List<UserEntity>>> getAllTenants() {
        List<UserEntity> tenants = tenantService.getAllTenants();
        APIResponse<List<UserEntity>> apiResponse = APIResponse.<List<UserEntity>>builder()
                .timeStamp(LocalDateTime.now())
                .msg("Tenants fetched successfully")
                .data(tenants)
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/tenants/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ROLE_STAFF', 'ROLE_TENANT')")
    public ResponseEntity<APIResponse<UserEntity>> getTenantById(@PathVariable Long id) {
        UserEntity tenant = tenantService.getTenantById(id);
        APIResponse<UserEntity> apiResponse = APIResponse.<UserEntity>builder()
                .timeStamp(LocalDateTime.now())
                .msg("Tenant details")
                .data(tenant)
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/tenants/{id}/profile")
    @PreAuthorize("hasAnyRole('ROLE_TENANT', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<UserEntity> updateProfile(
            @PathVariable Long id,
            @RequestBody TenantProfileUpdateDto dto) {
        return ResponseEntity.ok(tenantService.updateProfile(id, dto));
    }

    @PostMapping(value = "/tenants/{id}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_TENANT', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<TenantDocumentResponseDto> uploadDocument(
            @PathVariable Long id,
            @RequestParam("documentType") String documentType,
            @RequestParam("file") MultipartFile file) {

        try {
            TenantDocumentResponseDto response = tenantService.uploadDocument(id, documentType, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/tenants/{id}/documents")
    @PreAuthorize("hasAnyRole('ROLE_TENANT', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<List<TenantDocumentResponseDto>> getTenantDocuments(@PathVariable Long id) {
        return ResponseEntity.ok(tenantService.getTenantDocuments(id));
    }

    @GetMapping("/documents/{id}")
    @PreAuthorize("hasAnyRole('ROLE_TENANT', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long id) {
        try {
            TenantDocument fileMetadata = tenantService.getDocumentById(id);
            byte[] decryptedData = tenantService.downloadDocument(id);

            String fileName = fileMetadata.getFilePath();
            String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            if (fileName.endsWith(".pdf.enc")) contentType = MediaType.APPLICATION_PDF_VALUE;
            else if (fileName.endsWith(".jpeg.enc") || fileName.endsWith(".jpg.enc")) contentType = MediaType.IMAGE_JPEG_VALUE;
            else if (fileName.endsWith(".png.enc")) contentType = MediaType.IMAGE_PNG_VALUE;

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName.replace(".enc", "") + "\"")
                    .body(decryptedData);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/documents/{id}/status")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<TenantDocumentResponseDto> verifyDocument(
            @PathVariable Long id,
            @RequestBody DocumentVerificationRequestDto requestDto) {
        TenantDocumentResponseDto response = tenantService.verifyDocument(id, requestDto.getStatus());
        return ResponseEntity.ok(response);
    }
}
