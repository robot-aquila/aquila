package ru.prolib.aquila.core.utils;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class Range<T extends Comparable<T>> {
	private final T min, max;
	
	public Range(T min, T max) {
		if ( min == null || max == null ) {
			throw new NullPointerException();
		}
		if ( min.compareTo(max) > 0 ) {
			throw new IllegalArgumentException();
		}
		this.min = min;
		this.max = max;
	}
	
	public T getMin() {
		return min;
	}
	
	public T getMax() {
		return max;
	}
	
	/**
	 * Create new range based on two ranges.
	 * <p>
	 * This method determines new min and max values of two ranges and uses
	 * them to create new range.
	 * <p>
	 * @param that - the second range. The argument may be null. In this case
	 * this instance will be returned.
	 * @return new range which is between min and max of both ranges
	 */
	public Range<T> extend(Range<T> that) {
		if ( that == null ) {
			return this;
		}
		T newMin = min.compareTo(that.min) < 0 ? min : that.min;
		T newMax = max.compareTo(that.max) > 0 ? max : that.max;
		return new Range<>(newMin, newMax);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != Range.class ) {
			return false;
		}
		Range<?> o = (Range<?>) other;
		return new EqualsBuilder()
				.append(o.min, min)
				.append(o.max, max)
				.isEquals();
	}

}
