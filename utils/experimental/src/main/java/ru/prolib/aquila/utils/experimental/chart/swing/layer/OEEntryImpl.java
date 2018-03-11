package ru.prolib.aquila.utils.experimental.chart.swing.layer;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;

public class OEEntryImpl implements OEEntry {
	private final boolean isBuy;
	private final CDecimal price;
	
	public OEEntryImpl(boolean isBuy, CDecimal price) {
		this.isBuy = isBuy;
		this.price = price;
	}

	@Override
	public CDecimal getPrice() {
		return price;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != OEEntryImpl.class ) {
			return false;
		}
		OEEntryImpl o = (OEEntryImpl) other;
		return new EqualsBuilder()
				.append(o.price, price)
				.append(o.isBuy, isBuy)
				.isEquals();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[isBuy=" + isBuy + " price=" + price + "]";
	}

	@Override
	public boolean isBuy() {
		return isBuy;
	}

	@Override
	public boolean isSell() {
		return ! isBuy;
	}

}
