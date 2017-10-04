package ru.prolib.aquila.utils.experimental.chart.formatters;

import java.text.DecimalFormat;
import java.text.ParseException;

/**
 * Created by TiM on 25.06.2017.
 */
public class NumberLabelFormatter extends DefaultLabelFormatter<Number> {
    private int precision = 2;
    private final DecimalFormat formatter;

    public NumberLabelFormatter() {
        formatter = new DecimalFormat();
        formatter.setGroupingUsed(false);
        withPrecision(precision);
    }

    @Override
    public String format(Number x) {
        return x==null?"":formatter.format(x);
    }

    @Override
    public Number parse(String str) {
        try {
            return formatter.parse(str);
        } catch (ParseException e) {
            return null;
        }
    }

    @Override
    public Integer getPrecision() {
        return precision;
    }

    public NumberLabelFormatter withPrecision(int precision) {
        this.precision = precision;
        formatter.setMaximumFractionDigits(precision);
        formatter.setMinimumFractionDigits(precision);
        return this;
    }
}
