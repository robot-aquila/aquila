package ru.prolib.aquila.utils.experimental.chart.axis;

public interface AxisDriver extends RulerRendererRegistry {
	
	/**
	 * Get symbolic ID of the axis.
	 * <p>
	 * @return axis ID
	 */
	String getID();
	
	/**
	 * Get axis direction.
	 * <p>
	 * @return axis direction
	 */
	AxisDirection getAxisDirection();
	
}
