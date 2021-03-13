package com.js.demo.email;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@AllArgsConstructor
@Slf4j
public class EmailService implements EmailSender {

    private final JavaMailSender javaMailSender;

    @Override
    @Async
    public void sendEmail(String address, String emailToSend) {
        try {
            MimeMessage mimMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimHelper = new MimeMessageHelper(mimMessage, "utf-8");
            mimHelper.setText(emailToSend, true);
            mimHelper.setTo(address);
            mimHelper.setSubject("Confirm your email");
            mimHelper.setFrom("me!");
            javaMailSender.send(mimMessage);
        } catch (MessagingException e) {
            log.error("Failed to send email", e);
            throw new IllegalStateException("Failed to send email!");
        }
    }
}
