package ru.prolib.aquila.utils.experimental.chart.swing.layers;

import org.apache.commons.lang3.Range;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.utils.experimental.chart.BarChartLayer;
import ru.prolib.aquila.utils.experimental.chart.BarChartVisualizationContext;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters.LabelFormatter;

import java.awt.*;
import java.util.List;
import java.util.HashMap;

import static ru.prolib.aquila.utils.experimental.chart.swing.Utils.getGraphics;

/**
 * Created by TiM on 13.06.2017.
 */
public abstract class AbstractBarChartLayer<TCategory, TValue> implements BarChartLayer<TCategory> {
    protected String id;
    protected Series<TValue> data;
    protected HashMap<Integer, Color> colors = new HashMap<>();
    protected boolean visible = true;
    protected List<String> tooltips;

    public AbstractBarChartLayer(Series<TValue> data) {
        this.data = data;
        this.id = data.getId();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void paint(BarChartVisualizationContext vc){
        if(!visible){
            return;
        }
        Graphics2D g = (Graphics2D) getGraphics(vc).create();
        tooltips.clear();
        data.lock();
        try {
            int first = vc.getFirstVisibleCategoryIndex();
            for (int i = 0; i < vc.getNumberOfVisibleCategories(); i++) {
                TValue value = data.get(first + i);
                if(value!=null){
                    paintObject(i, value, vc, g);
                    tooltips.add(createTooltipText(value, vc.getValuesLabelFormatter()));
                } else {
                    tooltips.add(null);
                }
            }
        } catch (ValueException e) {
            e.printStackTrace();
        } finally {
            data.unlock();
            g.dispose();
        }
    }

    @Override
    public Range<Double> getValueRange(int first, int number) {
        Double minY = null;
        Double maxY = null;
        if (!visible || data == null) {
            return null;
        }
        data.lock();
        try {
            for(int i=first; i< first + number; i++){
                TValue value = null;
                try {
                    value = data.get(i);
                } catch (ValueException e) {
                    value = null;
                }
                if(value!=null){
                    double y = getMaxValue(value);
                    if(maxY==null || y>maxY){
                        maxY = y;
                    }
                    y = getMinValue(value);
                    if(minY==null || y<minY){
                        minY = y;
                    }
                }
            }
        } finally {
            data.unlock();
        }
        if(minY!=null && maxY!=null){
            return Range.between(minY, maxY);
        }
        return null;
    }

    @Override
    public BarChartLayer<TCategory> setVisible(boolean visible) {
        this.visible = visible;
        return this;
    }

    @Override
    public BarChartLayer<TCategory> setColor(Color color) {
        return setColor(0, color);
    }

    @Override
    public BarChartLayer<TCategory> setColor(int colorId, Color color) {
        colors.put(colorId, color);
        return this;
    }

    public void setTooltips(List<String> tooltips) {
        this.tooltips = tooltips;
    }

    protected String createTooltipText(TValue value, LabelFormatter labelFormatter) {
        return String.format("%s: %s", getId(), labelFormatter.format(value));
    }

    protected double getMaxValue(TValue value){
        if(value instanceof Number){
            return ((Number) value).doubleValue();
        }
        return 0;
    }

    protected  double getMinValue(TValue value){
        if(value instanceof Number){
            return ((Number) value).doubleValue();
        }
        return 0;
    }

    protected abstract void paintObject(int categoryIdx, TValue value, BarChartVisualizationContext context, Graphics2D g);

}