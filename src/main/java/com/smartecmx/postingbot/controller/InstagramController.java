package com.smartecmx.postingbot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartecmx.postingbot.exception.PostingBotException;
import com.smartecmx.postingbot.service.InstagramService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/app/postingBot/v1/instagram")
@RequiredArgsConstructor
public class InstagramController {
    
    private final InstagramService instagramService;

    @PostMapping("/postMeme")
    public ResponseEntity<String> postToInstagram() throws PostingBotException {
            log.info("Initiating post to Instagram");
            String postId = instagramService.postMeme();
            log.info("Meme posted to Instagram successfully, ID: " + postId);
            return ResponseEntity.ok("Meme posted to Instagram successfully, ID: " + postId);
    }
}
