package ru.prolib.aquila.transaq.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;

public class SecurityBoardParams {
	private final int decimals;
	private final CDecimal tickSize, lotSize, tickValue;
	
	public SecurityBoardParams(int decimals,
			CDecimal tickSize,
			CDecimal lotSize,
			CDecimal tickValue)
	{
		this.decimals = decimals;
		this.tickSize = tickSize;
		this.lotSize = lotSize;
		this.tickValue = tickValue;
	}
	
	public int getDecimals() {
		return decimals;
	}
	
	public CDecimal getTickSize() {
		return tickSize;
	}
	
	public CDecimal getLotSize() {
		return lotSize;
	}
	
	public CDecimal getTickValue() {
		return tickValue;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(9817239, 19201)
				.append(decimals)
				.append(tickSize)
				.append(lotSize)
				.append(tickValue)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != SecurityBoardParams.class ) {
			return false;
		}
		SecurityBoardParams o = (SecurityBoardParams) other;
		return new EqualsBuilder()
				.append(o.decimals, decimals)
				.append(o.tickSize, tickSize)
				.append(o.lotSize, lotSize)
				.append(o.tickValue, tickValue)
				.build();
	}

}
