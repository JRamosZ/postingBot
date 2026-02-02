package com.smartecmx.postingbot.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
import com.smartecmx.postingbot.model.Responses.FacebookVideoPostResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FacebookUtil {
    @Value("${com.smartecmx.postingbot.util.facebook.facebook_post_url}")
    private String facebookPostUrl;

    @Value("${com.smartecmx.postingbot.util.facebook.facebook_video_post_url}")
    private String facebookVideoPostUrl;

    @Value("${com.smartecmx.postingbot.util.facebook.facebook_reels_post_url}")
    private String facebookReelsPostUrl;

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

    public String postFacebookReel(String message, Path videoPath) throws PostingBotException, IOException {
        RestTemplate rest = new RestTemplate();

        Token token = tokenUtil.getActiveTokenByType("page");

        MultiValueMap<String, Object> initBody = new LinkedMultiValueMap<>();
        initBody.add("upload_phase", "start");
        initBody.add("access_token", token.getValue());

        ResponseEntity<Map> initResp = rest.postForEntity(facebookReelsPostUrl, new HttpEntity<>(initBody), Map.class);

        String videoId = initResp.getBody().get("video_id").toString();
        String uploadUrl = initResp.getBody().get("upload_url").toString();

        byte[] fileBytes = Files.readAllBytes(videoPath);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/octet-stream");
        headers.set("offset", "0");
        headers.set("file_size", String.valueOf(fileBytes.length));
        headers.set("Authorization", "OAuth " + token.getValue());

        HttpEntity<byte[]> transferEntity = new HttpEntity<>(fileBytes, headers);

        rest.exchange(uploadUrl, HttpMethod.POST, transferEntity, String.class);

        MultiValueMap<String, Object> finishBody = new LinkedMultiValueMap<>();
        finishBody.add("upload_phase", "finish");
        finishBody.add("video_id", videoId);
        finishBody.add("access_token", token.getValue());
        finishBody.add("title", "Mi Reel");
        finishBody.add("description", message);
        finishBody.add("video_state", "PUBLISHED");

        ResponseEntity<FacebookVideoPostResponse> finishResp = rest.postForEntity(facebookReelsPostUrl, new HttpEntity<>(finishBody), FacebookVideoPostResponse.class);
        if (finishResp.getStatusCode() != HttpStatus.OK) {
            throw new FacebookException("Failed to post Reel to Facebook: " + finishResp.getBody());
        }

        return finishResp.getBody().getPost_id();
    }
}
