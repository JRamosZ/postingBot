package com.smartecmx.postingbot.util;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.smartecmx.postingbot.common.CommonMethod;
import com.smartecmx.postingbot.exception.NotFoundException;
import com.smartecmx.postingbot.exception.PostingBotException;
import com.smartecmx.postingbot.model.TechnicalTip;
import com.smartecmx.postingbot.repository.TechnicalTipRepository;
import com.smartecmx.postingbot.service.EmailService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class TechnicalTipUtil {
    private final TechnicalTipRepository technicalTipRepository;
    private final EmailService emailService;

    public TechnicalTip getTechnicalTipForFacebook() throws PostingBotException {
        List<TechnicalTip> technicalTips = technicalTipRepository.findAllByPublishedAtFacebookIsNull();
        if (technicalTips.size() < 5 && technicalTips.size() > 0) {
            emailService.sendRunningOutOfTechnicalTipsEmail("Facebook");
        } else if (technicalTips.size() == 0) {
            emailService.sendRanOutOfTechnicalTipsToPostEmail("Facebook");
            throw new NotFoundException("No Technical Tips found for Facebook");
        }
        return technicalTips.get((int) (Math.random() * technicalTips.size()));
    }

    public TechnicalTip getTechnicalTipForInstagram() throws PostingBotException{
        List<TechnicalTip> technicalTip = technicalTipRepository.findAllByPublishedAtInstagramIsNull();
        if (technicalTip.size() < 5 && technicalTip.size() > 0) {
            emailService.sendRunningOutOfTechnicalTipsEmail("Instagram");
        } else if (technicalTip.size() == 0) {
            emailService.sendRanOutOfTechnicalTipsToPostEmail("Instagram");
            throw new NotFoundException("No Technical Tips found for Instagram");
        }
        return technicalTip.get((int) (Math.random() * technicalTip.size()));
    }

    public void updateTechnicalTipPublished(String platform, UUID technicalTipId) throws PostingBotException {
        TechnicalTip technicalTip = technicalTipRepository.findById(technicalTipId).orElseThrow(() -> new NotFoundException("Technical Tip not found with ID: " + technicalTipId));
        if (platform == "Facebook") {
            technicalTip.setPublishedAtFacebook(CommonMethod.getCurrentDateTime());
        } else if (platform == "Instagram") {
            technicalTip.setPublishedAtInstagram(CommonMethod.getCurrentDateTime());
        }
        technicalTip.setUpdatedAt(CommonMethod.getCurrentDateTime());
        technicalTipRepository.save(technicalTip);
    }

}
