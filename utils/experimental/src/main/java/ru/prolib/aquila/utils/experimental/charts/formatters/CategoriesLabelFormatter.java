package ru.prolib.aquila.utils.experimental.charts.formatters;

/**
 * Created by TiM on 23.01.2017.
 */
public interface CategoriesLabelFormatter<T> {
    boolean isMinorLabel(T x);
    String getLabelStyleClass();
    String getMinorLabelStyleClass();
    String format(T x);
    T parse(String str);
}
