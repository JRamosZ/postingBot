package com.smartecmx.postingbot.util;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.smartecmx.postingbot.exception.MetaException;
import com.smartecmx.postingbot.exception.NotFoundException;
import com.smartecmx.postingbot.exception.PostingBotException;
import com.smartecmx.postingbot.model.Token;
import com.smartecmx.postingbot.model.Responses.MetaDebugTokenResponse;
import com.smartecmx.postingbot.repository.TokenRepository;
import com.smartecmx.postingbot.service.EmailService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class TokenUtil {

    private final TokenRepository tokenRepository;
    private final EmailService emailService;

    @Value("${com.smartecmx.postingbot.util.meta.debug_token_url}")
    private String debugTokenUrl;
    
    @Value("${com.smartecmx.postingbot.util.meta.app_id}")
    private String appId;

    @Value("${com.smartecmx.postingbot.util.meta.app_secret}")
    private String appSecret;

    public void saveToken(String tokenValue, String tokenType, LocalDate expiresAt) {
        Token currentToken = tokenRepository.findByTypeAndActive(tokenType, true);
        if (currentToken != null) {
            currentToken.setActive(false);
            tokenRepository.save(currentToken);
        }
        Token newToken = new Token();
        newToken.setValue(tokenValue);
        newToken.setType(tokenType);
        newToken.setExpiresAt(expiresAt);
        tokenRepository.save(newToken);
    }

    public Token getActiveTokenByType(String tokenType) throws PostingBotException {
        Token token = tokenRepository.findByTypeAndActive(tokenType, true);
        if (token == null) {
            emailService.sendNoActiveTokenEmail(tokenType);
            throw new NotFoundException("No active token found for type: " + tokenType);
        }
        return token;
        
    }

    public MetaDebugTokenResponse debugToken(String tokenValue) throws PostingBotException {
        RestTemplate restTemplate = new RestTemplate();
        
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(debugTokenUrl)
            .queryParam("input_token", tokenValue)
            .queryParam("access_token", appId + "|" + appSecret);

        ResponseEntity<MetaDebugTokenResponse> response = restTemplate.getForEntity(uriBuilder.toUriString(), MetaDebugTokenResponse.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new MetaException("Failed to debug token: " + response.getBody());
        }
        
        return response.getBody();
    }

    public void deactivateToken(String tokenValue) {
        Token token = tokenRepository.findByValue(tokenValue);
        if (token != null) {
            token.setActive(false);
            tokenRepository.save(token);
        }
    }

}
