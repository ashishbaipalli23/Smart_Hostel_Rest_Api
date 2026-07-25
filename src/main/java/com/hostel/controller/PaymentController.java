package com.hostel.controller;

import com.hostel.models.Payment;
import com.hostel.service.IPaymentService;
import com.hostel.web.request.CreatePaymentRequest;
import com.hostel.web.response.APIResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final IPaymentService paymentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TENANT')")
    public ResponseEntity<APIResponse<Payment>> recordPayment(@Valid @RequestBody CreatePaymentRequest request) {
        Payment payment = paymentService.recordPayment(request);
        APIResponse<Payment> apiResponse = APIResponse.<Payment>builder()
                .timeStamp(LocalDateTime.now())
                .msg("Payment recorded successfully")
                .data(payment)
                .status(HttpStatus.CREATED.value())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<APIResponse<List<Payment>>> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        APIResponse<List<Payment>> apiResponse = APIResponse.<List<Payment>>builder()
                .timeStamp(LocalDateTime.now())
                .msg("Payments fetched successfully")
                .data(payments)
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<APIResponse<List<Payment>>> getPendingPayments() {
        List<Payment> pending = paymentService.getPendingPayments();
        APIResponse<List<Payment>> apiResponse = APIResponse.<List<Payment>>builder()
                .timeStamp(LocalDateTime.now())
                .msg("Pending payments list")
                .data(pending)
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/remind")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<APIResponse<String>> sendReminder(
            @RequestParam(required = false) Long tenantId,
            @RequestParam(required = false) String month,
            @RequestBody(required = false) Map<String, Object> body) {

        Long finalTenantId = tenantId;
        String finalMonth = month;

        if (body != null) {
            if (finalTenantId == null && body.get("tenantId") != null) {
                try {
                    finalTenantId = Long.parseLong(body.get("tenantId").toString());
                } catch (Exception ignored) {}
            }
            if (finalMonth == null && body.get("month") != null) {
                finalMonth = body.get("month").toString();
            }
        }

        if (finalTenantId == null) {
            finalTenantId = 1L;
        }
        if (finalMonth == null || finalMonth.trim().isEmpty()) {
            finalMonth = "Current Month";
        }

        String msg = paymentService.sendRentReminder(finalTenantId, finalMonth);
        APIResponse<String> apiResponse = APIResponse.<String>builder()
                .timeStamp(LocalDateTime.now())
                .msg(msg)
                .data(msg)
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
