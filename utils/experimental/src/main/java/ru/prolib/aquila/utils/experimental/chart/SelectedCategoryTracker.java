package ru.prolib.aquila.utils.experimental.chart;

/**
 * Tracker of selected category.
 */
public interface SelectedCategoryTracker {
	/**
	 * Check is category selected.
	 * <p>
	 * @return true if selected, false - otherwise
	 */
	boolean isSelected();
	
	/**
	 * Get absolute index of selected category.
	 * <p>
	 * Absolute index means index of element in general series of all categories.
	 * It's different than visible index which is relative to start of bar chart.
	 * <p>
	 * @return absolute index of category
	 * @throws IllegalStateException - when category is not selected
	 */
	int getAbsoluteIndex();
	
	/**
	 * Get visible index of selected category.
	 * <p>
	 * Visible index means number of displaying bar where category must be shown.
	 * <p>
	 * @return visible index of category
	 * @throws IllegalStateException - when category is not selected
	 */
	int getVisibleIndex();

}
