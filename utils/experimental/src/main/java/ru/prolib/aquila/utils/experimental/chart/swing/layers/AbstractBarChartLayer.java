package ru.prolib.aquila.utils.experimental.chart.swing.layers;

import org.apache.commons.lang3.Range;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.utils.experimental.chart.BarChartLayer;
import ru.prolib.aquila.utils.experimental.chart.BarChartVisualizationContext;
import ru.prolib.aquila.utils.experimental.chart.formatters.LabelFormatter;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import static ru.prolib.aquila.utils.experimental.chart.swing.Utils.getGraphics;

/**
 * Created by TiM on 13.06.2017.
 */
public abstract class AbstractBarChartLayer<TCategory, TValue> implements BarChartLayer<TCategory> {

    protected static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    protected final HashMap<Integer, Color> colors = new HashMap<>();
    protected final Map<Integer, Object> params = new HashMap<>();

    protected String id;
    protected Series<TValue> data;
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
            int dataLength = data.getLength();
            for (int i = 0; i < vc.getNumberOfVisibleCategories() && first+i < dataLength; i++) {
                if(first + i < dataLength){
                    TValue value = data.get(first + i);
                    if(value!=null){
                        paintObject(i, value, vc, g);
                        tooltips.add(createTooltipText(value, vc.getValuesLabelFormatter()));
                    } else {
                        tooltips.add(null);
                    }
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
    public Range<CDecimal> getValueRange(int first, int number) {
        CDecimal minY = CDecimalBD.ZERO;
        CDecimal maxY = CDecimalBD.ZERO;
        if ( ! visible || data == null ) {
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
                    CDecimal y = getMaxValue(value);
                    minY = minY.min(y);
                    maxY = maxY.max(y);
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
    public boolean isVisible() {
        return visible;
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
    public BarChartLayer<TCategory> setParam(int paramId, Object value) {
        params.put(paramId, value);
        return this;
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

    abstract protected CDecimal getMaxValue(TValue value);/*{
        if(value instanceof Number){
            return ((Number) value).doubleValue();
        }
        return 0;
    }*/

    abstract protected CDecimal getMinValue(TValue value);/*{
        if(value instanceof Number){
            return ((Number) value).doubleValue();
        }
        return 0;
    }*/

    protected abstract void paintObject(int categoryIdx, TValue value,
    		BarChartVisualizationContext context, Graphics2D g);

}
