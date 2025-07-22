package com.smartecmx.postingbot.service;

import org.springframework.stereotype.Service;

import com.smartecmx.postingbot.util.EmailUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailUtil emailUtil;

    public void sendUserTokenExpirationEmail() {
        String subject = "Facebook User Token Expiration Notification";
        String bodyText = String.format("The Facebook user token is no longer valid. Please generate a new one from the Graph API Explorer to restore the auto-posting bot.");
        emailUtil.sendBasicNotificationEmail(subject, bodyText);
    }

    public void sendUserTokenExpiresTodayEmail() {
        String subject = "Facebook User Token Expiration Warning";
        String bodyText = String.format("The Facebook User Token expires today, refresh it inmediately, automatic post will be disabled for safety!. Please generate a new one from the Graph API Explorer to ensure the auto-posting bot continues to function.");
        emailUtil.sendBasicNotificationEmail(subject, bodyText);
    }

    public void sendUserTokenExpirationInTimeframeEmail(int daysUntilExpiration) {
        String subject = "Facebook User Token Expiration Warning";
        String bodyText = String.format("The Facebook User Token expires in %d days, please generate a new one from the Graph API Explorer to ensure the auto-posting bot continues to function.", daysUntilExpiration);
        emailUtil.sendBasicNotificationEmail(subject, bodyText);
    }

    public void sendRunningOutOfMemesEmail(String platform) {
        String subject = String.format("Running Out of Memes for %s", platform);
        String bodyText = String.format("The bot is running out of memes to post on %s. Please add more memes to the database to ensure continuous posting.", platform);
        emailUtil.sendBasicNotificationEmail(subject, bodyText);
    }

    public void sendRanOutOfMemesToPostEmail(String platform) {
        String subject = String.format("Ran Out of Memes to Post on %s", platform);
        String bodyText = String.format("The bot has run out of memes to post on %s. Please add more memes to the database to ensure continuous posting.", platform);
        emailUtil.sendBasicNotificationEmail(subject, bodyText);
    }

    public void sendNoActiveTokenEmail(String tokenType) {
        String subject = "No Active Token Found";
        String bodyText = String.format("No active token found for type: %s. Please ensure a valid token is available for the bot to function properly.", tokenType);
        emailUtil.sendBasicNotificationEmail(subject, bodyText);
    }

    public void sendFacebookPostErrorEmail(String errorMessage) {
        String subject = "Error Posting to Facebook";
        String bodyText = String.format("An error occurred while trying to post to Facebook: %s. Please check the bot's configuration and ensure it is functioning correctly.", errorMessage);
        emailUtil.sendBasicNotificationEmail(subject, bodyText);
    }

    public void sendInstagramPostErrorEmail(String errorMessage) {
        String subject = "Error Posting to Instagram";
        String bodyText = String.format("An error occurred while trying to post to Instagram: %s. Please check the bot's configuration and ensure it is functioning correctly.", errorMessage);
        emailUtil.sendBasicNotificationEmail(subject, bodyText);
    }
    
}
