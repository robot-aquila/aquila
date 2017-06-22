package ru.prolib.aquila.utils.experimental.swing_chart.layers;

import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.utils.experimental.swing_chart.CoordConverter;
import ru.prolib.aquila.utils.experimental.swing_chart.interpolator.LineRenderer;
import ru.prolib.aquila.utils.experimental.swing_chart.interpolator.Point;
import ru.prolib.aquila.utils.experimental.swing_chart.interpolator.SmoothLineRenderer;

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

    @Override
    protected void paintObject(Instant category, Double value, CoordConverter<Instant> converter) {
        //Nothing
    }

    @Override
    public void paint(CoordConverter<Instant> converter) {
        Graphics2D g = (Graphics2D) converter.getGraphics().create();
        try {
            g.setColor(color);
            g.setStroke(new BasicStroke(INDICATOR_LINE_WIDTH));
            List<Point> points = new ArrayList<>();
            for(int i=0; i<categories.getLength(); i++){
                Instant c = null;
                Double v = null;
                try {
                    c = categories.get(i);
                } catch (ValueException e) {
                    e.printStackTrace();
                }
                if(converter.isCategoryDisplayed(c)){
                    try {
                        v = data.get(i);
                    } catch (ValueException e) {
                        e.printStackTrace();
                    }
                    if(v!=null){
                        points.add(new Point(converter.getX(c), converter.getY(v)));
                        currentTooltips.put(c, createTooltipText(v));
                    }
                }
            }
            g.draw(lineRenderer.renderLine(points));
        } finally {
            g.dispose();
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
