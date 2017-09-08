package ru.prolib.aquila.utils.experimental.chart;

import java.util.List;

import ru.prolib.aquila.core.data.Series;

/**
 * Created by TiM on 08.09.2017.
 */
public interface BarChart<TCategory> {

	ChartOrientation getOrientation();
	BarChart<TCategory> setHeight(int points);
	int getHeight();
	
	BarChartAxis<TCategory> getTopAxis();
	BarChartAxis<TCategory> getLeftAxis();
	BarChartAxis<TCategory> getRightAxis();
	BarChartAxis<TCategory> getBottomAxis();

    BarChartLayer<TCategory> getLayer(String id);
    BarChartLayer<TCategory> addLayer(BarChartLayer<TCategory> layer);
    BarChartLayer<TCategory> addSmoothLine(String layerId);
    BarChartLayer<TCategory> addPolyLine(String layerId);
    BarChartLayer<TCategory> addHystogram(String layerId);
    BarChartLayer<TCategory> addSmoothLine(Series<? extends Number> series);
    BarChartLayer<TCategory> addPolyLine(Series<? extends Number> series);
    BarChartLayer<TCategory> addHystogram(Series<? extends Number> series);
    BarChart<TCategory> dropLayer(String id);

    /**
     * Set chart visible area.
     * <p>
     * @param first - first visible category
     * @param number - number of visible categories
     * @param minValue - min visible value
     * @param maxValue - max visible value
     * @return this object
     */
    BarChart<TCategory> setVisibleArea(int first, int number, double minValue, double maxValue);

    List<ChartOverlay> getOverlays();
    BarChart<TCategory> addStaticOverlay(String text, int y);
    BarChart<TCategory> addOverlay(ChartOverlay overlay);
    
    void paint();
}
