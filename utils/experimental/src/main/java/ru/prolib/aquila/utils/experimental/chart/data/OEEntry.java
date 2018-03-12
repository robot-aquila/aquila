package ru.prolib.aquila.utils.experimental.chart.data;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;

/**
 * Single descriptor of order execution.
 */
public interface OEEntry {
	
	boolean isBuy();
	boolean isSell();
	CDecimal getPrice();

}
