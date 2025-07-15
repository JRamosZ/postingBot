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
import com.smartecmx.postingbot.model.Responses.FacebookResponse;

import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor
public class FacebookUtil {
    @Value("${com.smartecmx.postingbot.util.facebook.facebook_post_url}")
    private String facebookPostUrl;

    public String postFacebookFeed (String message, ByteArrayResource picture) throws FacebookException {
        RestTemplate rest = new RestTemplate();

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("source", picture);
        params.add("message", message);
        params.add("access_token", "access_token_placeholder"); // Replace with actual access token

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);

        ResponseEntity<FacebookResponse> response = rest.postForEntity(facebookPostUrl, request, FacebookResponse.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new FacebookException("Failed to post to Facebook: " + response.getBody());
        }
        System.out.println("Post to Facebook successful: " + response.getBody());
        return response.getBody().getId();
    }
}
