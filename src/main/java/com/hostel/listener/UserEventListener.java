package com.hostel.listener;

import com.hostel.dto.UserEmailDataDto;
import com.hostel.events.UserCreatedEvent;
import com.hostel.service.IEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Slf4j
@Component
public class UserEventListener {

    private final IEmailService emailService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserCreated(UserCreatedEvent event) {

        log.info("Processing UserCreatedEvent for user = {}", event.username());

        try {
            // Build Email DTO
            UserEmailDataDto emailDto = UserEmailDataDto.builder()
                    .name(event.name())
                    .username(event.username())
                    .phoneNumber(event.phoneNumber())
                    .aadhaarNumber(event.aadhaarNumber())
                    .address(event.address())
                    .city(event.city())
                    .state(event.state())
                    .pinCode(event.pinCode())
                    .role(event.role())
                    .joiningDate(event.joiningDate())
                    .build();

            // Send emails
            emailService.sendUserRegistrationEmail(emailDto, event.rawPassword());
            emailService.sendAdminNotificationEmail(emailDto);

            log.info("Emails sent successfully for user ={}", event.username());

        } catch (Exception ex) {
            log.error("Error sending emails for userId={}", event.username(), ex);
        }
    }
}
