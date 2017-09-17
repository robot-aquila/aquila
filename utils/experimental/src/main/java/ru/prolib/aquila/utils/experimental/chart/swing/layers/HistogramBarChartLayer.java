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
public class HistogramBarChartLayer<TCategory> extends AbstractBarChartLayer<TCategory, Number> {

    protected int sign = 1;

    public HistogramBarChartLayer(Series<Number> data) {
        super(data);
    }

    public HistogramBarChartLayer(Series<Number> data, int sign) {
        this(data);
        this.sign = sign;
    }

    @Override
    protected double getMinValue(Number value) {
        return sign > 0 ? 0 : sign * value.doubleValue();
    }

    @Override
    protected double getMaxValue(Number value) {
        return sign > 0 ? sign * value.doubleValue() : 0;
    }

    @Override
    protected void paintObject(int categoryIdx, Number value, BarChartVisualizationContext context, Graphics2D g) {
        int cnt = context.getNumberOfVisibleCategories();
        int x = context.toCanvasX(categoryIdx);
        int y = context.toCanvasY(value.doubleValue() * sign);
        double height = Math.abs(y - context.toCanvasY(0d));
        if(height==0){
            height = 1;
        }
        double width = context.getStepX()*CANDLE_WIDTH_RATIO;
        width= width<CANDLE_MIN_WIDTH?CANDLE_MIN_WIDTH:width;
        g.setColor(colors.get(0));
        g.fill(new Rectangle2D.Double(x - width / 2, sign>0 ? y : y - height, width, height));
    }
}
