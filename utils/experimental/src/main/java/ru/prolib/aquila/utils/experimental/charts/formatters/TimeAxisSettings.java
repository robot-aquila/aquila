package ru.prolib.aquila.utils.experimental.charts.formatters;

import java.time.LocalDateTime;

/**
 * Created by TiM on 23.01.2017.
 */
public interface TimeAxisSettings {
    String getYearLabelFormat();
    String getMonthLabelFormat();
    String getDayLabelFormat();
    String getHourLabelFormat();
    String getLabelFormat();
    String getMinorLabelFormat();
    boolean isMinorLabel(LocalDateTime time);
    String getLabelStyleClass();
    String getMinorLabelStyleClass();
    String formatDateTime(LocalDateTime dateTime);
}
