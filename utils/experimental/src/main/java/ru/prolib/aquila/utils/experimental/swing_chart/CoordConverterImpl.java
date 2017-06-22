package ru.prolib.aquila.utils.experimental.swing_chart;

import ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters.RangeInfo;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * Created by TiM on 12.06.2017.
 */
public class CoordConverterImpl<TCategories> implements CoordConverter<TCategories> {

    private final List<TCategories> categories;
    private final Graphics2D graphics;
    private final Rectangle2D plotBounds;
    private final RangeInfo yRangeInfo;

    public CoordConverterImpl(List<TCategories> categories, Graphics2D graphics, Rectangle2D plotBounds, RangeInfo yRangeInfo) {
        this.categories = categories;
        this.graphics = graphics;
        this.plotBounds = plotBounds;
        this.yRangeInfo = yRangeInfo;
    }

    @Override
    public List<TCategories> getCategories() {
        return categories;
    }

    @Override
    public boolean isCategoryDisplayed(TCategories category) {
        return categories.contains(category);
    }

    @Override
    public Double getX(TCategories category) {
        int idx = categories.indexOf(category);
        if(idx>=0){
            double step = getStepX();
            return plotBounds.getMinX() + step*(idx+1);
        }
        return null;
    }

    @Override
    public Double getY(Double value) {
        if(value >= yRangeInfo.getMinValue() && value <= yRangeInfo.getMaxValue()){
            return plotBounds.getMaxY() - (value-yRangeInfo.getMinValue())*getCoeffY();
        }
        return null;
    }

    @Override
    public TCategories getCategory(double x) {
        int idx = (int) Math.round ((x - plotBounds.getMinX())/getStepX() - 1);
        if(idx>=0 && idx<categories.size()){
            return categories.get(idx);
        }
        return null;
    }

    @Override
    public Double getValue(double y) {
        return (plotBounds.getMaxY() - y)/getCoeffY() + yRangeInfo.getMinValue();
    }

    @Override
    public Rectangle2D getPlotBounds() {
        return plotBounds;
    }

    @Override
    public double getStepX(){
        return plotBounds.getWidth()/(categories.size()+1);
    }

    @Override
    public Graphics2D getGraphics() {
        return graphics;
    }

    public RangeInfo getYRangeInfo() {
        return yRangeInfo;
    }

    private double getCoeffY(){
        return (plotBounds.getMaxY()-plotBounds.getMinY())/(yRangeInfo.getMaxValue() - yRangeInfo.getMinValue());
    }
}
