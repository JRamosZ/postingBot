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
import org.springframework.web.util.UriComponentsBuilder;

import com.smartecmx.postingbot.exception.InstagramException;
import com.smartecmx.postingbot.exception.PostingBotException;
import com.smartecmx.postingbot.model.Token;
import com.smartecmx.postingbot.model.Responses.ContainerStatusResponse;
import com.smartecmx.postingbot.model.Responses.InstagramCreateContainerResponse;
import com.smartecmx.postingbot.model.Responses.InstagramPostContainerResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class InstagramUtil {

    @Value("${com.smartecmx.postingbot.util.instagram.create_container_url}")
    private String createContainerUrl;

    @Value("${com.smartecmx.postingbot.util.instagram.publish_container_url}")
    private String publishContainerUrl;

    @Value("${com.smartecmx.postingbot.util.meta.base_api_url}")
    private String baseApiUrl;

    private final TokenUtil tokenUtil;
    
    public InstagramCreateContainerResponse createContainerForImage(String imageUrl, String postHeader) throws PostingBotException{
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

    public InstagramCreateContainerResponse createContainerForReel(String videoUrl, String postHeader) throws PostingBotException{
        RestTemplate rest = new RestTemplate();

        Token token = tokenUtil.getActiveTokenByType("page");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("media_type", "REELS");
        body.add("video_url", videoUrl);
        body.add("caption", postHeader);
        body.add("thumbnail_url", videoUrl);
        body.add("access_token", token.getValue());
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<InstagramCreateContainerResponse> response = rest.postForEntity(createContainerUrl, request, InstagramCreateContainerResponse.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new InstagramException("Failed to post to Instagram: " + response.getBody());
        }
        log.info("Container created for Instagram Reel with ID: " + response.getBody().getId());

        return response.getBody();
    }

    public void validateContainerAvailability(String containerId) throws PostingBotException, InterruptedException {
        RestTemplate restTemplate = new RestTemplate();
        Token token = tokenUtil.getActiveTokenByType("page");
        String url = baseApiUrl + "/" + containerId;

        int retries = 10;
        int delayMs = 10000;

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
            .queryParam("fields", "status_code")
            .queryParam("access_token", token.getValue());


        for (int i = 0; i < retries; i++) {
            ResponseEntity<ContainerStatusResponse> statusResp = restTemplate.getForEntity(builder.build().toUriString(), ContainerStatusResponse.class);
            String status = statusResp.getBody().getStatus_code();
            log.info("Try #" + (i + 1) + ": Container status is " + status);
            if ("FINISHED".equals(status)) {
                break;
            } else if ("ERROR".equals(status)) {
                throw new InstagramException("Error processing container: " + statusResp.getBody());
            }

            if (i == retries - 1) {
                throw new InstagramException("Container processing timed out after " + retries + " attempts. Container ID: " + containerId);
            }

            Thread.sleep(delayMs);
        }
    }
}
