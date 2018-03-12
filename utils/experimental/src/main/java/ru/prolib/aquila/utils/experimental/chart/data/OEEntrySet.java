package ru.prolib.aquila.utils.experimental.chart.data;

import java.util.Collection;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;

/**
 * Set of descriptors of order executions.
 */
public interface OEEntrySet {
	
	CDecimal getMinPrice();
	CDecimal getMaxPrice();
	Collection<OEEntry> getEntries();
	OEEntrySet addEntry(OEEntry entry);
	OEEntrySet addEntry(boolean isBuy, CDecimal price);

}
