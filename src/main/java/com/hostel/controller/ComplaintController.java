package com.hostel.controller;

import com.hostel.models.Complaint;
import com.hostel.models.UserEntity;
import com.hostel.repository.UserRepository;
import com.hostel.service.IComplaintService;
import com.hostel.web.request.CreateComplaintRequest;
import com.hostel.web.request.UpdateComplaintStatusRequest;
import com.hostel.web.response.APIResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final IComplaintService complaintService;
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_TENANT', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<APIResponse<Complaint>> createComplaint(
            @RequestBody(required = false) CreateComplaintRequest request,
            Authentication authentication) {

        final CreateComplaintRequest finalRequest = request != null ? request : new CreateComplaintRequest();

        if (finalRequest.getTenantId() == null && authentication != null) {
            String username = authentication.getName();
            Optional<UserEntity> userOpt = userRepository.findByUsername(username);
            if (userOpt.isPresent()) {
                finalRequest.setTenantId(userOpt.get().getId());
            }
        }

        if (finalRequest.getTenantId() == null) {
            finalRequest.setTenantId(1L);
        }
        if (finalRequest.getCategory() == null || finalRequest.getCategory().trim().isEmpty()) {
            finalRequest.setCategory("Plumbing");
        }
        if (finalRequest.getTitle() == null || finalRequest.getTitle().trim().isEmpty()) {
            finalRequest.setTitle("Maintenance Request");
        }
        if (finalRequest.getDescription() == null || finalRequest.getDescription().trim().isEmpty()) {
            finalRequest.setDescription("Facility maintenance issue reported.");
        }

        Complaint complaint = complaintService.createComplaint(finalRequest);
        APIResponse<Complaint> apiResponse = APIResponse.<Complaint>builder()
                .timeStamp(LocalDateTime.now())
                .msg("Maintenance complaint created successfully")
                .data(complaint)
                .status(HttpStatus.CREATED.value())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF', 'ROLE_TENANT', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<APIResponse<List<Complaint>>> getAllComplaints() {
        List<Complaint> complaints = complaintService.getAllComplaints();
        APIResponse<List<Complaint>> apiResponse = APIResponse.<List<Complaint>>builder()
                .timeStamp(LocalDateTime.now())
                .msg("Complaints fetched")
                .data(complaints)
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<APIResponse<Complaint>> assignStaff(
            @PathVariable Long id,
            @RequestParam Long staffId) {
        Complaint complaint = complaintService.assignStaff(id, staffId);
        APIResponse<Complaint> apiResponse = APIResponse.<Complaint>builder()
                .timeStamp(LocalDateTime.now())
                .msg("Staff assigned to complaint")
                .data(complaint)
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/{id}/update-status")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<APIResponse<Complaint>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateComplaintStatusRequest request,
            Authentication authentication) {
        String updatedBy = authentication != null ? authentication.getName() : "Staff";
        Complaint complaint = complaintService.updateStatus(id, request, updatedBy);
        APIResponse<Complaint> apiResponse = APIResponse.<Complaint>builder()
                .timeStamp(LocalDateTime.now())
                .msg("Complaint status updated to " + request.getStatus())
                .data(complaint)
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
