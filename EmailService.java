package com.maxblox.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Value("${app.url}")
    private String appUrl;
    
    private final JavaMailSender mailSender;
    
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    
    public void sendVerificationEmail(String toEmail, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Подтверждение регистрации в MaxBlox");
        message.setText("Добро пожаловать в MaxBlox!\n\n" +
                       "Для подтверждения вашего аккаунта перейдите по ссылке:\n" +
                       appUrl + "/api/users/verify?code=" + verificationCode + "\n\n" +
                       "Ссылка действительна 24 часа.\n\n" +
                       "Если вы не регистрировались в MaxBlox, проигнорируйте это письмо.");
        
        mailSender.send(message);
    }
}