package ru.prolib.aquila.ta;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Тип разворотной точки.
 */
public class PivotType {
	public static final PivotType MAX = new PivotType(1);
	public static final PivotType MIN = new PivotType(-1);
	private final int type;
	
	private PivotType(int type) {
		super();
		this.type = type;
	}
	
	@Override
	public boolean equals(Object o) {
		if ( o == null || !(o instanceof PivotType) ) {
			return false;
		} else if ( o == this ) {
			return true;
		}
		PivotType other = (PivotType) o; 
		return new EqualsBuilder()
			.append(type, other.type)
			.isEquals();
	}
	
	@Override
	public String toString() {
		return this == MAX ? "MAX" : "MIN";
	}
}