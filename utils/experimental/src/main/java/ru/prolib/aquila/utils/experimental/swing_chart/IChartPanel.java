package ru.prolib.aquila.utils.experimental.swing_chart;

import ru.prolib.aquila.core.data.KSeries;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.utils.experimental.swing_chart.layers.ChartLayer;

import javax.swing.*;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.time.Instant;
import java.util.List;

/**
 * Created by TiM on 08.09.2017.
 */
public interface IChartPanel<TCategories> extends MouseWheelListener, MouseMotionListener {
    JPanel getRootPanel();

    /* Charts */
    Chart<TCategories> addChart(String id);
    Chart<TCategories> addChart(String id, Integer height) throws IllegalArgumentException;
    Chart<TCategories> getChart(String id);

    /* Index of first displayed category */
    void setCurrentPosition(int position);
    void setCurrentPosition(int position, boolean newCategoryAdded);
    int getCurrentPosition();

    /* number of displayed categories */
    int getNumberOfPoints();
    void setNumberOfPoints(int numberOfPoints);

    /* Data */
    void clearData();
    void setCategoriesSeries(Series<TCategories> categoriesSeries);
}
