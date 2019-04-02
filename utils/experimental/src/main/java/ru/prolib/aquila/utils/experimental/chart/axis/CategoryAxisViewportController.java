package ru.prolib.aquila.utils.experimental.chart.axis;

public interface CategoryAxisViewportController {
	
	/**
	 * Get current viewport.
	 * <p>
	 * @return viewport
	 */
	CategoryAxisViewport getViewport();
	
	/**
	 * Set desired number of bars to display.
	 * <p>
	 * How it actually affects depends on implementation. See controller info
	 * for details.
	 * <p>
	 * @param number - number of bars to display
	 */
	void setPreferredNumberOfBars(Integer number);
	
	/**
	 * Update params according to number of visible bars available.
	 * <p>
	 * Some cases it is unable to display desired number of bars. It depends
	 * on actual display (window) parameters. This call should inform controller
	 * of number of bars which are actually displayable.
	 * <p>
	 * @param number - number of displayable bars
	 */
	void updateNumberOfVisibleBars(int number);

}
