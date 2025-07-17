package com.smartecmx.postingbot.service;

import java.time.LocalDate;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import com.smartecmx.postingbot.exception.FacebookException;
import com.smartecmx.postingbot.exception.PostingBotException;
import com.smartecmx.postingbot.model.Meme;
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
    private static final Integer DEFAULT_TOKEN_DURATION_DAYS = 60; 

    public String postMemeToFacebook() throws PostingBotException {
        try {
            Meme memeToPublish = memeUtil.getMemeForFacebook();
            ByteArrayResource memeUrl = imgflipUtil.createMeme(memeToPublish.getTemplateId(), memeToPublish.getMemeTexts());
            String postId = facebookUtil.postFacebookFeed(memeToPublish.getPostHeader(), memeUrl);
            return postId;
        } catch (Exception e) {
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
            tokenUtil.saveToken(longUserToken.getAccess_token(), "user", LocalDate.now().plusDays(tokenDuration));
            
            FacebookPageLongTokenResponse longPageToken = facebookUtil.getPageLongLifeToken(longUserToken.getAccess_token());
            tokenUtil.saveToken(longPageToken.getData().get(0).getAccess_token(), "page", LocalDate.now().plusDays(tokenDuration));
        } catch (Exception e) {
            throw new FacebookException("Failed to fetch new page long token: " + e.getMessage());
        }
    }

}
