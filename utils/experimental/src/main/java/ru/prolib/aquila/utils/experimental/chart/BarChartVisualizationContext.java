package ru.prolib.aquila.utils.experimental.chart;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.utils.experimental.chart.formatters.LabelFormatter;

public interface BarChartVisualizationContext {
	
	BarChartOrientation getOrientation();

	int getFirstVisibleCategoryIndex();
	
	int getNumberOfVisibleCategories();
	
	/**
	 * Get minimum visible value.
	 * <p>
	 * @return minimal value
	 */
	CDecimal getMinVisibleValue();
	
	/**
	 * Get maximum visible value.
	 * <p>
	 * @return maximum value
	 */
	CDecimal getMaxVisibleValue();
	
	/**
	 * Get display coordinate of the value.
	 * <p>
	 * This method transforms value to appropriate coordinate of display.
	 * The axis to apply depends on chart orientation and implementation.
	 * It's still to be converted to coordinates of graphics device.
	 * For example chart orientation is
	 * {@link BarChartOrientation#LEFT_TO_RIGHT} and the drawing device implies
	 * that the Y axis increases from bottom to top and the X axis from the
	 * left to the right. In this case the result of this call mean Y
	 * coordinate - the offset from the bottom of the screen in pixels.
	 * <p>
	 * @param value - value to get coordinate of
	 * @return display coordinate associated with the value
	 */
	int toDisplay(CDecimal value);
	
	/**
	 * Get display coordinate of specified bar start.
	 * <p>
	 * This method transforms category index to appropriate display coordinate
	 * of the bar. This coordinate points to the first pixel of bar. The axis
	 * to apply depends on chart orientation and implementation. It's still to
	 * be converted to coordinates of graphics device. For example chart
	 * orientation is {@link BarChartOrientation#LEFT_TO_RIGHT} and the drawing
	 * device implies that the Y axis increases from bottom to top and the X
	 * axis from the left to the right. In this case the result of this call
	 * mean X coordinate which points to the leftmost pixel of bar area. At the
	 * right side of that pixel (including it) is a bar side - area covered
	 * by the bar.
	 * <p>
	 * @param categoryIndex - index of category (same as bar index)
	 * @return display coordinate of specified bar
	 */
	int toDisplayBarStart(int categoryIndex);
	
	/**
	 * Get bar width in pixels.
	 * <p>
	 * @return bar side in pixels
	 */
	int getBarWidthPx();

	Rectangle getPlotBounds();

	LabelFormatter getValuesLabelFormatter();
	
}
