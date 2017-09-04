package ru.prolib.aquila.utils.experimental.swing_chart.layers;

import ru.prolib.aquila.core.concurrency.Lockable;
import ru.prolib.aquila.core.concurrency.Multilock;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.utils.experimental.swing_chart.CoordConverter;
import ru.prolib.aquila.utils.experimental.swing_chart.interpolator.LineRenderer;
import ru.prolib.aquila.utils.experimental.swing_chart.interpolator.Point;
import ru.prolib.aquila.utils.experimental.swing_chart.interpolator.SmoothLineRenderer;
import ru.prolib.aquila.utils.experimental.swing_chart.layers.data.ChartLayerDataStorage;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ru.prolib.aquila.utils.experimental.swing_chart.ChartConstants.INDICATOR_LINE_WIDTH;

/**
 * Created by TiM on 31.01.2017.
 */
public class IndicatorChartLayer extends AbstractChartLayer<Instant, Number> {
    private LineRenderer lineRenderer = new SmoothLineRenderer();
    private Color color;
    private boolean invertValues = false;
    private int sign = 1;

    public IndicatorChartLayer(String id) {
        super(id);
    }

    public IndicatorChartLayer(String id, ChartLayerDataStorage<Instant, Number> storage) {
        super(id, storage);
    }

    @Override
    protected void paintObject(Instant category, Number value, CoordConverter<Instant> converter, Graphics2D g) {
        //Nothing
    }

    @Override
    public void paint(CoordConverter<Instant> converter) {
        if(storage.getCategories()==null || storage.getData()==null){
            return;
        }

        Graphics2D g = (Graphics2D) converter.getGraphics().create();

        Set<Lockable> locks = new HashSet<>();
        locks.add(storage.getCategories());
        locks.add(storage.getData());
        Multilock lock = new Multilock(locks);
        lock.lock();
        try {
            g.setColor(color);
            g.setStroke(new BasicStroke(INDICATOR_LINE_WIDTH));
            List<Point> points = new ArrayList<>();
            for(int i=0; i<storage.getCategories().getLength(); i++){
                Instant c = null;
                Double v = null;
                try {
                    c = storage.getCategories().get(i);
                } catch (ValueException e) {
                    e.printStackTrace();
                }
                if(converter.isCategoryDisplayed(c)){
                    try {
                        Number n = storage.getData().get(i);
                        v = n==null?null:n.doubleValue()*sign;
                    } catch (ValueException e) {
                        e.printStackTrace();
                    }
                    if(v==null) {
                        points.add(null);
                    } else {
                        points.add(new Point(converter.getX(c), converter.getY(v)));
                        currentTooltips.put(c, createTooltipText(v));
                    }
                }
            }
            g.draw(lineRenderer.renderLine(points));
        } finally {
            g.dispose();
            lock.unlock();
        }
    }

    public void setLineRenderer(LineRenderer lineRenderer) {
        this.lineRenderer = lineRenderer;
    }

    @Override
    protected double getMaxValue(Number value) {
        return value == null ? null : (value.doubleValue() * sign);
    }

    @Override
    protected double getMinValue(Number value) {
        return value == null ? null : value.doubleValue() * sign;
    }

    @Override
    protected String createTooltipText(Number value) {
        return String.format("%s: %.2f", getId(), value == null ? null : value.doubleValue() * sign);
    }

    public IndicatorChartLayer withColor(Color color) {
        this.color = color;
        return this;
    }

    public IndicatorChartLayer withInvertValues(boolean invertValues) {
        this.invertValues = invertValues;
        sign = invertValues?-1:1;
        return this;
    }
}
