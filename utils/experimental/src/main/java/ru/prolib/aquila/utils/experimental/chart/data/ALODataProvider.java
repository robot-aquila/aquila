package ru.prolib.aquila.utils.experimental.chart.data;

import java.util.Collection;

/**
 * The data provider of set of active limit orders.
 * This service is to use when set of price levels and appropriate volumes are used.
 * For example in chart layer to highlight open orders.
 */
public interface ALODataProvider {
	
	/**
	 * Get volumes for each price level on which active order is exists.
	 * <p>
	 * @return active orders data
	 */
	public Collection<ALOData> getOrderVolumes();

}
