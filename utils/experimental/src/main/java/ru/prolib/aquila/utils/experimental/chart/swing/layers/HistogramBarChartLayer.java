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
    public static final int INVERT_VALUES_PARAM = 0;
    public static final int ZERO_LINE_ON_CENTER_PARAM = 1;

    public HistogramBarChartLayer(Series<Number> data) {
        super(data);
        setParam(INVERT_VALUES_PARAM, false);
        setParam(ZERO_LINE_ON_CENTER_PARAM, false);
    }

    @Override
    protected double getMinValue(Number value) {
        if(zeroOnCenter()){
            return -Math.abs(value.doubleValue());
        }
        return getSign() > 0 ? 0 : getSign() * value.doubleValue();
    }

    @Override
    protected double getMaxValue(Number value) {
        if(zeroOnCenter()){
            return Math.abs(value.doubleValue());
        }
        return getSign() > 0 ? getSign() * value.doubleValue() : 0;
    }

    @Override
    protected void paintObject(int categoryIdx, Number value, BarChartVisualizationContext context, Graphics2D g) {
        int cnt = context.getNumberOfVisibleCategories();
        int x = context.toCanvasX(categoryIdx);
        int y = context.toCanvasY(value.doubleValue() * getSign());
        double height = Math.abs(y - context.toCanvasY(0d));
        if(height==0){
            height = 1;
        }
        double width = context.getStepX()*CANDLE_WIDTH_RATIO;
        width= width<CANDLE_MIN_WIDTH?CANDLE_MIN_WIDTH:width;
        g.setColor(colors.get(0));
        g.fill(new Rectangle2D.Double(x - width / 2, getSign()>0 ? y : y - height, width, height));
    }

    private int getSign(){
        return params.get(INVERT_VALUES_PARAM).equals(true)?-1:1;
    }

    private boolean zeroOnCenter(){
        return (Boolean) params.get(ZERO_LINE_ON_CENTER_PARAM);
    }
}
