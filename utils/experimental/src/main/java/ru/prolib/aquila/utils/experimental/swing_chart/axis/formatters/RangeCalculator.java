package ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters;

/**
 * Created by TiM on 20.06.2017.
 */
public interface RangeCalculator {
    RangeInfo autoRange(double minValue, double maxValue, double length, double minStep);
    RangeInfo autoRange(double minValue, double maxValue, double length, double minStep, Integer precision);
}
