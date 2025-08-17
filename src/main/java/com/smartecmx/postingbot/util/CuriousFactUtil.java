package com.smartecmx.postingbot.util;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.smartecmx.postingbot.common.CommonMethod;
import com.smartecmx.postingbot.exception.NotFoundException;
import com.smartecmx.postingbot.exception.PostingBotException;
import com.smartecmx.postingbot.model.CuriousFact;
import com.smartecmx.postingbot.repository.CuriousFactRepository;
import com.smartecmx.postingbot.service.EmailService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CuriousFactUtil {
    private final CuriousFactRepository curiousFactRepository;
    private final EmailService emailService;

    public CuriousFact getCuriousFactForFacebook() throws PostingBotException {
        List<CuriousFact> curiousFacts = curiousFactRepository.findAllByPublishedAtFacebookIsNull();
        if (curiousFacts.size() < 5) {
            // emailService.sendRunningOutOfMemesEmail("Facebook");
        } else if (curiousFacts.isEmpty()) {
            // emailService.sendRanOutOfMemesToPostEmail("Facebook");
            throw new NotFoundException("No Curious Fact found for Facebook");
        }
        return curiousFacts.get((int) (Math.random() * curiousFacts.size()));
    }

    public CuriousFact getCuriousFactForInstagram() throws PostingBotException{
        List<CuriousFact> curiousFact = curiousFactRepository.findAllByPublishedAtInstagramIsNull();
        if (curiousFact.size() < 5) {
            // emailService.sendRunningOutOfMemesEmail("Instagram");
        } else if (curiousFact.isEmpty()) {
            // emailService.sendRanOutOfMemesToPostEmail("Instagram");
            throw new NotFoundException("No Curious Fact found for Instagram");
        }
        return curiousFact.get((int) (Math.random() * curiousFact.size()));
    }

    public void updateCuriousFactPublished(String platform, UUID curiousFactId) throws PostingBotException {
        CuriousFact curiousFact = curiousFactRepository.findById(curiousFactId).orElseThrow(() -> new NotFoundException("Curious Fact not found with ID: " + curiousFactId));
        if (platform == "Facebook") {
            curiousFact.setPublishedAtFacebook(CommonMethod.getCurrentDateTime());
        } else if (platform == "Instagram") {
            curiousFact.setPublishedAtInstagram(CommonMethod.getCurrentDateTime());
        }
        curiousFactRepository.save(curiousFact);
    }
}
