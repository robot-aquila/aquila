package ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters;

/**
 * Created by TiM on 19.06.2017.
 */
public class DefaultLabelFormatter implements LabelFormatter<Object> {
    @Override
    public String format(Object x) {
        return x.toString();
    }

    @Override
    public Object parse(String str) {
        return null;
    }
}
