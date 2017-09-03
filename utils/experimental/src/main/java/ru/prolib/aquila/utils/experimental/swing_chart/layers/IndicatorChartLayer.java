package ru.prolib.aquila.utils.experimental.swing_chart.layers;

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
import java.util.List;

import static ru.prolib.aquila.utils.experimental.swing_chart.ChartConstants.INDICATOR_LINE_WIDTH;

/**
 * Created by TiM on 31.01.2017.
 */
public class IndicatorChartLayer extends AbstractChartLayer<Instant, Double> {
    private LineRenderer lineRenderer = new SmoothLineRenderer();
    private Color color;

    public IndicatorChartLayer(String id) {
        super(id);
    }

    public IndicatorChartLayer(String id, ChartLayerDataStorage<Instant, Double> storage) {
        super(id, storage);
    }

    @Override
    protected void paintObject(Instant category, Double value, CoordConverter<Instant> converter, Graphics2D g) {
        //Nothing
    }

    @Override
    public void paint(CoordConverter<Instant> converter) {
        if(storage.getCategories()==null || storage.getData()==null){
            return;
        }

        Graphics2D g = (Graphics2D) converter.getGraphics().create();
        try {
            g.setColor(color);
            g.setStroke(new BasicStroke(INDICATOR_LINE_WIDTH));
            List<Point> points = new ArrayList<>();
            storage.getCategories().lock();
            storage.getData().lock();
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
                        v = storage.getData().get(i);
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
            storage.getCategories().unlock();
            storage.getData().unlock();
        }
    }

    public void setLineRenderer(LineRenderer lineRenderer) {
        this.lineRenderer = lineRenderer;
    }

    @Override
    protected double getMaxValue(Double value) {
        return value;
    }

    @Override
    protected double getMinValue(Double value) {
        return value;
    }

    @Override
    protected String createTooltipText(Double value) {
        return String.format("%s: %.2f", getId(), value);
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
