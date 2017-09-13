package ru.prolib.aquila.data.storage.segstor;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class YearPoint implements Comparable<YearPoint> {
	private final int year;
	
	public YearPoint(int year) {
		this.year = year;
	}
	
	public int getYear() {
		return year;
	}

	@Override
	public int compareTo(YearPoint o) {
		return new CompareToBuilder()
				.append(year, o.year)
				.toComparison();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + year + "]";
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(45, 1276819)
				.append(year)
				.toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != YearPoint.class ) {
			return false;
		}
		YearPoint o = (YearPoint) other;
		return new EqualsBuilder()
				.append(year, o.year)
				.isEquals();
	}

}
