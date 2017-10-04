package ru.prolib.aquila.utils.experimental.chart.swing;

import ru.prolib.aquila.utils.experimental.chart.BarChartVisualizationContext;
import ru.prolib.aquila.utils.experimental.chart.Rectangle;
import ru.prolib.aquila.utils.experimental.chart.formatters.LabelFormatter;
import ru.prolib.aquila.utils.experimental.chart.formatters.RangeInfo;

import java.awt.*;

/**
 * Created by TiM on 11.09.2017.
 */
public class BarChartVisualizationContextImpl implements BarChartVisualizationContext {
    private final int first, number;
    private final Graphics2D graphics;
    private final Rectangle plotBounds;
    private final RangeInfo yRangeInfo;
    private final LabelFormatter<?> valuesLabelFormatter;

    public BarChartVisualizationContextImpl(int first, int number, Graphics2D graphics, Rectangle plotBounds, RangeInfo yRangeInfo, LabelFormatter<?> valuesLabelFormatter) {
        this.first = first;
        this.number = number;
        this.graphics = graphics;
        this.plotBounds = plotBounds;
        this.yRangeInfo = yRangeInfo;
        this.valuesLabelFormatter = valuesLabelFormatter;
    }

    @Override
    public int getFirstVisibleCategoryIndex() {
        return first;
    }

    @Override
    public int getNumberOfVisibleCategories() {
        return number;
    }

    @Override
    public double getMinVisibleValue() {
        return yRangeInfo.getMinValue();
    }

    @Override
    public double getMaxVisibleValue() {
        return yRangeInfo.getMaxValue();
    }

    @Override
    public double toValue(int canvasX, int canvasY) {
        return (plotBounds.getY() + plotBounds.getHeight() - canvasY)/getCoeffY() + yRangeInfo.getMinValue();
    }

    @Override
    public int toCategoryIdx(int canvasX, int canvasY) {
        return (int) Math.round ((canvasX - plotBounds.getX())/getStepX() - 1);
    }

    @Override
    public int toCanvasX(int displayedCategoryIdx) {
        return (int) Math.round(plotBounds.getX() + getStepX()*(displayedCategoryIdx+1));
    }

    @Override
    public int toCanvasX(double value) {
        return 0;
    }

    @Override
    public int toCanvasY(double value) {
        return (int) Math.round(plotBounds.getY() + plotBounds.getHeight() - (value-yRangeInfo.getMinValue())*getCoeffY());
    }

    @Override
    public int toCanvasY(int displayedCategoryIdx) {
        return 0;
    }

    @Override
    public double getStepX() {
        return plotBounds.getWidth()/(number+1d);
    }

    @Override
    public RangeInfo getRangeInfo() {
        return yRangeInfo;
    }

    @Override
    public Rectangle getPlotBounds() {
        return plotBounds;
    }

    public Graphics2D getGraphics() {
        return graphics;
    }

    @Override
    public LabelFormatter<?> getValuesLabelFormatter() {
        return valuesLabelFormatter;
    }

    private double getCoeffY(){
        return (plotBounds.getHeight())/(yRangeInfo.getMaxValue() - yRangeInfo.getMinValue());
    }
}
