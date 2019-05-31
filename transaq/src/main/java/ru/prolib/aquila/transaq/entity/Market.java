package ru.prolib.aquila.transaq.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Market {
	private final int id;
	private final String name;
	
	public Market(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public int getID() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(1435547, 19)
				.append(id)
				.append(name)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != Market.class ) {
			return false;
		}
		Market o = (Market) other;
		return new EqualsBuilder()
				.append(o.id, id)
				.append(o.name, name)
				.build();
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
}
