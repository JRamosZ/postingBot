package com.smartecmx.postingbot.service;

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
    private final EmailService emailService;
    private final CloudinaryService cloudinaryService;

    public String postMemeToFacebook() throws PostingBotException {
        try {
            Meme memeToPublish = memeUtil.getMemeForFacebook();
            String memeUrl = imgflipUtil.createMeme(memeToPublish.getTemplateId(), memeToPublish.getMemeTexts());
            String modifiedMemeUrl = cloudinaryService.uploadAndTransformMemeFromUrl(memeUrl, "meme_facebook_"+ memeToPublish.getId().toString());
            String postId = facebookUtil.postFacebookFeed(memeToPublish.getPostHeader(), modifiedMemeUrl);
            memeUtil.updateMemePublished("Facebook", memeToPublish.getId());
            return postId;
        } catch (Exception e) {
            emailService.sendFacebookPostErrorEmail(e.getMessage());
            throw new PostingBotException("Failed to post meme to Facebook: " + e.getMessage());
        }

    }

}
