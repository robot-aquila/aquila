package ru.prolib.aquila.utils.experimental.chart.swing.layers;

import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.utils.experimental.chart.BarChartVisualizationContext;
import ru.prolib.aquila.utils.experimental.swing_chart.interpolator.LineRenderer;
import ru.prolib.aquila.utils.experimental.swing_chart.interpolator.Point;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;

import static ru.prolib.aquila.utils.experimental.chart.swing.Utils.getGraphics;
import static ru.prolib.aquila.utils.experimental.swing_chart.ChartConstants.INDICATOR_LINE_WIDTH;

/**
 * Created by TiM on 13.09.2017.
 */
public class IndicatorBarChartLayer<TCategory> extends AbstractBarChartLayer<TCategory, Number> {

    private final LineRenderer renderer;

    public IndicatorBarChartLayer(Series<?> data, LineRenderer renderer) {
        super(data);
        this.renderer = renderer;
    }

    @Override
    protected void paintObject(int categoryIdx, Number number, BarChartVisualizationContext context, Graphics2D g) {

    }

    @Override
    public void paint(BarChartVisualizationContext context) {
        if(data==null){
            return;
        }

        Graphics2D g = (Graphics2D) getGraphics(context).create();
        data.lock();
        try {
            g.setColor(colors.get(0));
            g.setStroke(new BasicStroke(INDICATOR_LINE_WIDTH));
            List<Point> points = new ArrayList<>();
            for(int i=0; i<context.getNumberOfVisibleCategories(); i++){
                Double v = null;
                try {
                    Number n = (Number) data.get(i + context.getFirstVisibleCategoryIndex());
                    v = n==null?null:n.doubleValue();
                } catch (ValueException e) {
                    e.printStackTrace();
                }
                if(v==null) {
                    points.add(null);
                    currentTooltips.add(null);
                } else {
                    points.add(new Point(context.toCanvasX(i), context.toCanvasY(v)));
                    currentTooltips.add(createTooltipText(v, context.getValuesLabelFormatter()));
                }
            }
            g.draw(renderer.renderLine(points));
        } finally {
            g.dispose();
            data.unlock();
        }
    }
}
