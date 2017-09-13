package ru.prolib.aquila.utils.experimental.chart;

import java.awt.Color;

import org.apache.commons.lang3.Range;

public interface BarChartLayer<TCategory> {
	public static final int DEFAULT_COLOR = 0;
	
	String getId();
	
	/**
	 * Determine min/max values for the specified interval.
	 * <p>
	 * @param first - index of first element in series to start ranging of
	 * @param number - number of elements to scan
	 * @return max and min values for the specified interval
	 */
	Range<Double> getValueRange(int first, int number);

	/**
	 * Show or hide layer.
	 * <p>
	 * @param visible - visible flag
	 * @return this object
	 */
	BarChartLayer<TCategory> setVisible(boolean visible);
	
	/**
	 * Set primary layer color.
	 * <p>
	 * Same as call for {@link #setColor(int, Color)} with zero color ID.
	 * <p>
	 * @param color - color
	 * @return this
	 */
	BarChartLayer<TCategory> setColor(Color color);

	/**
	 * Set layer color.
	 * <p>
	 * @param colorId - color ID depends on layer implementation. See layer info for details.
	 * @param color - color
	 * @return this
	 * @throws IllegalArgumentException if unsupported color ID specified
	 */
	BarChartLayer<TCategory> setColor(int colorId, Color color);
	
	/**
	 * Paint layer.
	 * <p>
	 * @param context - visualization context. Context is implementation depended.
	 */
	void paint(BarChartVisualizationContext context);

	String getTooltipText(int categoryIdx);

}
