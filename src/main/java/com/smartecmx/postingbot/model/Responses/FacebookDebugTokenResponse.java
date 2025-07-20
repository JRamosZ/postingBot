package com.smartecmx.postingbot.model.Responses;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class FacebookDebugTokenResponse {
    private Data data;

    @Setter
    @Getter
    @NoArgsConstructor
    public static class Data {
        private String app_id;
        private String type;
        private String application;
        private Integer data_access_expires_at;
        private Integer expires_at;
        private Boolean is_valid;
        private Integer issued_at;
        private String profile_id;
        private List<String> scopes;
        private List<GranularScope> granular_scopes;
        private String user_id;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    public static class GranularScope {
        private String scope;
        private List<String> target_ids;
    }

}
