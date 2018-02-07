package ru.prolib.aquila.utils.experimental.chart.axis;

public interface AxisDriver {
	
	/**
	 * Set axis direction.
	 * <p>
	 * @param dir - new axis direction
	 */
	void setAxisDirection(AxisDirection dir);
	
	/**
	 * Get axis direction.
	 * <p>
	 * @return axis direction
	 */
	AxisDirection getAxisDirection();

}
