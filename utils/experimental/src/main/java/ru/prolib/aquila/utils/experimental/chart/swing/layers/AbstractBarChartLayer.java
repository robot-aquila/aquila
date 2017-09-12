package ru.prolib.aquila.utils.experimental.chart.swing.layers;

import org.apache.commons.lang3.Range;
import ru.prolib.aquila.core.concurrency.Lockable;
import ru.prolib.aquila.core.concurrency.Multilock;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.utils.experimental.chart.BarChartLayer;
import ru.prolib.aquila.utils.experimental.chart.BarChartVisualizationContext;
import ru.prolib.aquila.utils.experimental.chart.swing.BarChartVisualizationContextImpl;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static ru.prolib.aquila.utils.experimental.chart.swing.Utils.getGraphics;

/**
 * Created by TiM on 13.06.2017.
 */
public abstract class AbstractBarChartLayer<TCategory> implements BarChartLayer<TCategory> {
    protected String id;
    protected Series<TCategory> categories;
    protected Series<?> data;
    protected HashMap<Integer, Color> colors = new HashMap<>();
    protected boolean visible = true;
//    protected final ChartLayerDataStorage<TCategories, TValues> storage;
//    protected Map<TCategories, String> currentTooltips = new HashMap<>();
//    protected LabelFormatter<TValues> labelFormatter = new DefaultLabelFormatter<>();

    public AbstractBarChartLayer(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public BarChartLayer<TCategory> setCategories(Series<TCategory> categories) {
        this.categories = categories;
        return this;
    }

    @Override
    public BarChartLayer<TCategory> setData(Series<?> data) {
        this.data = data;
        if(this.id == null || "".equals(this.id)){
            this.id = data.getId();
        }
        return this;
    }

    @Override
    public void paint(BarChartVisualizationContext vc){
        if(!visible){
            return;
        }
        Graphics2D g = (Graphics2D) getGraphics(vc).create();
//        currentTooltips.clear();
        Lockable lock = lockSeries();
        try {
            int first = vc.getFirstVisibleCategoryIndex();
            for (int i = 0; i < vc.getNumberOfVisibleCategories(); i++) {
                Object value = data.get(first + i);
                if(value!=null){
                    paintObject(i, value, vc, g);
                }
            }
        } catch (ValueException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Range<Double> getValueRange(int first, int number) {
        Double minY = null;
        Double maxY = null;
        if (!visible || data == null) {
            return null;
        }
        Lockable lock = lockSeries();
        try {
            for(int i=first; i< first + number; i++){
                Object value = null;
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
            lock.unlock();
        }
        return Range.between(minY, maxY);
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

    //    @Override
//    public String getTooltip(TCategories category) {
//        return currentTooltips.get(category);
//    }
//
//    protected String createTooltipText(TValues value) {
//        return String.format("%s: %s", getId(), labelFormatter.format(value));
//    }


    protected abstract void paintObject(int categoryIdx, Object value, BarChartVisualizationContext context, Graphics2D g);

    protected abstract double getMaxValue(Object value);
    protected abstract double getMinValue(Object value);

    protected Lockable lockSeries(){
        Set<Lockable> locks = new HashSet<>();
        locks.add(categories);
        locks.add(data);
        Multilock lock = new Multilock(locks);
        lock.lock();
        return lock;
    }
}
