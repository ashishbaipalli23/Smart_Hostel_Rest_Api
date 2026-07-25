package com.hostel.listener;

import com.hostel.dto.HostelEmailDataDto;
import com.hostel.events.HostelCreatedEvent;
import com.hostel.service.IEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class HostelEventListener {

    private final IEmailService emailService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handelHostelCreated(HostelCreatedEvent event) {

        log.info("Processing Creation of hostel ... : {} ",event.code());

        try {
            //EMAIL DTO
            HostelEmailDataDto emailDataDto = HostelEmailDataDto.builder()
                    .state(event.state())
                    .genderType(event.genderType())
                    .name(event.name())
                    .totalFloors(event.totalFloors())
                    .pinCode(event.pinCode())
                    .city(event.city())
                    .address(event.address()).code(event.code())
                    .build();

            emailService.sendHostelCreationMail(emailDataDto);


            log.info("Emails sent successfully for Admin");

        } catch (Exception ex) {
            log.error("Error sending emails Admin", ex);
        }
    }

}



