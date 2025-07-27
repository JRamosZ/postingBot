package com.smartecmx.postingbot.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.smartecmx.postingbot.exception.InstagramException;
import com.smartecmx.postingbot.exception.PostingBotException;
import com.smartecmx.postingbot.model.Token;
import com.smartecmx.postingbot.model.Responses.InstagramCreateContainerResponse;
import com.smartecmx.postingbot.model.Responses.InstagramPostContainerResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InstagramUtil {

    @Value("${com.smartecmx.postingbot.util.instagram.create_container_url}")
    private String createContainerUrl;

    @Value("${com.smartecmx.postingbot.util.instagram.publish_container_url}")
    private String publishContainerUrl;

    private final TokenUtil tokenUtil;
    
    public InstagramCreateContainerResponse createContainer(String imageUrl, String postHeader) throws PostingBotException{
        RestTemplate rest = new RestTemplate();

        Token token = tokenUtil.getActiveTokenByType("page");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("image_url", imageUrl);
        body.add("caption", postHeader);
        body.add("access_token", token.getValue());
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<InstagramCreateContainerResponse> response = rest.postForEntity(createContainerUrl, request, InstagramCreateContainerResponse.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new InstagramException("Failed to post to Instagram: " + response.getBody());
        }

        return response.getBody();
    }

    public InstagramPostContainerResponse postContainer(String containerId) throws PostingBotException{
        RestTemplate rest = new RestTemplate();

        Token token = tokenUtil.getActiveTokenByType("page");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("creation_id", containerId);
        body.add("access_token", token.getValue());
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<InstagramPostContainerResponse> response = rest.postForEntity(publishContainerUrl, request, InstagramPostContainerResponse.class);
        
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new InstagramException("Failed to post to Instagram: " + response.getBody());
        }
        return response.getBody();
    }
}
