package com.smartecmx.postingbot.common;

import java.util.Arrays;
import java.util.Optional;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OverlayConfig {
    // This enum names must match the filenames of the images in Cloudinary (without extension)
    TECHNICALTIP_1_XAYEGE("Arial", 52, "bold", "#000000", "north_west", 100, 250, 900, "right"),
    TECHNICALTIP_1_XAYEGE_CTA("Courier", 41, "bold", "#FFFFFF", "north_west", 280, 1270, 800, "center"),
    TECHNICALTIP_2_UUBFSU("Arial", 48, "bold", "#000000", "north_west", 100, 400, 800, "left"),
    TECHNICALTIP_2_UUBFSU_CTA("Courier", 43, "bold", "#FFFFFF", "north_west", 50, 1020, 400, "center"),
    TECHNICALTIP_3_IQOJSC("Arial", 48, "bold", "#000000", "north_west", 140, 320, 800, "center"),
    TECHNICALTIP_3_IQOJSC_CTA("Courier", 43, "bold", "#FFFFFF", "north_west", 155, 1185, 400, "center"),
    TECHNICALTIP_4_TCZA85("Arial", 56, "bold", "#000000", "north_west", 100, 300, 900, "left"),
    TECHNICALTIP_4_TCZA85_CTA("Courier", 43, "bold", "#FFFFFF", "north_west", 280, 1268, 800, "center"),
    TECHNICALTIP_5_AQE4OQ("Arial", 48, "bold", "#000000", "north_west", 20, 20, 800, "center"),
    TECHNICALTIP_5_AQE4OQ_CTA("Courier", 48, "bold", "#FFFFFF", "north_west", 20, 20, 800, "center");

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
