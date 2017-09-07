package ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters;

/**
 * Created by TiM on 19.06.2017.
 */
public class DefaultLabelFormatter<T> implements LabelFormatter<T> {
    @Override
    public String format(T x) {
        return x==null?"":x.toString();
    }

    @Override
    public T parse(String str) {
        return null;
    }

    @Override
    public Integer getPrecision() {
        return null;
    }
}
