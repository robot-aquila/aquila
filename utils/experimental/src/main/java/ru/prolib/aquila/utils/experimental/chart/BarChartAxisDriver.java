package ru.prolib.aquila.utils.experimental.chart;

public interface BarChartAxisDriver {

	/**
	 * Update internal state according to viewport specified.
	 * <p>
	 * This update is primary update and should be called before layout calculation.
	 * This operation resets viewport and layout which were previously set.
	 * Note that the viewport attributes may be changed during the operation.
	 * For example the driver may increase value range to meet tick size requirement.
	 * Current viewport will be stored to the driver and will be used until changed.
	 * <p>
	 * @param viewport - current viewport
	 */
	void updateViewport(BarChartViewport viewport);
	
	/**
	 * Update internal state according to layout specified.
	 * <p>
	 * The layout is used to determine how the painting area reflects value range.
	 * This update is secondary update and should be called after layout calculation.
	 * This operation resets layout which was previously set.
	 * Note that viewport attributes may be changed during the operation.
	 * For example the driver may increase value range to get better view when
	 * translate values to display coordinates. Current layout will be stored
	 * to the driver and will be used until viewport or layout changed.
	 * <p>
	 * @param layout - current layout
	 */
	void updateLayout(ChartLayout layout);

}
