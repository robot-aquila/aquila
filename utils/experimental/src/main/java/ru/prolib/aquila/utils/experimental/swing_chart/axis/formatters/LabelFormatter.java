package ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters;

/**
 * Created by TiM on 23.01.2017.
 */
public interface LabelFormatter<T> {
    String format(T x);
    T parse(String str);
}
