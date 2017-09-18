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
    public static final int INVERT_VALUES_PARAM = 0;
    public static final int ZERO_LINE_ON_CENTER_PARAM = 1;

    private final LineRenderer renderer;

    public IndicatorBarChartLayer(Series<Number> data, LineRenderer renderer) {
        super(data);
        this.renderer = renderer;
        setParam(INVERT_VALUES_PARAM, false);
        setParam(ZERO_LINE_ON_CENTER_PARAM, false);
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
        tooltips.clear();
        data.lock();
        try {
            g.setColor(colors.get(0));
            g.setStroke(new BasicStroke(INDICATOR_LINE_WIDTH));
            List<Point> points = new ArrayList<>();
            int first = context.getFirstVisibleCategoryIndex();
            int dataLength = data.getLength();

            for(int i=0; i<context.getNumberOfVisibleCategories() && first+i < dataLength; i++){
                Double v = null;
                try {
                    Number n = data.get(i + first);
                    v = n==null?null:n.doubleValue();
                } catch (ValueException e) {
                    e.printStackTrace();
                }
                if(v==null) {
                    points.add(null);
                    tooltips.add(null);
                } else {
                    v = v * getSign();
                    points.add(new Point(context.toCanvasX(i), context.toCanvasY(v)));
                    tooltips.add(createTooltipText(v, context.getValuesLabelFormatter()));
                }
            }
            g.draw(renderer.renderLine(points));
        } finally {
            g.dispose();
            data.unlock();
        }
    }

    @Override
    protected double getMaxValue(Number value) {
        if(zeroOnCenter()){
            return Math.abs(value.doubleValue());
        }
        return getSign()*value.doubleValue();
    }

    @Override
    protected double getMinValue(Number value) {
        if(zeroOnCenter()){
            return -Math.abs(value.doubleValue());
        }
        return getSign()*value.doubleValue();
    }

    private int getSign(){
        return params.get(INVERT_VALUES_PARAM).equals(true)?-1:1;
    }

    private boolean zeroOnCenter(){
        return (boolean) params.get(ZERO_LINE_ON_CENTER_PARAM);
    }
}
