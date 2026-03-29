package com.hostel.service;

import com.hostel.models.UserEntity;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService implements IEmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String adminEmail;

    @Override
    @Async
    public void sendUserRegistrationEmail(UserEntity user, String plainPassword) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(user.getUsername());
            helper.setSubject("Registration Successful - Smart Hostel System");

            Context context = new Context();
            context.setVariable("name", user.getName());
            context.setVariable("username", user.getUsername());
            context.setVariable("password", plainPassword);
            context.setVariable("role", user.getRole().name());

            String htmlContent = templateEngine.process("emails/registration-user", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Registration email sent successfully to User: {}", user.getUsername());

        } catch (MessagingException e) {
            log.error("Failed to send registration email to {}: {}", user.getUsername(), e.getMessage());
        }
    }

    @Override
    @Async
    public void sendAdminNotificationEmail(UserEntity user) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // admin email is configured in application properties
            helper.setTo(adminEmail);
            helper.setSubject("New User Registered Successfully - Smart Hostel System");

            Context context = new Context();
            context.setVariable("name", user.getName());
            context.setVariable("username", user.getUsername());
            context.setVariable("phone", user.getPhoneNumber());
            context.setVariable("aadhaar", user.getAadhaarNumber());
            context.setVariable("address", user.getAddress() + ", " + user.getCity() + ", " + user.getState() + " - " + user.getPincode());
            context.setVariable("role", user.getRole().name());
            context.setVariable("joiningDate", user.getJoiningDate());

            String htmlContent = templateEngine.process("emails/registration-admin", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Admin notification email sent successfully for User: {}", user.getUsername());

        } catch (MessagingException e) {
            log.error("Failed to send admin notification email for {}: {}", user.getUsername(), e.getMessage());
        }
    }
}
