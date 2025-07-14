package com.smartecmx.postingbot.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import com.smartecmx.postingbot.exception.PostingBotException;
import com.smartecmx.postingbot.model.Meme;
import com.smartecmx.postingbot.util.FacebookUtil;
import com.smartecmx.postingbot.util.ImgflipUtil;
import com.smartecmx.postingbot.util.MemeUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FacebookService {
    
    private final MemeUtil memeUtil;
    private final ImgflipUtil imgflipUtil;
    private final FacebookUtil facebookUtil;

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

}
