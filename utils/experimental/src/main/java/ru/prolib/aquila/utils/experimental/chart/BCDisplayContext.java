package ru.prolib.aquila.utils.experimental.chart;

import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDisplayMapper;

/**
 * Bar chart display context.
 */
public interface BCDisplayContext {

	CategoryAxisDisplayMapper getCategoryAxisMapper();
	ValueAxisDisplayMapper getValueAxisMapper();
	
	/**
	 * Get plot area.
	 * <p>
	 * Plot area is the place where chart data must be shown.
	 * It does not include space of rulers.
	 * <p>
	 * @return rectangle of plot area
	 */
	Rectangle getPlotArea();
	
	/**
	 * Get canvas area.
	 * <p>
	 * Canvas is an area of whole chart. It includes both plot and rulers space.
	 * <p>
	 * @return rectangle of canvas area
	 */
	Rectangle getCanvasArea();
	
}
