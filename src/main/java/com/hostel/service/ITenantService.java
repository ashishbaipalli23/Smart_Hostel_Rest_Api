package com.hostel.service;

import com.hostel.web.request.TenantProfileUpdateDto;
import com.hostel.web.response.TenantDocumentResponseDto;
import com.hostel.models.UserEntity;
import com.hostel.utils.DocumentStatus;
import com.hostel.models.TenantDocument;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service interface for managing Tenant-related operations.
 * Includes profile management and document management features.
 */
public interface ITenantService {

    /**
     * Updates the profile information of a specific tenant.
     *
     * @param tenantId The unique identifier of the tenant.
     * @param dto      The DTO containing updatable profile fields.
     * @return The updated UserEntity representing the tenant.
     */
    UserEntity updateProfile(Long tenantId, TenantProfileUpdateDto dto);

    /**
     * Uploads a document for a specific tenant securely.
     *
     * @param tenantId     The unique identifier of the tenant.
     * @param documentType The type of document being uploaded (e.g., AADHAAR, PAN).
     * @param file         The document file to upload.
     * @return DTO representation of the uploaded document status.
     * @throws Exception If there is an issue during file storage or encryption.
     */
    TenantDocumentResponseDto uploadDocument(Long tenantId, String documentType, MultipartFile file) throws Exception;

    /**
     * Retrieves all documents uploaded by a specific tenant.
     *
     * @param tenantId The unique identifier of the tenant.
     * @return A list of TenantDocumentResponseDto objects representing the tenant's documents.
     */
    List<TenantDocumentResponseDto> getTenantDocuments(Long tenantId);

    /**
     * Admin operation to verify or reject a tenant document.
     *
     * @param documentId The unique identifier of the document.
     * @param newStatus  The new verification status to apply.
     * @return DTO representation of the updated document status.
     */
    TenantDocumentResponseDto verifyDocument(Long documentId, DocumentStatus newStatus);

    /**
     * Downloads and decrypts a specific tenant document.
     *
     * @param documentId The unique identifier of the document.
     * @return The raw byte array payload of the decrypted document file.
     * @throws Exception If there is an issue during file retrieval or decryption.
     */
    byte[] downloadDocument(Long documentId) throws Exception;

    /**
     * Retrieves full metadata for a document by its ID.
     *
     * @param documentId The unique identifier of the document.
     * @return The TenantDocument entity.
     */
    TenantDocument getDocumentById(Long documentId);
}
