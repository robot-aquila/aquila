package ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters;

import java.text.DecimalFormat;
import java.text.ParseException;

/**
 * Created by TiM on 25.06.2017.
 */
public class DoubleLabelFormatter extends DefaultLabelFormatter<Double> {
    private int precision = 2;
    private final DecimalFormat formatter;

    public DoubleLabelFormatter() {
        formatter = new DecimalFormat();
        formatter.setGroupingUsed(false);
        setPrecision(precision);
    }

    @Override
    public String format(Double x) {
        return formatter.format(x);
    }

    @Override
    public Double parse(String str) {
        try {
            return (Double) formatter.parse(str);
        } catch (ParseException e) {
            return null;
        }
    }

    @Override
    public Integer getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
        formatter.setMaximumFractionDigits(precision);
        formatter.setMinimumFractionDigits(precision);
    }
}
