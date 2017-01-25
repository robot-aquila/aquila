package ru.prolib.aquila.utils.experimental.charts.formatters;

import java.time.LocalDateTime;

/**
 * Created by TiM on 23.01.2017.
 */
public class M15TimeAxisSettings extends DefaultTimeAxisSettings{

    @Override
    public boolean isMinorLabel(LocalDateTime time) {
        return time.getMinute()!=0;
    }

    @Override
    public String getMinorLabelFormat() {
        return "";
    }
}
