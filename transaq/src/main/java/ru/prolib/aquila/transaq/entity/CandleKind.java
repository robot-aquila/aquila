package ru.prolib.aquila.transaq.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class CandleKind {
	private final int id, period;
	private final String name;
	
	public CandleKind(int id, int period, String name) {
		this.id = id;
		this.period = period;
		this.name = name;
	}
	
	public int getID() {
		return id;
	}
	
	public int getPeriod() {
		return period;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(65232311, 7751)
				.append(id)
				.append(period)
				.append(name)
				.build();
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != CandleKind.class ) {
			return false;
		}
		CandleKind o = (CandleKind) other;
		return new EqualsBuilder()
				.append(o.id, id)
				.append(o.period, period)
				.append(o.name, name)
				.build();
	}

}
