package ru.prolib.aquila.utils.experimental.swing_chart;

import ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters.RangeInfo;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * Created by TiM on 12.06.2017.
 */
public interface CoordConverter<TCategories> {
    List<TCategories> getCategories();
    boolean isCategoryDisplayed(TCategories category);
    Double getX(TCategories category);
    Double getY(Double value);
    double getStepX();
    TCategories getCategory(double x);
    Double getValue(double y);
    Rectangle2D getPlotBounds();
    Graphics2D getGraphics();
    RangeInfo getYRangeInfo();
}
