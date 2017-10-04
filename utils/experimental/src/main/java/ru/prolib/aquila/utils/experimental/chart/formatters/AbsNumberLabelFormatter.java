package ru.prolib.aquila.utils.experimental.chart.formatters;

/**
 * Created by TiM on 05.09.2017.
 */
public class AbsNumberLabelFormatter extends NumberLabelFormatter {
    @Override
    public String format(Number x) {
        return super.format(x==null?null:Math.abs(x.doubleValue()));
    }
}
