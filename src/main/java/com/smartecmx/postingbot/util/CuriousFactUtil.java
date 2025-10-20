package com.smartecmx.postingbot.util;

import java.io.File;
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
        if (curiousFacts.size() < 5 && curiousFacts.size() > 0) {
            emailService.sendRunningOutOfCuriousFactsEmail("Facebook");
        } else if (curiousFacts.size() == 0) {
            emailService.sendRanOutOfCuriousFactsToPostEmail("Facebook");
            throw new NotFoundException("No Curious Fact found for Facebook");
        }
        return curiousFacts.get((int) (Math.random() * curiousFacts.size()));
    }

    public CuriousFact getCuriousFactForInstagram() throws PostingBotException{
        List<CuriousFact> curiousFact = curiousFactRepository.findAllByPublishedAtInstagramIsNull();
        if (curiousFact.size() < 5 && curiousFact.size() > 0) {
            emailService.sendRunningOutOfCuriousFactsEmail("Instagram");
        } else if (curiousFact.size() == 0) {
            emailService.sendRanOutOfCuriousFactsToPostEmail("Instagram");
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
        curiousFact.setUpdatedAt(CommonMethod.getCurrentDateTime());
        curiousFactRepository.save(curiousFact);
    }

    public void deleteDirectoryRecursively(File curiousFactFolder) {
        if (curiousFactFolder != null && curiousFactFolder.exists()) {
            File[] files = curiousFactFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectoryRecursively(file);
                    } else {
                        file.delete();
                    }
                }
            }
            curiousFactFolder.delete();
        }
    }
}
