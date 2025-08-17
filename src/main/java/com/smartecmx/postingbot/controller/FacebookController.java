package com.smartecmx.postingbot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartecmx.postingbot.exception.PostingBotException;
import com.smartecmx.postingbot.service.FacebookService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/app/postingBot/v1/facebook")
@RequiredArgsConstructor
public class FacebookController {

    private final FacebookService facebookService;

    @Scheduled(cron = "0 0 18 * * 5", zone = "America/Mexico_City")
    @PostMapping("/postMeme")
    public ResponseEntity<String> postToFacebook() throws PostingBotException {
            log.info("Initiating post to Facebook");
            String postId = facebookService.postMemeToFacebook();
            log.info("Post to Facebook completed successfully");
            return ResponseEntity.ok("Meme posted to Facebook successfully, ID: " + postId);
    }

    @PostMapping("/postCuriousFact")
    public ResponseEntity<String> postCuriousFact() throws PostingBotException {
        log.info("Initiating post of curious fact to Facebook");
        String postId = facebookService.postCuriousFactToFacebook();
        log.info("Post of curious fact to Facebook completed successfully");
        return ResponseEntity.ok("Curious fact posted to Facebook successfully, ID: " + postId);
    }
}