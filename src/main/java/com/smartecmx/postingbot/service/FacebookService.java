package com.smartecmx.postingbot.service;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.smartecmx.postingbot.common.OverlayConfig;
import com.smartecmx.postingbot.exception.PostingBotException;
import com.smartecmx.postingbot.model.CuriousFact;
import com.smartecmx.postingbot.model.Meme;
import com.smartecmx.postingbot.model.TechnicalTip;
import com.smartecmx.postingbot.model.Responses.GoogleTtsResponse;
import com.smartecmx.postingbot.util.CuriousFactUtil;
import com.smartecmx.postingbot.util.FacebookUtil;
import com.smartecmx.postingbot.util.ImgflipUtil;
import com.smartecmx.postingbot.util.MemeUtil;
import com.smartecmx.postingbot.util.TechnicalTipUtil;
import com.smartecmx.postingbot.util.TextToSpeechUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FacebookService {
    
    @Value("${com.smartecmx.postingbot.util.cloudinary.background_pics_folder}")
    private String backgroundPicsFolder;

    @Value("${com.smartecmx.postingbot.util.cloudinary.background_songs_folder}")
    private String backgroundSongsFolder;

    @Value("${com.smartecmx.postingbot.util.cloudinary.technical_tips_folder}")
    private String technicalTipsFolder;

    private static final String CURIOUS_FACTS_FOLDER = "files/curiousFacts";

    private final MemeUtil memeUtil;
    private final CuriousFactUtil curiousFactUtil;
    private final TechnicalTipUtil technicalTipUtil;
    private final ImgflipUtil imgflipUtil;
    private final FacebookUtil facebookUtil;
    private final TextToSpeechUtil textToSpeechUtil;
    private final EmailService emailService;
    private final CloudinaryService cloudinaryService;
    private final FFMpegService ffmpegService;

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

    public String postCuriousFactToFacebook() throws PostingBotException {
        try {
            CuriousFact curiousFact = curiousFactUtil.getCuriousFactForFacebook();

            String curiousFactFolderPath = CURIOUS_FACTS_FOLDER + "/" + curiousFact.getId();
            File curiousFactFolder = new File(curiousFactFolderPath);

            GoogleTtsResponse ttsResponse = textToSpeechUtil.getGoogleTtsResponse(curiousFact.getFactText());
            textToSpeechUtil.generateSpeechFile(ttsResponse.getAudioContent(), curiousFactFolderPath, "speech_" + curiousFact.getId() + ".mp3");
            textToSpeechUtil.generateSrtFromTimepoints(ttsResponse.getTimepoints(), curiousFact.getFactText(), curiousFactFolderPath, "subtitles_" + curiousFact.getId() + ".srt");
            cloudinaryService.downloadRandomItemFromFolder(backgroundPicsFolder, curiousFactFolderPath, "backgroundImage_" + curiousFact.getId() + ".jpg");
            cloudinaryService.downloadRandomItemFromFolder(backgroundSongsFolder, curiousFactFolderPath, "backgroundMusic_" + curiousFact.getId() + ".mp3");
            ffmpegService.generateVideo(curiousFactFolderPath, "finalVideo_" + curiousFact.getId() + ".mp4");

            String postId = facebookUtil.postFacebookReel(curiousFact.getPostHeader(), Path.of(curiousFactFolderPath + "/finalVideo_" + curiousFact.getId() + ".mp4"));
            curiousFactUtil.updateCuriousFactPublished("Facebook", curiousFact.getId());
            curiousFactUtil.deleteDirectoryRecursively(curiousFactFolder);
            return postId;
        } catch (Exception e) {
            emailService.sendFacebookPostErrorEmail(e.getMessage());
            throw new PostingBotException("Failed to post curious fact to Facebook: " + e.getMessage());
        }
    
    }

    public String postTechnicalTipToFacebook() throws PostingBotException {
        try {
            TechnicalTip technicalTip = technicalTipUtil.getTechnicalTipForFacebook();
             Map<String,String> technicalTipImage = cloudinaryService.getRandomItemFromFolder(technicalTipsFolder);
            // String technicalTipName = technicalTipImage.get("filename");
            String technicalTipName = "TechnicalTip_1_xayege";
            Optional<OverlayConfig> overlayConfigEnum = OverlayConfig.fromFilename(technicalTipName);
            //TODO Get CAT Overlay Config
            if (overlayConfigEnum.isEmpty()) {
                throw new PostingBotException("No overlay configuration found for technical tip image: " + technicalTipName);
            }

            String finalImageUrl = cloudinaryService.generateTechnicalTipUrl(technicalTipName, technicalTip.getTipText(), overlayConfigEnum.get(), technicalTip.getCtaText(), overlayConfigEnum.get());
            //TODO Post to Facebook
            return finalImageUrl;
        } catch (Exception e) {
            emailService.sendFacebookPostErrorEmail(e.getMessage());
            throw new PostingBotException("Failed to post technical tip to Facebook: " + e.getMessage());
        }
    }

}
