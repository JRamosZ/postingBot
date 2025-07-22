package com.smartecmx.postingbot.service;

import org.springframework.stereotype.Service;

import com.smartecmx.postingbot.exception.PostingBotException;
import com.smartecmx.postingbot.model.Meme;
import com.smartecmx.postingbot.model.Responses.InstagramCreateContainerResponse;
import com.smartecmx.postingbot.model.Responses.InstagramPostContainerResponse;
import com.smartecmx.postingbot.util.ImgflipUtil;
import com.smartecmx.postingbot.util.InstagramUtil;
import com.smartecmx.postingbot.util.MemeUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InstagramService {
    
    private final MemeUtil memeUtil;
    private final ImgflipUtil imgflipUtil;
    private final InstagramUtil instagramUtil;
    private final EmailService emailService;

    public String postMeme() throws PostingBotException {
        try {
            Meme memeToPublish = memeUtil.getMemeForInstagram();
            Object memeUrl = imgflipUtil.createMeme(memeToPublish.getTemplateId(), memeToPublish.getMemeTexts());
            InstagramCreateContainerResponse containerId = instagramUtil.createContainer((String) memeUrl, memeToPublish.getPostHeader());
            InstagramPostContainerResponse postId = instagramUtil.postContainer(containerId.getId());
            memeUtil.updateMemePublished("Instagram", memeToPublish.getId());
            return postId.getId();
        } catch (Exception e) {
            emailService.sendInstagramPostErrorEmail(e.getMessage());
            throw new PostingBotException("Failed to post meme to Instagram: " + e.getMessage());
        }
    }
}
