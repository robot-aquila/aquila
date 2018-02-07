package ru.prolib.aquila.utils.experimental.chart.axis;

import ru.prolib.aquila.utils.experimental.chart.Segment1D;

public interface CategoryAxisDisplayMapper extends AxisDisplayMapper {
	
	/**
	 * Get number of visible bars.
	 * <p>
	 * Bar is the place to display data related to category. Number of
	 * categories which are actually displayed always less or equal to
	 * number of visible bars.
	 * <p>
	 * @return number of visible bars
	 */
	int getNumberOfVisibleBars();
	
	/**
	 * Get index of first visible category.
	 * <p>
	 * Set of visible categories does not necessarily correspond to desired
	 * viewport. Actually it may be less than expected due to layout size. This
	 * method returns index of category which is actually first visible
	 * according to the current axis setup.
	 * <p>
	 * @return index of first visible category
	 */
	int getFirstVisibleCategory();
	
	/**
	 * Get index of last visible category.
	 * <p>
	 * Set of visible categories does not necessarily correspond to desired
	 * viewport. Actually it may be less than expected due to layout size. This
	 * method returns index of category which is actually last visible
	 * according to the current axis setup.
	 * <p>
	 * @return index of last visible category
	 */
	int getLastVisibleCategory();
	
	/**
	 * Get number of visible categories.
	 * <p>
	 * Set of visible categories does not necessarily correspond to desired
	 * viewport. Actually it may be less than expected due to layout size. This
	 * method returns number of categories which are actually visible according
	 * to the current axis setup.
	 * <p>
	 * @return number of visible categories
	 */
	int getNumberOfVisibleCategories();
	
	/**
	 * Convert display coordinate to index of category.
	 * <p>
	 * @param display - coordinate of screen to convert from
	 * @return appropriate category index
	 */
	int toCategory(int displayCoord);
	
	/**
	 * Convert category index to display segment.
	 * <p>
	 * @param categoryIndex - index of category to convert
	 * @return segment of display associated with specified category
	 */
	Segment1D toDisplay(int categoryIndex);

}
