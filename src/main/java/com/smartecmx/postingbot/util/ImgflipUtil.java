package com.smartecmx.postingbot.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.smartecmx.postingbot.exception.ImgflipException;
import com.smartecmx.postingbot.model.Responses.ImgflipResponse;

import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor
public class ImgflipUtil {

    @Value("${com.smartecmx.postingbot.util.imgflip.imgflip_url}")
    private String imgflipUrl;

    @Value("${com.smartecmx.postingbot.util.imgflip.imgflip_username}")
    private String imgflipUsername;

    @Value("${com.smartecmx.postingbot.util.imgflip.imgflip_password}")
    private String imgflipPassword;
    
    public ByteArrayResource createMeme(String templateId, String[] texts) throws ImgflipException {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("template_id", templateId);
        formData.add("username", imgflipUsername);
        formData.add("password", imgflipPassword);
        for (int i = 0; i < texts.length; i++) {
            formData.add("boxes["+i+"][text]", texts[i]);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

        ResponseEntity<ImgflipResponse> response = restTemplate.postForEntity(imgflipUrl, request, ImgflipResponse.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new ImgflipException("Failed to create meme: " + response.getStatusCode());
        }
        
        byte[] imageBytes = restTemplate.getForObject(response.getBody().getData().getUrl(), byte[].class);
        ByteArrayResource meme = new ByteArrayResource(imageBytes) {
            @Override
            public String getFilename() {
                return "meme.jpg";
            }
        };
        return meme;
    }

}
