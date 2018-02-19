package ru.prolib.aquila.utils.experimental.chart;

import java.util.List;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDriver;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDriver;

/**
 * Created by TiM on 08.09.2017.
 */
public interface BarChart {
	
	BarChartOrientation getOrientation();
	@Deprecated
	BarChart setHeight(int points);
	@Deprecated
	int getHeight();
	
	CategoryAxisDriver getCategoryAxisDriver();
	ValueAxisDriver getValueAxisDriver();
	
	List<BarChartLayer> getLayers();
	BarChartLayer getLayer(String id);
	BarChartLayer addLayer(BarChartLayer layer);
	BarChartLayer addSmoothLine(Series<CDecimal> series);
	BarChartLayer addPolyLine(Series<CDecimal> series);
	BarChartLayer addHistogram(Series<CDecimal> series);
	BarChart dropLayer(String id);
	
	/**
	 * Set chart visible area.
	 * <p>
	 * TODO: change to control through axis driver
	 * <p>
	 * @param first - first visible category
	 * @param number - number of visible categories
	 * @return this object
	 */
	@Deprecated
	BarChart setVisibleArea(int first, int number);
	
	/**
	 * Set chart values interval.
	 * <p>
	 * TODO: change to control through axis driver
	 * <p>
	 * @param minValue - min visible value, null if not set
	 * @param maxValue - max visible value, null if not set
	 * @return this object
	 */
	@Deprecated
	BarChart setValuesInterval(CDecimal minValue, CDecimal maxValue);
	
	List<ChartOverlay> getOverlays();
	BarChart addStaticOverlay(String text, int y);
	BarChart addOverlay(ChartOverlay overlay);
	
	void paint();
}
