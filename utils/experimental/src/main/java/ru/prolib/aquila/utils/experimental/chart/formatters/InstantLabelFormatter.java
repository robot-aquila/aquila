package ru.prolib.aquila.utils.experimental.chart.formatters;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Created by TiM on 23.01.2017.
 */
public class InstantLabelFormatter extends DefaultLabelFormatter<Instant> {

    private final ZoneId zoneId;

    protected DateTimeFormatter yearLabelFormatter = DateTimeFormatter.ofPattern(getYearLabelFormat());
    protected DateTimeFormatter monthLabelFormatter = DateTimeFormatter.ofPattern(getMonthLabelFormat());
    protected DateTimeFormatter dayLabelFormatter = DateTimeFormatter.ofPattern(getDayLabelFormat());
    protected DateTimeFormatter hourLabelFormatter = DateTimeFormatter.ofPattern(getHourLabelFormat());
    protected DateTimeFormatter labelFormatter = DateTimeFormatter.ofPattern(getLabelFormat());
    protected DateTimeFormatter minorLabelFormatter = DateTimeFormatter.ofPattern(getMinorLabelFormat());

    public InstantLabelFormatter() {
        zoneId = ZoneId.systemDefault();
    }

    public InstantLabelFormatter(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    public String getYearLabelFormat() {
        return "YYYY ";
    }

    public String getMonthLabelFormat() {
        return "MM.YY";
    }

    public String getDayLabelFormat() {
        return "dd.MM";
    }

    public String getHourLabelFormat() {
        return "HH:mm";
    }

    public String getLabelFormat() {
        return "HH:mm";
    }

    public String getMinorLabelFormat() {
        return "HH:mm";
    }

    @Override
    public String format(Instant x) {
        LocalDateTime dateTime = x.atZone(zoneId).toLocalDateTime();
        DateTimeFormatter formatter = labelFormatter;
        if(dateTime.getDayOfYear()==1 && dateTime.getDayOfMonth()==1 && dateTime.getHour()==0 && dateTime.getMinute()==0 && !"".equals(getYearLabelFormat())){
            formatter = yearLabelFormatter;
        } else if(dateTime.getDayOfMonth()==1 && dateTime.getHour()==0 && dateTime.getMinute()==0 && !"".equals(getMonthLabelFormat())){
            formatter = monthLabelFormatter;
        } else if(dateTime.getHour()==0 && dateTime.getMinute()==0 && !"".equals(getDayLabelFormat())){
            formatter = dayLabelFormatter;
        } else if(dateTime.getMinute()==0 && !"".equals(getHourLabelFormat())){
            formatter = hourLabelFormatter;
        }
        return formatter.format(dateTime);
    }

    @Override
    public Instant parse(String str) {
        return Instant.parse(str);
    }
}
