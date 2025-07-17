package com.smartecmx.postingbot.model.Responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FacebookUserLongTokenResponse {
    private String access_token;
    private String token_type;
    private String expires_in;
}
