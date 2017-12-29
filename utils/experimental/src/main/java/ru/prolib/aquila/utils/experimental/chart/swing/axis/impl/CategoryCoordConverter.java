package ru.prolib.aquila.utils.experimental.chart.swing.axis.impl;

public interface CategoryCoordConverter {
	
	/**
	 * Convert category index to display coordinate.
	 * <p>
	 * @param categoryIndex - index of category to convert
	 * @return appropriate display coordinate
	 */
	int toDisplay(int categoryIndex);
	
	/**
	 * Convert display coordinate to index of category.
	 * <p>
	 * @param display - coordinate of screen to convert from
	 * @return appropriate coordinate index
	 */
	int toCategory(int display);

}
