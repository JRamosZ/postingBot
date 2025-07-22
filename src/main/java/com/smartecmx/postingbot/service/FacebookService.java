package com.smartecmx.postingbot.service;

import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;

import com.smartecmx.postingbot.common.CommonMethod;
import com.smartecmx.postingbot.exception.FacebookException;
import com.smartecmx.postingbot.exception.PostingBotException;
import com.smartecmx.postingbot.model.Meme;
import com.smartecmx.postingbot.model.Token;
import com.smartecmx.postingbot.model.Responses.FacebookDebugTokenResponse;
import com.smartecmx.postingbot.model.Responses.FacebookPageLongTokenResponse;
import com.smartecmx.postingbot.model.Responses.FacebookUserLongTokenResponse;
import com.smartecmx.postingbot.util.FacebookUtil;
import com.smartecmx.postingbot.util.ImgflipUtil;
import com.smartecmx.postingbot.util.MemeUtil;
import com.smartecmx.postingbot.util.TokenUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FacebookService {
    
    private final MemeUtil memeUtil;
    private final ImgflipUtil imgflipUtil;
    private final FacebookUtil facebookUtil;
    private final TokenUtil tokenUtil;
    private final EmailService emailService;
    private static final Integer DEFAULT_TOKEN_DURATION_DAYS = 60; 

    public String postMemeToFacebook() throws PostingBotException {
        try {
            Meme memeToPublish = memeUtil.getMemeForFacebook();
            String memeUrl = imgflipUtil.createMeme(memeToPublish.getTemplateId(), memeToPublish.getMemeTexts());
            String postId = facebookUtil.postFacebookFeed(memeToPublish.getPostHeader(), memeUrl);
            memeUtil.updateMemePublished("Facebook", memeToPublish.getId());
            return postId;
        } catch (Exception e) {
            emailService.sendFacebookPostErrorEmail(e.getMessage());
            throw new PostingBotException("Failed to post meme to Facebook: " + e.getMessage());
        }

    }

    public void getNewPageLongToken(String userShortToken) throws PostingBotException {
        try {
            FacebookUserLongTokenResponse longUserToken = facebookUtil.getUserLongLifeToken(userShortToken);
            Integer tokenDuration;
            if (longUserToken.getExpires_in() != null) {
                tokenDuration = (int) (Integer.parseInt(longUserToken.getExpires_in())/86400); //There are 86400 seconds on a day
            } else {
                tokenDuration = DEFAULT_TOKEN_DURATION_DAYS;
            }
            tokenUtil.saveToken(longUserToken.getAccess_token(), "user", CommonMethod.getCurrentDate().plusDays(tokenDuration));
            
            FacebookPageLongTokenResponse longPageToken = facebookUtil.getPageLongLifeToken(longUserToken.getAccess_token());
            tokenUtil.saveToken(longPageToken.getData().get(0).getAccess_token(), "page", CommonMethod.getCurrentDate().plusDays(tokenDuration));
        } catch (Exception e) {
            throw new FacebookException("Failed to fetch new page long token: " + e.getMessage());
        }
    }

    public String getTokenStatus() throws PostingBotException {
        try {
            Token userToken = tokenUtil.getActiveTokenByType("user");
            Token pageToken = tokenUtil.getActiveTokenByType("page");
            
            FacebookDebugTokenResponse userTokenInfo = tokenUtil.debugToken(userToken.getValue());
            FacebookDebugTokenResponse pageTokenInfo = tokenUtil.debugToken(pageToken.getValue());
            
            if (userTokenInfo.getData().getIs_valid() && !pageTokenInfo.getData().getIs_valid()) {
                FacebookPageLongTokenResponse newPageLongToken = facebookUtil.getPageLongLifeToken(userToken.getValue());
                tokenUtil.saveToken(newPageLongToken.getData().get(0).getAccess_token(), "page", userToken.getExpiresAt());
                return "Page token was invalid, a new one has been fetched and saved";
            }
            
            if (!userTokenInfo.getData().getIs_valid()) {
                tokenUtil.deactivateToken(userToken.getValue());
                tokenUtil.deactivateToken(pageToken.getValue());
                emailService.sendUserTokenExpirationEmail();
                return "User token is invalid, refresh it inmediately!!!";
            }
            
            Long daysUntilExpration = ChronoUnit.DAYS.between(CommonMethod.getCurrentDate(), userToken.getExpiresAt());
            if (daysUntilExpration == 0) {
                tokenUtil.deactivateToken(userToken.getValue());
                tokenUtil.deactivateToken(pageToken.getValue());
                emailService.sendUserTokenExpiresTodayEmail();
                return "User token expires today, refresh it inmediately, automatic post will be disabled for safety!";
            } else if (daysUntilExpration <= 3) {
                emailService.sendUserTokenExpirationInTimeframeEmail(daysUntilExpration.intValue());
                return "User token is about to expire in " + daysUntilExpration + " days, refresh it!";
            }

            return "User and Page tokens are valid";

        } catch (Exception e) {
            throw new FacebookException("Failed to get token status: " + e.getMessage());
        }
    }

}
