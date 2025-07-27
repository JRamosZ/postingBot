package com.smartecmx.postingbot.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.smartecmx.postingbot.exception.MetaException;
import com.smartecmx.postingbot.exception.PostingBotException;
import com.smartecmx.postingbot.model.Responses.MetaPageLongTokenResponse;
import com.smartecmx.postingbot.model.Responses.MetaUserLongTokenResponse;

import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor
public class MetaUtil {

    @Value("${com.smartecmx.postingbot.util.meta.get_personal_long_life_token_url}")
    private String getPersonalLongLifeTokenUrl;

    @Value("${com.smartecmx.postingbot.util.meta.get_page_long_life_token_url}")
    private String getPageLongLifeTokenUrl;

    @Value("${com.smartecmx.postingbot.util.meta.app_id}")
    private String appId;

    @Value("${com.smartecmx.postingbot.util.meta.app_secret}")
    private String appSecret;

    public MetaUserLongTokenResponse getUserLongLifeToken(String userShortToken) throws PostingBotException {
        RestTemplate rest = new RestTemplate();

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getPersonalLongLifeTokenUrl)
            .queryParam("grant_type", "fb_exchange_token")
            .queryParam("client_id", appId)
            .queryParam("client_secret", appSecret)
            .queryParam("fb_exchange_token", userShortToken);
        
        ResponseEntity<MetaUserLongTokenResponse> response = rest.getForEntity(builder.toUriString(), MetaUserLongTokenResponse.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new MetaException("Failed to fetch user long life token: " + response.getBody());
        }
        return response.getBody();
    }

    public MetaPageLongTokenResponse getPageLongLifeToken(String userLongLifeToken) throws PostingBotException {
        RestTemplate rest = new RestTemplate();

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getPageLongLifeTokenUrl)
            .queryParam("access_token", userLongLifeToken);
        
        ResponseEntity<MetaPageLongTokenResponse> response = rest.getForEntity(builder.toUriString(), MetaPageLongTokenResponse.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new MetaException("Failed to fetch page long life token: " + response.getBody());
        }

        return response.getBody();
    }

}
