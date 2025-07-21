package com.smartecmx.postingbot.util;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.smartecmx.postingbot.exception.NotFoundException;
import com.smartecmx.postingbot.exception.PostingBotException;
import com.smartecmx.postingbot.model.Meme;
import com.smartecmx.postingbot.repository.MemeRepository;
import com.smartecmx.postingbot.service.EmailService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class MemeUtil {
    
    private final MemeRepository memeRepository;
    private final EmailService emailService;

    public Meme getMemeForFacebook() throws PostingBotException {
        List<Meme> memes = memeRepository.findAllByPublishedAtFacebookIsNull();
        if (memes.size() < 5) {
            emailService.sendRunningOutOfMemesEmail();
        } else if (memes.size() < 1) {
            throw new NotFoundException("No memes found for Facebook");
        } else if (memes.isEmpty()) {
            emailService.sendRanOutOfMemesToPostEmail();
            throw new NotFoundException("No memes found for Facebook");
        }
        return memes.get((int) (Math.random() * memes.size()));
    }

    public Meme getMemeForInstagram() throws PostingBotException{
        List<Meme> memes = memeRepository.findAllByPublishedAtInstagramIsNull();
        if (memes.isEmpty()) {
            throw new NotFoundException("No memes found for Instagram");
        }
        return memes.get((int) (Math.random() * memes.size()));
    }

    public void updateMemePublishedAtFacebook(UUID memeId, LocalDateTime publishedAt) throws PostingBotException {
        Meme meme = memeRepository.findById(memeId).orElseThrow(() -> new NotFoundException("Meme not found with ID: " + memeId));
        meme.setPublishedAtFacebook(publishedAt);
        memeRepository.save(meme);
    }

}
