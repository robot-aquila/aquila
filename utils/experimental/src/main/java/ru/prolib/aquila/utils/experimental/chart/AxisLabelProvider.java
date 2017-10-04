package ru.prolib.aquila.utils.experimental.chart;

import ru.prolib.aquila.utils.experimental.chart.formatters.LabelFormatter;

/**
 * Created by TiM on 12.09.2017.
 */
public interface AxisLabelProvider {
    int getCanvasX(int i);
    int getCanvasY(int i);
    String getLabel(int i, LabelFormatter labelFormatter);
    int getLength();
}
