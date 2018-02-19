package ru.prolib.aquila.utils.experimental.chart.axis.utils;

import java.time.Instant;

/**
 * This is universal label mapper which is used to convert specified
 * time to label from predefined set.
 */
public interface MTFLabelMapper {
	
	/**
	 * Get label of specified time.
	 * <p>
	 * @param time - time to convert to label
	 * @return appropriate label if specified time can be converted to label.
	 * Otherwise returns null.
	 */
	MTFLabel convertToLabel(Instant time);

}
