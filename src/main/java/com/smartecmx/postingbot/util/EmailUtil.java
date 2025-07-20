package com.smartecmx.postingbot.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class EmailUtil {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${com.smartecmx.postingbot.util.mail.notifications_inbox}")
    private String notificationsInbox;

    @Value("${com.smartecmx.postingbot.util.mail.notifications_sender}")
    private String notificationsSender;

    public void sendUserTokenExpirationEmail() throws IOException, MessagingException {
        String subject = "⚠️ Facebook User Token Expiration Notification";
        String bodyText = String.format("The Facebook user token is no longer valid. Please generate a new one from the Graph API Explorer to restore the auto-posting bot.");
        sendBasicNotificationEmail(subject, bodyText);
    }

    public void sendUserTokenExpiresTodayEmail() throws IOException, MessagingException {
        String subject = "⚠️ Facebook User Token Expiration Warning";
        String bodyText = String.format("The Facebook User Token expires today, refresh it inmediately, automatic post will be disabled for safety!. Please generate a new one from the Graph API Explorer to ensure the auto-posting bot continues to function.");
        sendBasicNotificationEmail(subject, bodyText);
    }

    public void sendUserTokenExpirationInTimeframeEmail(int daysUntilExpiration) throws IOException, MessagingException {
        String subject = "⚠️ Facebook User Token Expiration Warning";
        String bodyText = String.format("The Facebook User Token expires in %d days, please generate a new one from the Graph API Explorer to ensure the auto-posting bot continues to function.", daysUntilExpiration);
        sendBasicNotificationEmail(subject, bodyText);
    }

    public void sendRunningOutOfMemesEmail() throws IOException, MessagingException {
        String subject = "⚠️ Running Out of Memes for Facebook";
        String bodyText = "The bot is running out of memes to post on Facebook. Please add more memes to the database to ensure continuous posting.";
        sendBasicNotificationEmail(subject, bodyText);
    }

    public void sendRanOutOfMemesToPostEmail() throws IOException, MessagingException {
        String subject = "⚠️ Ran Out of Memes to Post on Facebook";
        String bodyText = "The bot has run out of memes to post on Facebook. Please add more memes to the database to ensure continuous posting.";
        sendBasicNotificationEmail(subject, bodyText);
    }

    public void sendNoActiveTokenEmail(String tokenType) throws IOException, MessagingException {
        String subject = "⚠️ No Active Token Found";
        String bodyText = String.format("No active token found for type: %s. Please ensure a valid token is available for the bot to function properly.", tokenType);
        sendBasicNotificationEmail(subject, bodyText);
    }

    public void sendFacebookPostErrorEmail(String errorMessage) throws IOException, MessagingException {
        String subject = "⚠️ Error Posting to Facebook";
        String bodyText = String.format("An error occurred while trying to post to Facebook: %s. Please check the bot's configuration and ensure it is functioning correctly.", errorMessage);
        sendBasicNotificationEmail(subject, bodyText);
    }

    private void sendBasicNotificationEmail(String subject, String bodyText) throws IOException, MessagingException {
        String htmlTemplate = loadTemplate("../templates/BaseNoficationEmail.html");
        String htmlBody = htmlTemplate
        .replace("{{subject}}", subject)
        .replace("{{bodyText}}", bodyText);

        sendHtmlEmail(subject, htmlBody);
    }

    private String loadTemplate(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    private void sendHtmlEmail(String subject, String htmlBody) throws IOException, MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(notificationsInbox);
        helper.setSubject(subject);
        helper.setFrom(notificationsSender);
        helper.setText(htmlBody, true);

        mailSender.send(message);
    }
}
