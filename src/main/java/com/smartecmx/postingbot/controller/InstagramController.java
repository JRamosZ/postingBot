package com.smartecmx.postingbot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartecmx.postingbot.exception.PostingBotException;
import com.smartecmx.postingbot.service.InstagramService;
import com.smartecmx.postingbot.util.InstagramUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/app/postingBot/v1/instagram")
@RequiredArgsConstructor
public class InstagramController {
    
    private final InstagramService instagramService;
    private final InstagramUtil instagramUtil;
    
    @Scheduled(cron = "0 30 12 * * 5", zone = "America/Mexico_City")
    @PostMapping("/postMeme")
    public ResponseEntity<String> postToInstagram() throws PostingBotException {
            log.info("Initiating post to Instagram");
            String postId = instagramService.postMeme();
            log.info("Meme posted to Instagram successfully, ID: " + postId);
            return ResponseEntity.ok("Meme posted to Instagram successfully, ID: " + postId);
    }

    // @Scheduled(cron = "0 0 12 * * 2", zone = "America/Mexico_City")
    @Scheduled(cron = "00 54 00 * * 1", zone = "America/Mexico_City")
    @PostMapping("/postCuriousFact")
    public ResponseEntity<String> postCuriousFact() throws PostingBotException {
        log.info("Initiating post of curious fact to Facebook");
        String postId = instagramService.postCuriousFact();
        log.info("Curious fact posted to Instagram successfully, ID: " + postId);
        return ResponseEntity.ok("Curious fact posted to Instagram successfully, ID: " + postId);
    }

    @PostMapping("/postContainer/{containerId}")
    public ResponseEntity<String> postContainer(@PathVariable("containerId") String containerId) throws PostingBotException {
        log.info("Initiating post of container to Instagram");
        String postId = instagramUtil.postContainer(containerId).getId();
        log.info("Post of curious fact to Instagram completed successfully with ID: " + postId);
        return ResponseEntity.ok("Post of curious fact to Instagram completed successfully with ID: " + postId);
    }
}