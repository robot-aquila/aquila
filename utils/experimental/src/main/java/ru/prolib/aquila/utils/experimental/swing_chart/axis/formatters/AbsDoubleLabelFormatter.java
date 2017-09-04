package ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters;

/**
 * Created by TiM on 05.09.2017.
 */
public class AbsDoubleLabelFormatter extends DoubleLabelFormatter {
    @Override
    public String format(Double x) {
        return super.format(Math.abs(x));
    }
}
