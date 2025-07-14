package com.smartecmx.postingbot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/")
    public String getFacebookStatus() {
        log.info("Fetching Facebook status");
        return "Facebook posting bot is running";
    }
    
    @PostMapping("/postMeme")
    public ResponseEntity<String> postToFacebook() throws PostingBotException {
            log.info("Initiating post to Facebook");
            String postId = facebookService.postMemeToFacebook();
            log.info("Post to Facebook completed successfully");
            return ResponseEntity.ok("Meme posted to Facebook successfully, ID: " + postId);

    }
    
}
