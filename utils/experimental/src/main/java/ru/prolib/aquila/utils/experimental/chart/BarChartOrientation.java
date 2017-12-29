package ru.prolib.aquila.utils.experimental.chart;

public enum BarChartOrientation {
	
	/**
	 * Bars from left to right. Leftmost bar is earliest bar and rightmost is the latest.
	 */
	LEFT_TO_RIGHT,
	
	/**
	 * Bars from right to left. Leftmost bar is latest bar and rightmost is the earliest.
	 */
	RIGHT_TO_LEFT,
	
	/**
	 * Bars from top to bottom. Upper bar is most earliest and the lower bar is the most latest.
	 */
	TOP_TO_BOTTOM,
	
	/**
	 * Bars from bottom to top. Upper bar is most latest and the lower bar is the most earliest.
	 */
	BOTTOM_TO_TOP

}
