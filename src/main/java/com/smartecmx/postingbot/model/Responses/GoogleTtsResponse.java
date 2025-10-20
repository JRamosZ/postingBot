package com.smartecmx.postingbot.model.Responses;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class GoogleTtsResponse {
    
    private String audioContent;

    private List<Timepoint> timepoints;
    
    @Setter
    @Getter
    @NoArgsConstructor
    public static class Timepoint {
        private String markName;
        private double timeSeconds;
    }
}
