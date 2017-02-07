package ru.prolib.aquila.utils.experimental.charts;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Created by TiM on 31.01.2017.
 */
public class Utils {
    public static LocalDateTime toLocalDateTime(Instant time){
        return time.atOffset(ZoneOffset.UTC).toLocalDateTime();
    }
}
