package ru.prolib.aquila.utils.experimental.chart.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;

public class OEEntrySetImpl implements OEEntrySet {
	private final List<OEEntry> entries;
	
	OEEntrySetImpl(List<OEEntry> entries) {
		this.entries = entries;
	}
	
	public OEEntrySetImpl() {
		this(new ArrayList<>());
	}
	
	@Override
	public synchronized OEEntrySet addEntry(OEEntry data) {
		entries.add(data);
		return this;
	}
	
	@Override
	public synchronized OEEntrySet addEntry(boolean isSet, CDecimal price) {
		return addEntry(new OEEntryImpl(isSet, price));
	}

	@Override
	public synchronized CDecimal getMinPrice() {
		CDecimal min = null;
		for ( OEEntry e : entries ) {
			min = e.getPrice().min(min);
		}
		return min;
	}

	@Override
	public synchronized CDecimal getMaxPrice() {
		CDecimal max = null;
		for ( OEEntry e : entries ) {
			max = e.getPrice().max(max);
		}
		return max;
	}

	@Override
	public synchronized Collection<OEEntry> getEntries() {
		return entries;
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != OEEntrySetImpl.class ) {
			return false;
		}
		OEEntrySetImpl o = (OEEntrySetImpl) other;
		return new EqualsBuilder()
				.append(o.entries, entries)
				.isEquals();
	}

}
