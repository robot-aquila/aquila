package ru.prolib.aquila.utils.experimental.chart;

import java.util.List;

import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.utils.experimental.chart.formatters.LabelFormatter;

/**
 * Created by TiM on 08.09.2017.
 */
public interface BarChart<TCategory> {

	ChartOrientation getOrientation();
	BarChart<TCategory> setHeight(int points);
	int getHeight();

	BarChartAxis getTopAxis();
	BarChartAxis getLeftAxis();
	BarChartAxis getRightAxis();
	BarChartAxis getBottomAxis();

    List<BarChartLayer<TCategory>> getLayers();
    BarChartLayer<TCategory> getLayer(String id);
    BarChartLayer<TCategory> addLayer(BarChartLayer<TCategory> layer);
    BarChartLayer<TCategory> addSmoothLine(Series<? extends Number> series);
    BarChartLayer<TCategory> addPolyLine(Series<? extends Number> series);
    BarChartLayer<TCategory> addHistogram(Series<? extends Number> series);
    BarChart<TCategory> dropLayer(String id);

    /**
     * Set chart visible area.
     * <p>
     * @param first - first visible category
     * @param number - number of visible categories
     * @return this object
     */
    BarChart<TCategory> setVisibleArea(int first, int number);

    /**
     * Set chart values interval.
     * <p>
     * @param minValue - min visible value, null if not set
     * @param maxValue - max visible value, null if not set
     * @return this object
     */
    BarChart<TCategory> setValuesInterval(Double minValue, Double maxValue);

    List<ChartOverlay> getOverlays();
    BarChart<TCategory> addStaticOverlay(String text, int y);
    BarChart<TCategory> addOverlay(ChartOverlay overlay);
    
    void paint();

    LabelFormatter getValuesLabelFormatter();
    BarChart<TCategory> setValuesLabelFormatter(LabelFormatter valuesLabelFormatter);
}
