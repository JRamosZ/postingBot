package com.smartecmx.postingbot.util;

import java.time.LocalDate;

import java.util.List;

import org.springframework.stereotype.Component;

import com.smartecmx.postingbot.model.Token;
import com.smartecmx.postingbot.repository.TokenRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class TokenUtil {

    private final TokenRepository tokenRepository;

    public void saveToken(String tokenValue, String tokenType, LocalDate expiresAt) {
        
        List <Token> currentTokens = tokenRepository.findAllByTypeAndActive(tokenType, true);
        if (!currentTokens.isEmpty()) {
            for (Token token : currentTokens) {
                token.setActive(false);
                tokenRepository.save(token);
            }
        }
        Token newToken = new Token();
        newToken.setValue(tokenValue);
        newToken.setType(tokenType);
        newToken.setExpiresAt(expiresAt);
        tokenRepository.save(newToken);
    }

}
