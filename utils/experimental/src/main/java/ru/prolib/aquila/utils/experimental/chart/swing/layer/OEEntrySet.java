package ru.prolib.aquila.utils.experimental.chart.swing.layer;

import java.util.Collection;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;

public interface OEEntrySet {
	
	CDecimal getMinPrice();
	CDecimal getMaxPrice();
	Collection<OEEntry> getEntries();
	OEEntrySet addEntry(OEEntry entry);
	OEEntrySet addEntry(boolean isBuy, CDecimal price);

}
