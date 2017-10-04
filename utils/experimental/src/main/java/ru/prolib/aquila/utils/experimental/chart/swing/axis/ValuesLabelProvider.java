package ru.prolib.aquila.utils.experimental.chart.swing.axis;

import ru.prolib.aquila.utils.experimental.chart.AxisLabelProvider;
import ru.prolib.aquila.utils.experimental.chart.BarChartVisualizationContext;
import ru.prolib.aquila.utils.experimental.chart.formatters.LabelFormatter;

/**
 * Created by TiM on 12.09.2017.
 */
public class ValuesLabelProvider<TCategory> implements AxisLabelProvider {

    private final BarChartVisualizationContext context;

    public ValuesLabelProvider(BarChartVisualizationContext context) {
        this.context = context;
    }

    @Override
    public int getCanvasX(int i) {
        return context.toCanvasX(getValue(i));
    }

    @Override
    public int getCanvasY(int i) {
        return context.toCanvasY(getValue(i));
    }

    @Override
    public String getLabel(int i, LabelFormatter labelFormatter) {
        return labelFormatter.format(getValue(i));
    }

    @Override
    public int getLength() {
        return (int) Math.round((context.getRangeInfo().getLastValue() - context.getRangeInfo().getFirstValue())/context.getRangeInfo().getStepValue()) + 1;
    }

    private double getValue(int i){
        return context.getRangeInfo().getFirstValue() + i * context.getRangeInfo().getStepValue();
    }
}
