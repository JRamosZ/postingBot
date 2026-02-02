package com.smartecmx.postingbot.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EmailUtil {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${com.smartecmx.postingbot.util.mail.notifications_receiver}")
    private String notificationsReceiver;

    @Value("${com.smartecmx.postingbot.util.mail.notifications_receiver_cc}")
    private String notificationsReceiverCc;

    @Value("${spring.mail.username}")
    private String notificationsSender;

    @Value("${com.smartecmx.postingbot.util.mail.send_mail}")
    private String sendMail;

    public void sendBasicNotificationEmail(String subject, String bodyText) {
        try {
            String htmlTemplate = loadTemplate("templates/BaseNotificationEmail.html");
            String htmlBody = htmlTemplate
            .replace("{{subject}}", subject)
            .replace("{{body}}", bodyText);
    
            sendHtmlEmail(subject, htmlBody);
        } catch (IOException e) {
            log.error("Failed to load email template: " + e.getMessage());
        }
    }

    private String loadTemplate(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    private void sendHtmlEmail(String subject, String htmlBody) {
        if (!Boolean.parseBoolean(sendMail)) {
            log.info("Email sending is disabled. Subject: " + subject);
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
    
            helper.setTo(notificationsReceiver);
            helper.setCc(notificationsReceiverCc);
            helper.setSubject("⚠️ " + subject);
            helper.setFrom(notificationsSender, "SmarTec Mx Bot");
            helper.setText(htmlBody, true);
    
            ClassPathResource logo = new ClassPathResource("static/images/SmarTec Mx Logo.png");
            helper.addInline("smartecmxLogo", logo);
    
            mailSender.send(message);
        } catch (MessagingException e) {
            log.info("Failed to send email: " + e.getMessage());
        } catch (UnsupportedEncodingException e){
            log.info("Failed Encoding: " + e.getMessage());
        }
    }
}
