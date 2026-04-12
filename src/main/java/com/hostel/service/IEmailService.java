package com.hostel.service;


import com.hostel.web.request.UserRegistrationRequest;

public interface IEmailService {
    void sendUserRegistrationEmail(UserRegistrationRequest user, String plainPassword);
    void sendAdminNotificationEmail(UserRegistrationRequest user);
}
