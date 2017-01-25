package ru.prolib.aquila.utils.experimental.charts.formatters;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by TiM on 23.01.2017.
 */
public class DefaultTimeAxisSettings implements TimeAxisSettings {

    protected DateTimeFormatter yearLabelFormatter = DateTimeFormatter.ofPattern(getYearLabelFormat());
    protected DateTimeFormatter monthLabelFormatter = DateTimeFormatter.ofPattern(getMonthLabelFormat());
    protected DateTimeFormatter dayLabelFormatter = DateTimeFormatter.ofPattern(getDayLabelFormat());
    protected DateTimeFormatter hourLabelFormatter = DateTimeFormatter.ofPattern(getHourLabelFormat());
    protected DateTimeFormatter labelFormatter = DateTimeFormatter.ofPattern(getLabelFormat());
    protected DateTimeFormatter minorLabelFormatter = DateTimeFormatter.ofPattern(getMinorLabelFormat());

    @Override
    public String getYearLabelFormat() {
        return "YYYY ";
    }

    @Override
    public String getMonthLabelFormat() {
        return "MM.YY";
    }

    @Override
    public String getDayLabelFormat() {
        return "dd.MM";
    }

    @Override
    public String getHourLabelFormat() {
        return "HH:mm";
    }

    @Override
    public String getLabelFormat() {
        return "HH:mm";
    }

    @Override
    public String getMinorLabelFormat() {
        return "HH:mm";
    }

    @Override
    public boolean isMinorLabel(LocalDateTime time) {
        return false;
    }

    @Override
    public String getLabelStyleClass() {
        return "major-axis-label";
    }

    @Override
    public String getMinorLabelStyleClass() {
        return "minor-axis-label";
    }

    @Override
    public String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = labelFormatter;
        if (isMinorLabel(dateTime)){
            formatter = minorLabelFormatter;
        } else {
            if(dateTime.getDayOfYear()==1 && dateTime.getDayOfMonth()==1 && dateTime.getHour()==0 && dateTime.getMinute()==0 && !"".equals(getYearLabelFormat())){
                formatter = yearLabelFormatter;
            } else if(dateTime.getDayOfMonth()==1 && dateTime.getHour()==0 && dateTime.getMinute()==0 && !"".equals(getMonthLabelFormat())){
                formatter = monthLabelFormatter;
            } else if(dateTime.getHour()==0 && dateTime.getMinute()==0 && !"".equals(getDayLabelFormat())){
                formatter = dayLabelFormatter;
            } else if(dateTime.getMinute()==0 && !"".equals(getHourLabelFormat())){
                formatter = hourLabelFormatter;
            }
        }
        return formatter.format(dateTime);
    }
}
