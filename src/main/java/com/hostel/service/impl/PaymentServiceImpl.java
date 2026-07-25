package com.hostel.service.impl;

import com.hostel.exceptions.ResourceNotFoundException;
import com.hostel.models.Payment;
import com.hostel.models.UserEntity;
import com.hostel.repository.PaymentRepository;
import com.hostel.repository.UserRepository;
import com.hostel.service.IEmailService;
import com.hostel.service.IPaymentService;
import com.hostel.web.request.CreatePaymentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements IPaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final IEmailService emailService;

    @Override
    public Payment recordPayment(CreatePaymentRequest request) {
        UserEntity tenant = userRepository.findById(request.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with ID: " + request.getTenantId()));

        Payment payment = Payment.builder()
                .tenant(tenant)
                .amount(request.getAmount())
                .month(request.getMonth())
                .paymentDate(LocalDate.now())
                .paymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "UPI")
                .status("PAID")
                .transactionRef("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .build();

        return paymentRepository.save(payment);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public List<Payment> getPendingPayments() {
        return paymentRepository.findByStatus("PENDING");
    }

    @Override
    public List<Payment> getTenantPayments(Long tenantId) {
        return paymentRepository.findByTenantId(tenantId);
    }

    @Override
    public String sendRentReminder(Long tenantId, String month) {
        UserEntity tenant = userRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        String subject = "Rent Reminder for " + month;
        String body = "Dear " + tenant.getUsername() + ",\n\nYour rent for " + month + " is pending. Please complete the payment at your earliest convenience.\n\nSmart Hostel Management";
        
        try {
            emailService.sendEmail(tenant.getEmail(), subject, body);
            return "Email reminder sent successfully to " + tenant.getEmail();
        } catch (Exception e) {
            return "Failed to send email: " + e.getMessage();
        }
    }
}
