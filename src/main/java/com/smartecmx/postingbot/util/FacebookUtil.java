package com.smartecmx.postingbot.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.smartecmx.postingbot.exception.FacebookException;
import com.smartecmx.postingbot.exception.PostingBotException;
import com.smartecmx.postingbot.model.Token;
import com.smartecmx.postingbot.model.Responses.FacebookPostResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FacebookUtil {
    @Value("${com.smartecmx.postingbot.util.facebook.facebook_post_url}")
    private String facebookPostUrl;

    private final TokenUtil tokenUtil;

    public String postFacebookFeed (String message, String pictureUrl) throws PostingBotException {
        RestTemplate rest = new RestTemplate();

        Token token = tokenUtil.getActiveTokenByType("page");
        byte[] imageBytes = rest.getForObject(pictureUrl, byte[].class);

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("source", new ByteArrayResource(imageBytes) {
            @Override
            public String getFilename() { return "meme.jpg"; }
        });
        params.add("message", message);
        params.add("access_token", token.getValue());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);

        ResponseEntity<FacebookPostResponse> response = rest.postForEntity(facebookPostUrl, request, FacebookPostResponse.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new FacebookException("Failed to post to Facebook: " + response.getBody());
        }
        return response.getBody().getId();
    }

}
