package com.smartecmx.postingbot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartecmx.postingbot.exception.PostingBotException;
import com.smartecmx.postingbot.service.MetaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/app/postingBot/v1/meta")
public class MetaController {
    
    private final MetaService metaService;
    
    @PostMapping("/getNewPageLongToken/{userShortToken}")
    public ResponseEntity<String> getNewPageLongToken(@PathVariable("userShortToken") String userShortToken) throws PostingBotException {
        log.info("Fetching new page long token for posting");
        metaService.getNewPageLongToken(userShortToken);
        log.info("New page long token fetched successfully");
        return ResponseEntity.ok("New long page token fetched successfully");
    }

    @Scheduled(cron = "0 0 */12 * * *", zone = "America/Mexico_City")
    @GetMapping("/tokenStatus")
    public ResponseEntity<String> getTokenStatus() throws PostingBotException {
        log.info("Getting token status for posting");
        String tokenStatus = metaService.getTokenStatus();
        log.info("Token status fetched successfully: " + tokenStatus);
        return ResponseEntity.ok("Token status fetched successfully: " + tokenStatus);
    }
}
