package ru.prolib.aquila.utils.experimental.chart.swing.layers;

import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.utils.experimental.chart.BarChartVisualizationContext;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import static ru.prolib.aquila.utils.experimental.swing_chart.ChartConstants.CANDLE_MIN_WIDTH;
import static ru.prolib.aquila.utils.experimental.swing_chart.ChartConstants.CANDLE_WIDTH_RATIO;

/**
 * Created by TiM on 13.09.2017.
 */
public class HistogramBarChartLayer<TCategory> extends AbstractBarChartLayer<TCategory> {

    public HistogramBarChartLayer(String id) {
        super(id);
    }

    @Override
    protected void paintObject(int categoryIdx, Object value, BarChartVisualizationContext context, Graphics2D g) {
        int cnt = context.getNumberOfVisibleCategories();
        int x = context.toCanvasX(categoryIdx);
        if(value!=null){

        }
        int y = context.toCanvasY(((Number)value).doubleValue());
        double height = Math.abs(y - context.toCanvasY(0d));
        if(height==0){
            height = 1;
        }
        double width = context.getStepX()*CANDLE_WIDTH_RATIO;
        width= width<CANDLE_MIN_WIDTH?CANDLE_MIN_WIDTH:width;
        g.setColor(colors.get(0));
        g.fill(new Rectangle2D.Double(x - width/2, y, width, height));

    }

    @Override
    protected double getMaxValue(Object value) {
        if(value instanceof Number){
            return ((Number) value).doubleValue();
        }
        return 0;
    }

    @Override
    protected double getMinValue(Object value) {
        return 0;
    }
}
