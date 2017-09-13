package ru.prolib.aquila.utils.experimental.chart.swing.axis;

import ru.prolib.aquila.utils.experimental.chart.BarChartAxis;
import ru.prolib.aquila.utils.experimental.chart.AxisLabelProvider;
import ru.prolib.aquila.utils.experimental.chart.BarChartVisualizationContext;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters.DefaultLabelFormatter;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters.LabelFormatter;

/**
 * Created by TiM on 12.09.2017.
 */
public abstract class AbstractBarChartAxis implements BarChartAxis {

    protected boolean visible = true;
    protected LabelFormatter<?> labelFormatter = new DefaultLabelFormatter<>();

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public BarChartAxis setVisible(boolean visible) {
        this.visible = visible;
        return this;
    }

    @Override
    public LabelFormatter<?> getLabelFormatter() {
        return labelFormatter;
    }

    @Override
    public BarChartAxis setLabelFormatter(LabelFormatter<?> labelFormatter) {
        this.labelFormatter = labelFormatter;
        return this;
    }

    @Override
    public abstract void paint(BarChartVisualizationContext context, AxisLabelProvider labelProvider);
}
