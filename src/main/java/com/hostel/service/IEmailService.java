package com.hostel.service;

import com.hostel.models.UserEntity;

public interface IEmailService {
    void sendUserRegistrationEmail(UserEntity user, String plainPassword);
    void sendAdminNotificationEmail(UserEntity user);
}
