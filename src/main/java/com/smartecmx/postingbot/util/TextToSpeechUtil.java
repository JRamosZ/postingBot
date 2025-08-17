package com.smartecmx.postingbot.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.smartecmx.postingbot.common.CommonMethod;
import com.smartecmx.postingbot.exception.GoogleException;
import com.smartecmx.postingbot.exception.PostingBotException;
import com.smartecmx.postingbot.model.Responses.GoogleTtsResponse;
import com.smartecmx.postingbot.model.Responses.GoogleTtsResponse.Timepoint;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TextToSpeechUtil {
    
    @Value("${google.general.apiKey}")
    private String apiKey;
    
    @Value("${google.general.language_code}")
    private String languageCode;

    @Value("${google.general.audio_encoding}")
    private String audioEncoding;

    @Value("${google.tts.url}")
    private String ttsUrl;

    @Value("${google.tts.voice_name}")
    private String voiceName;

    @Value("${google.tts.speaking_rate}")
    private double speakingRate;

    @Value("${google.tts.pitch}")
    private double pitch;


    public GoogleTtsResponse getGoogleTtsResponse(String text) throws PostingBotException, IOException {
        RestTemplate rest = new RestTemplate();

        String url = ttsUrl + "?key=" + apiKey;

        String newText = convertTextToSsml(text);

        Map<String, Object> body = Map.of(
            "input", Map.of("ssml", newText),
            "voice", Map.of(
                "languageCode", languageCode,
                "name", voiceName
            ),
            "audioConfig", Map.of(
                "audioEncoding", audioEncoding,
                "speakingRate", speakingRate,
                "pitch", pitch
            ),
            "enableTimePointing", List.of( "SSML_MARK")
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<GoogleTtsResponse> response = rest.postForEntity(url, request, GoogleTtsResponse.class);

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new GoogleException("Failed to generate audio from text speech: " + response.getStatusCode());
        }

        return response.getBody();
    }

    public void generateSpeechFile(String audioContent, String destinationFolder, String fileName) throws PostingBotException, IOException {
        if (audioContent == null || audioContent.isEmpty()) {
            throw new PostingBotException("Audio content is empty or null");
        }

        byte[] audioBytes = Base64.getDecoder().decode(audioContent);
        CommonMethod.saveFile(audioBytes, destinationFolder, fileName);
        log.info("Speech file saved: " + destinationFolder + "/" + fileName);
    }

    public void generateSrtFromTimepoints(List<Timepoint> timepoints, String originalText, String destinationFolder, String fileName) throws IOException {
        List<String> subtitles = new ArrayList<>();
        int index = 1;
        
        String[] originalWords = originalText.split("\\s+");
        Map<String, String> markToWord = new HashMap<>();
        for (int i = 0; i < timepoints.size() && i < originalWords.length; i++) {
            markToWord.put(timepoints.get(i).getMarkName(), originalWords[i]);
        }

        List<String> wordsBuffer = new ArrayList<>();
        List<Double> timesBuffer = new ArrayList<>();
        double previousBlockEndtime = 0;
        boolean closedForMaxWords = true;

        for (int i = 0; i < timepoints.size(); i++) {
            Timepoint tp = timepoints.get(i);
            String word = markToWord.get(tp.getMarkName());
            if (word == null) {
                word = tp.getMarkName();
            }

            wordsBuffer.add(word);
            timesBuffer.add(tp.getTimeSeconds());

            boolean closeBlock = false;
            double offset = 0;

            if (word.matches(".*[.!?]$")) {
                closeBlock = true;
                offset = 0.6;
            }
            else if (word.matches(".*[,:]$")) {
                closeBlock = true;
                offset = 0.3;
            }
            else if (wordsBuffer.size() >= 5) {
                closeBlock = true;
            }
            else if (i == timepoints.size() - 1) {
                closeBlock = true;
            }

            if (closeBlock) {
                double startTime = closedForMaxWords ? previousBlockEndtime : timesBuffer.get(0) - offset;
                if (startTime < 0) {
                    startTime = 0;
                }

                if (startTime < previousBlockEndtime) {
                    startTime = previousBlockEndtime;
                }

                double endTime = timesBuffer.get(timesBuffer.size() - 1);

                String subtitleText = String.join(" ", wordsBuffer);

                subtitles.add(formatSubtitle(index++, startTime, endTime, subtitleText));

                if (wordsBuffer.size() >= 5) {
                    closedForMaxWords = true;
                } else {
                    closedForMaxWords = false;
                }
                previousBlockEndtime = endTime;
                wordsBuffer.clear();
                timesBuffer.clear();
            }
        }

        CommonMethod.saveFile(String.join("", subtitles).getBytes(), destinationFolder, fileName);
        log.info("Subtitles file saved: " + destinationFolder + "/" + fileName);
    }

    private static String convertTextToSsml(String text) {
        StringBuilder ssml = new StringBuilder("<speak>");
        
        String[] words = text.split("\\s+");
        
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            
            word = word.replace("&", "&amp;")
                             .replace("<", "&lt;")
                             .replace(">", "&gt;");
            
            ssml.append(word).append(" ")
                .append("<mark name=\"w").append(i + 1).append("\"/> ");
        }
        
        ssml.append("</speak>");
        return ssml.toString();
    }

    private static String formatSubtitle(int index, double startTime, double endTime, String text) {
        return index + "\n" +
                formatTime(startTime) + " --> " + formatTime(endTime) + "\n" +
                text + "\n\n";
    }

    private static String formatTime(double seconds) {
        int h = (int) (seconds / 3600);
        int m = (int) ((seconds % 3600) / 60);
        int s = (int) (seconds % 60);
        int ms = (int) ((seconds - (int) seconds) * 1000);
        return String.format("%02d:%02d:%02d,%03d", h, m, s, ms);
    }

}

