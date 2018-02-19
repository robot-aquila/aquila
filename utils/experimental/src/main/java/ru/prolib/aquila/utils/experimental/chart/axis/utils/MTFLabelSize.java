package ru.prolib.aquila.utils.experimental.chart.axis.utils;

/**
 * Interface of label size determination strategy.
 */
public interface MTFLabelSize {

	/**
	 * Get size of label with specified text.
	 * <p>
	 * @param labelText - text of label
	 * @return size of label
	 */
	int getVisibleSize(String labelText);

}
