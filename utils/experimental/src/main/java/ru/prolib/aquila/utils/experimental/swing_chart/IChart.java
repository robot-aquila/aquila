package ru.prolib.aquila.utils.experimental.swing_chart;

import ru.prolib.aquila.utils.experimental.swing_chart.axis.Axis;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.CategoryAxis;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters.LabelFormatter;
import ru.prolib.aquila.utils.experimental.swing_chart.layers.ChartLayer;

import javax.swing.*;
import java.util.List;

/**
 * Created by TiM on 08.09.2017.
 */
public interface IChart<TCategories> {
    JPanel getRootPanel();

    /* Axis */
    CategoryAxis<TCategories> getTopAxis();
    CategoryAxis<TCategories> getBottomAxis();
    Axis<TCategories> getLeftAxis();
    Axis<TCategories> getRightAxis();

    /* Layers */
    List<ChartLayer<TCategories, ?>> getLayers();
    ChartLayer<TCategories, ?> getLayer(String id);
    void addLayer(ChartLayer<TCategories, ?> layer);
    void dropLayer(ChartLayer<TCategories, ?> layer);
    ChartLayer<TCategories, ?> addSmoothLine(String layerId);
    ChartLayer<TCategories, ?> addPolyLine(String layerId);
    ChartLayer<TCategories, ?> addBars(String layerId);

    /* Mouse coords & active category */
    int getLastX(); // not used
    void setLastX(int lastX);
    int getLastY(); // not used
    void setLastY(int lastY); // not used

    TCategories getLastCategory();

    /* Label formatters */
    LabelFormatter<TCategories> getCategoriesLabelFormatter();
    void setCategoriesLabelFormatter(LabelFormatter<TCategories> categoriesLabelFormatter);
    LabelFormatter getValuesLabelFormatter();
    void setValuesLabelFormatter(LabelFormatter valuesLabelFormatter);

    /* set values bounds */
    void setMinValueInterval(Double minValueInterval);
    void setMaxValueInterval(Double maxValueInterval);

    /* overlays */
    List<Overlay> getOverlays();
    Overlay addStaticOverlay(String text, int y);
}
