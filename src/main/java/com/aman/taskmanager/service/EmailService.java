package com.aman.taskmanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.aman.taskmanager.dto.EmailRequest;

import jakarta.mail.internet.MimeMessage;

@Component
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String mailFrom;

    public void sendEmail(EmailRequest emailRequest) throws Exception {

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);

        messageHelper.setFrom(mailFrom, "E-Notes");
        messageHelper.setTo(emailRequest.getTo());
        messageHelper.setSubject(emailRequest.getSubject());
        messageHelper.setText(emailRequest.getBody(), true);

        mailSender.send(mimeMessage);
    }
}
