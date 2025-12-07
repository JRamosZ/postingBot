package com.smartecmx.postingbot.common;

import java.util.Arrays;
import java.util.Optional;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OverlayConfig {
    TECHNICALTIP_1_XAYEGE("Arial", 48, "bold", "#FFFFFF", "north", 20, 20, 800, "center"),
    TECHNICALTIP_2_UUBFSU("Arial", 48, "bold", "#000000", "north", 20, 20, 800, "center"),
    TECHNICALTIP_3_IQOJSC("Arial", 48, "bold", "#000000", "north", 20, 20, 800, "center"),
    TECHNICALTIP_4_TCZA85("Arial", 48, "bold", "#000000", "north", 20, 20, 800, "center"),
    TECHNICALTIP_5_AQE4OQ("Arial", 48, "bold", "#000000", "north", 20, 20, 800, "center");
    // CURIOUS_FACT("Roboto", 36, "#000000", "transparent", "south", 0, 40, 1000),
    // TECHNICAL_TIP("Courier", 32, "#FFFFFF", "rgba:000000:60", "north", 0, 20, 900);

    private final String font;
    private final int fontSize;
    private final String weight;
    private final String colorHex;
    private final String gravity;
    private final Integer x;
    private final Integer y;
    private final Integer maxWidth;
    private final String align;

    public static Optional<OverlayConfig> fromFilename(String filename) {
        if (filename == null || filename.isBlank()) return Optional.empty();
        String base = filename.replaceFirst("\\.[^.]*$", ""); // remove extension
        String key = base.replaceAll("[^A-Za-z0-9]", "_").toUpperCase();
        return Arrays.stream(values())
                .filter(v -> v.name().equals(key))
                .findFirst();
    }

}
