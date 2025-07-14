package com.smartecmx.postingbot.util;

import java.util.List;

import org.springframework.stereotype.Component;

import com.smartecmx.postingbot.exception.NotFoundException;
import com.smartecmx.postingbot.exception.PostingBotException;
import com.smartecmx.postingbot.model.Meme;
import com.smartecmx.postingbot.repository.MemeRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class MemeUtil {
    
    private MemeRepository memeRepository;

    public Meme getMemeForFacebook() throws PostingBotException{
        List<Meme> memes = memeRepository.findAllByPublishedAtFacebookIsNull();
        if (memes.isEmpty()) {
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

}
