package ru.prolib.aquila.utils.experimental.charts;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Created by TiM on 31.01.2017.
 */
public class Utils {

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static LocalDateTime toLocalDateTime(Instant time){
        return time.atOffset(ZoneOffset.UTC).toLocalDateTime();
    }

    public static String instantToStr(Instant time){
        return dateTimeFormatter.format(toLocalDateTime(time));
    }
}
