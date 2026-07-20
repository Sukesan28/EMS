package com.example.ems.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final JavaMailSender mailSender;

    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async("mailExecutor")
    public void sendEmail(String to, String subject, String body) {
        log.info("Sending async email to: {}, subject: {}", to, subject);
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom("no-reply@globaltech.com");
            mailSender.send(message);
            log.info("Async email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            log.info("[EMAIL SIMULATION] To: {}\nSubject: {}\nBody:\n{}", to, subject, body);
        }
    }

    @Async("mailExecutor")
    public void sendEmployeeAccountEmail(String email, String name, String code, String tempPassword) {
        String subject = "GlobalTech Solutions - Employee Account Created";
        String body = String.format("Hello %s,\n\n" +
                "Your Employee Account has been created.\n\n" +
                "Employee Code: %s\n" +
                "Temporary Password: %s\n\n" +
                "Please login and change your password.", name, code, tempPassword);
        sendEmail(email, subject, body);
    }

    @Async("mailExecutor")
    public void sendPasswordResetEmail(String email, String name, String tempPassword) {
        String subject = "GlobalTech Solutions - Password Reset";
        String body = String.format("Hello %s,\n\n" +
                "Your password has been reset by IT Support.\n" +
                "Temporary Password: %s\n\n" +
                "Please login and change your password.", name, tempPassword);
        sendEmail(email, subject, body);
    }
}
