package com.smartecmx.postingbot.common;

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
}
