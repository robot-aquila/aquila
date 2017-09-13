package ru.prolib.aquila.data.storage.segstor;

import java.time.LocalDate;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class DatePoint implements Comparable<DatePoint> {
	private final LocalDate date;
	
	public DatePoint(LocalDate date) {
		this.date = date;
	}
	
	public DatePoint(int year, int month, int dayOfMonth) {
		this(LocalDate.of(year, month, dayOfMonth));
	}
	
	public LocalDate getDate() {
		return date;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + date + "]";
	}

	@Override
	public int compareTo(DatePoint other) {
		return date.compareTo(other.date);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(9113, 46815).append(date).toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != DatePoint.class ) {
			return false;
		}
		DatePoint o = (DatePoint) other;
		return new EqualsBuilder()
				.append(o.date, date)
				.isEquals();
	}

}
