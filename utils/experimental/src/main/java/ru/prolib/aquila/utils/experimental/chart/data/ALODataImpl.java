package ru.prolib.aquila.utils.experimental.chart.data;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;

/**
 * Simple implementation of data of active limit orders at price level.
 */
public class ALODataImpl implements ALOData {
	private final CDecimal price, buyVolume, sellVolume;
	
	public ALODataImpl(CDecimal price, CDecimal buyVolume, CDecimal sellVolume) {
		this.price = price;
		this.sellVolume = sellVolume;
		this.buyVolume = buyVolume;
	}

	@Override
	public CDecimal getTotalBuyVolume() {
		return buyVolume;
	}

	@Override
	public CDecimal getTotalSellVolume() {
		return sellVolume;
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
		if ( other == null || other.getClass() != ALODataImpl.class ) {
			return false;
		}
		ALODataImpl o = (ALODataImpl) other;
		return new EqualsBuilder()
				.append(o.buyVolume, buyVolume)
				.append(o.sellVolume, sellVolume)
				.append(o.price, price)
				.isEquals();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName()
				+ "[price=" + price
				+ " bv=" + buyVolume
				+ " sv=" + sellVolume
				+ "]";
	}

}
