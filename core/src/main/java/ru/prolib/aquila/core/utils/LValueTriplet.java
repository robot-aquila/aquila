package ru.prolib.aquila.core.utils;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Representation of changeable long value.
 * <p>
 * This class represents two predefined and one calculable value. Only two of
 * three components are allowed. The third component will be calculated using
 * the first two. This guarantees that all three components are always in
 * consistent state to each other.
 */
public class LValueTriplet {
	private Long initialValue, finalValue, changeValue;
	
	/**
	 * Get value before change.
	 * <p>
	 * @return value before change
	 */
	public long getInitialValue() {
		if ( initialValue != null ) {
			return initialValue;
		}
		if ( finalValue == null || changeValue == null ) {
			throw new IllegalStateException("Unable to calculate");
		}
		return finalValue - changeValue;
	}
	
	/**
	 * Get value after change.
	 * <p>
	 * @return value after change
	 */
	public long getFinalValue() {
		if ( finalValue != null ) {
			return finalValue;
		}
		if ( initialValue == null || changeValue == null ) {
			throw new IllegalStateException("Unable to calculate");
		}
		return initialValue + changeValue;
	}
	
	/**
	 * Get difference between changed and unchanged values.
	 * <p>
	 * @return the difference
	 */
	public long getChangeValue() {
		if ( changeValue != null ) {
			return changeValue;
		}
		if ( initialValue == null || finalValue == null ) {
			throw new IllegalStateException("Unable to calculate");
		}
		return finalValue - initialValue;
	}
	
	public void setInitialValue(long value) {
		if ( finalValue != null && changeValue != null ) {
			throw new IllegalStateException("Two of three components are defined");
		}
		initialValue= value;
	}
	
	public void setFinalValue(long value) {
		if ( initialValue != null && changeValue != null ) {
			throw new IllegalStateException("Two of three components are defined");
		}
		finalValue = value;
	}
	
	public void setChangeValue(long value) {
		if ( initialValue != null && finalValue != null ) {
			throw new IllegalStateException("Two of three components are defined");
		}
		changeValue = value;
	}
	
	@Override
	public String toString() {
		return "[i=" + initialValue + " c=" + changeValue + " f=" + finalValue + "]";
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != LValueTriplet.class ) {
			return false;
		}
		LValueTriplet o = (LValueTriplet) other;
		return new EqualsBuilder()
			.append(changeValue, o.changeValue)
			.append(finalValue, o.finalValue)
			.append(initialValue, o.initialValue)
			.isEquals();
	}

}
