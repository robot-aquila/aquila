package ru.prolib.aquila.utils.experimental.chart.swing.axis;

import ru.prolib.aquila.utils.experimental.chart.AxisLabelProvider;
import ru.prolib.aquila.utils.experimental.chart.BarChartVisualizationContext;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters.LabelFormatter;

import java.util.List;

/**
 * Created by TiM on 12.09.2017.
 */
public class CategoriesLabelProvider<TCategory> implements AxisLabelProvider {

    private final List<TCategory> categories;
    private final BarChartVisualizationContext context;

    public CategoriesLabelProvider(List<TCategory> categories, BarChartVisualizationContext context) {
        this.categories = categories;
        this.context = context;
    }

    @Override
    public int getCanvasX(int i) {
        return context.toCanvasX(i);
    }

    @Override
    public int getCanvasY(int i) {
        return context.toCanvasY(i);
    }

    @Override
    public String getLabel(int i, LabelFormatter labelFormatter) {
        return labelFormatter.format(categories.get(i));
    }

    @Override
    public int getLength() {
        return categories.size();
    }
}
