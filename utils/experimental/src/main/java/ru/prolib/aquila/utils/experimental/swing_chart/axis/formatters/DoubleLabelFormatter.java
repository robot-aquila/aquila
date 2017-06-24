package ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters;

import java.text.DecimalFormat;
import java.text.ParseException;

/**
 * Created by TiM on 25.06.2017.
 */
public class DoubleLabelFormatter implements LabelFormatter<Double> {
    private static DecimalFormat formatter = new DecimalFormat("#0.######");

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
}
