package com.smartecmx.postingbot.model.Responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ImgflipResponse {
    private boolean success;
    private Data data;
    private String error_message;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Data {
        private String url;
        private String page_url;
    }
}