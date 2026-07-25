package com.hostel.service;

import com.hostel.dto.HostelEmailDataDto;
import com.hostel.dto.UserEmailDataDto;

public interface IEmailService {

    void sendUserRegistrationEmail(UserEmailDataDto user, String plainPassword);

    void sendAdminNotificationEmail(UserEmailDataDto user);

    void sendHostelCreationMail(HostelEmailDataDto dto);

    void sendEmail(String to, String subject, String body);
}
