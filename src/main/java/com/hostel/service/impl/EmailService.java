package com.hostel.service.impl;

import com.hostel.dto.HostelEmailDataDto;
import com.hostel.dto.UserEmailDataDto;
import com.hostel.service.IEmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.Year;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService implements IEmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String adminEmail;

    @Override
    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Simple email sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send simple email to {}: {}", to, e.getMessage());
        }
    }

    @Override
    public void sendUserRegistrationEmail(
            UserEmailDataDto user,
            String plainPassword
    ) {

        Context context = createBaseContext();

        context.setVariable("name", user.name());
        context.setVariable("username", user.username());
        context.setVariable("password", plainPassword);
        context.setVariable("role", user.role());

        sendHtmlMail(
                user.username(),
                "Registration Successful - Smart Hostel System",
                "emails/registration-user",
                context
        );
    }

    @Override
    public void sendAdminNotificationEmail(UserEmailDataDto user) {

        Context context = createBaseContext();

        context.setVariable("name", user.name());
        context.setVariable("username", user.username());
        context.setVariable("phone", user.phoneNumber());
        context.setVariable("aadhaar", user.aadhaarNumber());

        context.setVariable(
                "address",
                user.address() + ", " +
                        user.city() + ", " +
                        user.state() + " - " +
                        user.pinCode()
        );

        context.setVariable("role", user.role());
        context.setVariable("joiningDate", user.joiningDate());

        sendHtmlMail(
                adminEmail,
                "New User Registered Successfully - Smart Hostel System",
                "emails/registration-admin",
                context
        );
    }

    @Override
    public void sendHostelCreationMail(HostelEmailDataDto dto) {

        Context context = createBaseContext();

        context.setVariable("code", dto.code());
        context.setVariable("name", dto.name());
        context.setVariable("address", dto.address());
        context.setVariable("city", dto.city());
        context.setVariable("state", dto.state());
        context.setVariable("pinCode", dto.pinCode());
        context.setVariable("genderType", dto.genderType());
        context.setVariable("totalFloors", dto.totalFloors());

        sendHtmlMail(
                adminEmail,
                "New Hostel Created - Smart Hostel System",
                "emails/register-hostel-admin",
                context
        );
    }

    /**
     * Common reusable mail sender method
     */
    private void sendHtmlMail(
            String to,
            String subject,
            String template,
            Context context
    ) {

        try {

            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);

            String htmlContent =
                    templateEngine.process(template, context);

            helper.setText(htmlContent, true);

            mailSender.send(message);

            log.info("Email sent successfully to {}", to);

        } catch (MessagingException e) {

            log.error(
                    "Failed to send email to {} : {}",
                    to,
                    e.getMessage(),
                    e
            );
        }
    }

    /**
     * Common reusable context creator
     */
    private Context createBaseContext() {

        Context context = new Context();

        context.setVariable(
                "currentYear",
                Year.now().getValue()
        );

        return context;
    }
}