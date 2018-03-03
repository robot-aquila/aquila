package ru.prolib.aquila.utils.experimental.chart.swing.layer;

import java.util.Collection;

/**
 * The data provider of active limit orders layer.
 */
public interface ALODataProvider {
	
	/**
	 * Get volumes for each price level on which active order is exists.
	 * <p>
	 * @return active orders data
	 */
	public Collection<ALOData> getOrderVolumes();

}
