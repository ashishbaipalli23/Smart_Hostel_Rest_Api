package com.hostel.controller;


import com.hostel.service.impl.TenantAllocationService;
import com.hostel.service.impl.UserService;
import com.hostel.web.request.AllocateTenantRequest;
import com.hostel.web.request.UserRegistrationRequest;
import com.hostel.web.response.APIResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    private final TenantAllocationService tenantAllocationService;

    @PostMapping("/register")
    public ResponseEntity<APIResponse<String>> createStaffAndTalents(@Valid @RequestBody UserRegistrationRequest request) {
        String responseMsg = userService.registerUser(request);
        APIResponse<String> apiResponse = APIResponse.<String>builder()
                .timeStamp(LocalDateTime.now())
                .msg("User Saved !")
                .data(responseMsg)
                .status(HttpStatus.CREATED.value())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED.value()).body(apiResponse);
    }

    @PostMapping("/allocate")
    public ResponseEntity<String> allocateTenant(@RequestBody @Valid AllocateTenantRequest request) {
        return ResponseEntity.ok(tenantAllocationService.allocateTenant(request));
    }
}
