package ru.prolib.aquila.utils.experimental.chart.axis;

import ru.prolib.aquila.utils.experimental.chart.Segment1D;

public interface AxisDisplayMapper {
	
	/**
	 * Get axis direction.
	 * <p>
	 * @return axis direction
	 */
	AxisDirection getAxisDirection();
	
	/**
	 * Get start coordinate of plot.
	 * <p>
	 * This means X-coordinate for horizontal-oriented axis and Y-coordinate
	 * for vertical-oriented.
	 * <p>
	 * @return start coordinate of plot
	 */
	int getPlotStart();
	
	/**
	 * Get size of plot.
	 * <p>
	 * This means width for horizontal-oriented axis and height for
	 * vertical-oriented.
	 * <p>
	 * @return size of plot
	 */
	int getPlotSize();
	
	Segment1D getPlot();

}
