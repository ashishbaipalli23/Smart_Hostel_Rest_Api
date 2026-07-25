package com.hostel.service;

import com.hostel.models.Payment;
import com.hostel.web.request.CreatePaymentRequest;
import java.util.List;

public interface IPaymentService {
    Payment recordPayment(CreatePaymentRequest request);
    List<Payment> getAllPayments();
    List<Payment> getPendingPayments();
    List<Payment> getTenantPayments(Long tenantId);
    String sendRentReminder(Long tenantId, String month);
}
