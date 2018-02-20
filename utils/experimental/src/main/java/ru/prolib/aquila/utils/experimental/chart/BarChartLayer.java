package ru.prolib.aquila.utils.experimental.chart;

import java.awt.Color;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.utils.Range;

public interface BarChartLayer extends ChartElement {
	public static final int DEFAULT_COLOR = 0;
	
	String getId();
	
	/**
	 * Determine min/max values for the specified interval.
	 * <p>
	 * @param first - index of first element in series to start ranging of
	 * @param number - number of elements to scan
	 * @return max and min values for the specified interval. Both values must be
	 * of maximum possible scale.
	 */
	Range<CDecimal> getValueRange(int first, int number);
	
	/**
	 * Set primary layer color.
	 * <p>
	 * Same as call for {@link #setColor(int, Color)} with zero color ID.
	 * <p>
	 * @param color - color
	 * @return this
	 */
	BarChartLayer setColor(Color color);

	/**
	 * Set layer color.
	 * <p>
	 * @param colorId - color ID depends on layer implementation. See layer info for details.
	 * @param color - color
	 * @return this
	 * @throws IllegalArgumentException if unsupported color ID specified
	 */
	BarChartLayer setColor(int colorId, Color color);

	/**
	 * Set layer param.
	 * <p>
	 * @param paramId - param ID depends on layer implementation. See layer info for details.
	 * @param value - value
	 * @return this
	 */
	BarChartLayer setParam(int paramId, Object value);

	/**
	 * Paint layer.
	 * <p>
	 * @param context - display context
	 * @param deviceArg - painting device (depends on drawing method)
	 */
	void paint(BCDisplayContext context, Object device);
	
	Color getColor(int colorId);
	Color getColor();
	Object getParam(int paramId);

}
