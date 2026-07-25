package com.hostel.controller;

import com.hostel.models.Visitor;
import com.hostel.service.IVisitorService;
import com.hostel.web.request.RegisterVisitorRequest;
import com.hostel.web.response.APIResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/visitors")
@RequiredArgsConstructor
public class VisitorController {

    private final IVisitorService visitorService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<APIResponse<Visitor>> registerVisitor(@Valid @RequestBody RegisterVisitorRequest request) {
        Visitor visitor = visitorService.registerVisitor(request);
        APIResponse<Visitor> apiResponse = APIResponse.<Visitor>builder()
                .timeStamp(LocalDateTime.now())
                .msg("Visitor registered successfully")
                .data(visitor)
                .status(HttpStatus.CREATED.value())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<APIResponse<List<Visitor>>> getAllVisitors() {
        List<Visitor> visitors = visitorService.getAllVisitors();
        APIResponse<List<Visitor>> apiResponse = APIResponse.<List<Visitor>>builder()
                .timeStamp(LocalDateTime.now())
                .msg("Visitors fetched")
                .data(visitors)
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}/checkout")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<APIResponse<Visitor>> checkoutVisitor(@PathVariable Long id) {
        Visitor visitor = visitorService.checkoutVisitor(id);
        APIResponse<Visitor> apiResponse = APIResponse.<Visitor>builder()
                .timeStamp(LocalDateTime.now())
                .msg("Visitor checked out")
                .data(visitor)
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
