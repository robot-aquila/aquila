package ru.prolib.aquila.transaq.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Board {
	private final String code, name;
	private final int marketID, typeID;
	
	public Board(String code, String name, int marketID, int typeID) {
		this.code = code;
		this.name = name;
		this.marketID = marketID;
		this.typeID = typeID;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getName() {
		return name;
	}
	
	public int getMarketID() {
		return marketID;
	}
	
	public int getTypeID() {
		return typeID;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(1365413, 59)
				.append(code)
				.append(name)
				.append(marketID)
				.append(typeID)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if  ( other == null || other.getClass() != Board.class ) {
			return false;
		}
		Board o = (Board) other;
		return new EqualsBuilder()
				.append(o.code, code)
				.append(o.name, name)
				.append(o.marketID, marketID)
				.append(o.typeID, typeID)
				.build();
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
