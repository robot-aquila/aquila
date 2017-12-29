package ru.prolib.aquila.utils.experimental.chart.swing.layers;

import org.apache.commons.lang3.Range;
import ru.prolib.aquila.utils.experimental.chart.BarChartLayer;
import ru.prolib.aquila.utils.experimental.chart.BarChartVisualizationContext;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.prolib.aquila.utils.experimental.chart.swing.Utils.getGraphics;
import static ru.prolib.aquila.utils.experimental.chart.ChartConstants.SELECTION_COLOR;

/**
 * Created by TiM on 13.09.2017.
 */
public class CursorLayer implements BarChartLayer {
    private Color color = SELECTION_COLOR;
    private AtomicInteger coord;

    public CursorLayer(AtomicInteger coord) {
        this.coord = coord;
    }

    @Override
    public String getId() {
        return "___CURSOR";
    }

    @Override
    public Range<Double> getValueRange(int first, int number) {
        return null;
    }

    @Override
    public boolean isVisible() {
        return false;
    }

    @Override
    public BarChartLayer setVisible(boolean visible) {
        return this;
    }

    @Override
    public BarChartLayer setColor(Color color) {
        this.color = color;
        return this;
    }

    @Override
    public BarChartLayer setColor(int colorId, Color color) {
        this.color = color;
        return this;
    }

    @Override
    public void paint(BarChartVisualizationContext context) {
        Graphics2D g = (Graphics2D) getGraphics(context).create();
        try {
            int lastCategoryIdx = context.toCategoryIdx(coord.get(), coord.get());
            if(lastCategoryIdx>=0 && lastCategoryIdx < context.getNumberOfVisibleCategories()){
                double x = context.toCanvasX(lastCategoryIdx);
                double width = context.getStepX();
                g.setColor(SELECTION_COLOR);
                g.fill(new Rectangle2D.Double(x-width/2, context.getPlotBounds().getUpperLeftY(), width, context.getPlotBounds().getHeight()));
            }
        } finally {
            g.dispose();
        }

    }

    @Override
    public BarChartLayer setParam(int paramId, Object value) {
        return this;
    }
}
