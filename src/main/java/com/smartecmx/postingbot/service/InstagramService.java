package com.smartecmx.postingbot.service;

import java.io.File;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.smartecmx.postingbot.exception.PostingBotException;
import com.smartecmx.postingbot.model.CuriousFact;
import com.smartecmx.postingbot.model.Meme;
import com.smartecmx.postingbot.model.Responses.GoogleTtsResponse;
import com.smartecmx.postingbot.model.Responses.InstagramCreateContainerResponse;
import com.smartecmx.postingbot.model.Responses.InstagramPostContainerResponse;
import com.smartecmx.postingbot.util.CuriousFactUtil;
import com.smartecmx.postingbot.util.ImgflipUtil;
import com.smartecmx.postingbot.util.InstagramUtil;
import com.smartecmx.postingbot.util.MemeUtil;
import com.smartecmx.postingbot.util.TextToSpeechUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstagramService {
    
    private final MemeUtil memeUtil;
    private final ImgflipUtil imgflipUtil;
    private final InstagramUtil instagramUtil;
    private final EmailService emailService;
    private final CloudinaryService cloudinaryService;
    private final TextToSpeechUtil textToSpeechUtil;
    private final FFMpegService ffmpegService;
    private final CuriousFactUtil curiousFactUtil;

    @Value("${com.smartecmx.postingbot.util.cloudinary.background_pics_folder}")
    private String backgroundPicsFolder;

    @Value("${com.smartecmx.postingbot.util.cloudinary.background_songs_folder}")
    private String backgroundSongsFolder;

    private static final String CURIOUS_FACTS_FOLDER = "files/curiousFacts";

    public String postMeme() throws PostingBotException {
        try {
            Meme memeToPublish = memeUtil.getMemeForInstagram();
            String memeUrl = imgflipUtil.createMeme(memeToPublish.getTemplateId(), memeToPublish.getMemeTexts());
            String modifiedMemeUrl = cloudinaryService.uploadAndTransformMemeFromUrl(memeUrl, "meme_instagram_"+ memeToPublish.getId().toString());
            InstagramCreateContainerResponse containerId = instagramUtil.createContainerForImage(modifiedMemeUrl, memeToPublish.getPostHeader());
            instagramUtil.validateContainerAvailability(containerId.getId());
            log.info("Container validated successfully, producing post");
            InstagramPostContainerResponse postId = instagramUtil.postContainer(containerId.getId());
            memeUtil.updateMemePublished("Instagram", memeToPublish.getId());
            return postId.getId();
        } catch (Exception e) {
            emailService.sendInstagramPostErrorEmail(e.getMessage());
            throw new PostingBotException("Failed to post meme to Instagram: " + e.getMessage());
        }
    }

        public String postCuriousFact() throws PostingBotException {
        try {
            CuriousFact curiousFact = curiousFactUtil.getCuriousFactForFacebook();

            String curiousFactFolderPath = CURIOUS_FACTS_FOLDER + "/" + curiousFact.getId();
            File curiousFactFolder = new File(curiousFactFolderPath);
            if (!curiousFactFolder.exists()) {
                GoogleTtsResponse ttsResponse = textToSpeechUtil.getGoogleTtsResponse(curiousFact.getFactText());
                textToSpeechUtil.generateSpeechFile(ttsResponse.getAudioContent(), curiousFactFolderPath, "speech_" + curiousFact.getId() + ".mp3");
                textToSpeechUtil.generateSrtFromTimepoints(ttsResponse.getTimepoints(), curiousFact.getFactText(), curiousFactFolderPath, "subtitles_" + curiousFact.getId() + ".srt");
                cloudinaryService.downloadRandomItemFromFolder(backgroundPicsFolder, curiousFactFolderPath, "backgroundImage_" + curiousFact.getId() + ".jpg");
                cloudinaryService.downloadRandomItemFromFolder(backgroundSongsFolder, curiousFactFolderPath, "backgroundMusic_" + curiousFact.getId() + ".mp3");
                ffmpegService.generateVideo(curiousFactFolderPath, "finalVideo_" + curiousFact.getId() + ".mp4");
            }
            String videoUrl = cloudinaryService.uploadVideo(Path.of(curiousFactFolderPath + "/finalVideo_" + curiousFact.getId() + ".mp4"));
            InstagramCreateContainerResponse containerId = instagramUtil.createContainerForReel(videoUrl, curiousFact.getPostHeader());
            instagramUtil.validateContainerAvailability(containerId.getId());
            log.info("Container validated successfully, producing post");
            InstagramPostContainerResponse postId = instagramUtil.postContainer(containerId.getId());
            curiousFactUtil.updateCuriousFactPublished("Instagram", curiousFact.getId());
            return postId.getId();
        } catch (Exception e) {
            emailService.sendInstagramPostErrorEmail(e.getMessage());
            throw new PostingBotException("Failed to post curious fact to Instagram: " + e.getMessage());
        }
    }
}
