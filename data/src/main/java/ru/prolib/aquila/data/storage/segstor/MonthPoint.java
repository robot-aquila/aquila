package ru.prolib.aquila.data.storage.segstor;

import java.time.Month;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class MonthPoint implements Comparable<MonthPoint> {
	private final int year;
	private final Month month;

	public MonthPoint(int year, Month month) {
		this.year = year;
		this.month = month;
	}
	
	public int getYear() {
		return year;
	}
	
	public Month getMonth() {
		return month;
	}

	@Override
	public int compareTo(MonthPoint o) {
		return new CompareToBuilder()
				.append(getYear(), o.getYear())
				.append(month,  o.month)
				.toComparison();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + getYear() + ", " + month + "]";
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(9001, 1528693)
				.append(getYear())
				.append(month)
				.toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != MonthPoint.class ) {
			return false;
		}
		MonthPoint o = (MonthPoint) other;
		return new EqualsBuilder()
				.append(getYear(), o.getYear())
				.append(month, o.getMonth())
				.isEquals();
	}

}
