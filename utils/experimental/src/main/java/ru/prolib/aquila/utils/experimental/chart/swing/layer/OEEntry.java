package ru.prolib.aquila.utils.experimental.chart.swing.layer;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;

/**
 * Order execution entry.
 */
public interface OEEntry {
	
	boolean isBuy();
	boolean isSell();
	CDecimal getPrice();

}
