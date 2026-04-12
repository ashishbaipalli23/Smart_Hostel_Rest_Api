package com.hostel.controller;

import com.hostel.web.request.DocumentVerificationRequestDto;
import com.hostel.web.request.TenantProfileUpdateDto;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@PreAuthorize("hasAnyRole('ROLE_TENANT', 'ROLE_ADMIN')")
public class TenantController {

    private final ITenantService tenantService;

    public TenantController(ITenantService tenantService) {
        this.tenantService = tenantService;
    }

    // --- Profile Operations ---

    @PutMapping("/tenants/{id}/profile")
    public ResponseEntity<UserEntity> updateProfile(
            @PathVariable Long id,
            @RequestBody TenantProfileUpdateDto dto) {
        return ResponseEntity.ok(tenantService.updateProfile(id, dto));
    }

    // --- Document Operations ---

    @PostMapping(value = "/tenants/{id}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_TENANT')")
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
    public ResponseEntity<List<TenantDocumentResponseDto>> getTenantDocuments(@PathVariable Long id) {
        return ResponseEntity.ok(tenantService.getTenantDocuments(id));
    }

    @GetMapping("/documents/{id}")
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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<TenantDocumentResponseDto> verifyDocument(
            @PathVariable Long id,
            @RequestBody DocumentVerificationRequestDto requestDto) {
        TenantDocumentResponseDto response = tenantService.verifyDocument(id, requestDto.getStatus());
        return ResponseEntity.ok(response);
    }
}
