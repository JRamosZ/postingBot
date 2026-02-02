package com.smartecmx.postingbot.common;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public final class CommonMethod {

    private static final String TIME_ZONE = "America/Mexico_City";
    
    private CommonMethod() {}

    public static LocalDate getCurrentDate() {
        return LocalDate.now(ZoneId.of(TIME_ZONE));
    }

    public static LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now(ZoneId.of(TIME_ZONE));
    }

    public static void saveFile(byte[] data, String filePath, String fileName) throws IOException {
        Files.createDirectories(Paths.get(filePath));
        Path path = Paths.get(filePath, fileName);
        Files.write(path, data);
    }

    public static void downloadFile(String folder, String url, String name) throws IOException {
        Path location = Path.of(folder, name);
        try (var in = new URL(url).openStream();
             var out = new FileOutputStream(location.toFile())) {
            in.transferTo(out);
        }
    }

}
