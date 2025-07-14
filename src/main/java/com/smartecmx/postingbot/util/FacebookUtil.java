package com.smartecmx.postingbot.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
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

    public String postFacebookFeed (String message, String link) throws FacebookException {
        RestTemplate rest = new RestTemplate();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("link", link);
        params.add("message", message);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<FacebookResponse> response = rest.postForEntity(facebookPostUrl, request, FacebookResponse.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new FacebookException("Failed to post to Facebook: " + response.getBody());
        }
        System.out.println("Post to Facebook successful: " + response.getBody());
        return response.getBody().getId();
    }
}
