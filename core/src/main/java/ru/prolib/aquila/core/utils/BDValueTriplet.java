package ru.prolib.aquila.core.utils;

import java.math.BigDecimal;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Representation of changeable big decimal value.
 * <p>
 * This class represents two predefined and one calculable value.
 * All values passed in are expected to the same scale which is passed to
 * constructor. Only two of three components are allowed. The third component
 * will be calculated using the first two. This guarantees that all three
 * components are always in consistent state to each other.
 */
public class BDValueTriplet {
	private BigDecimal initialValue, finalValue, changeValue;

	private void checkScale(BigDecimal value) {
		int scale;
		if ( initialValue != null ) {
			scale = initialValue.scale();
		} else if ( finalValue != null ) {
			scale = finalValue.scale();
		} else if ( changeValue != null ) {
			scale = changeValue.scale();
		} else {
			return;
		}
		if ( value.scale() != scale ) {
			throw new IllegalArgumentException("Scale mismatch: " + value
					+ " (expected: " + scale + ")");
		}
	}
	
	/**
	 * Get value before change.
	 * <p>
	 * @return value before change
	 */
	public BigDecimal getInitialValue() {
		if ( initialValue != null ) {
			return initialValue;
		}
		if ( finalValue == null || changeValue == null ) {
			throw new IllegalStateException("Unable to calculate");
		}
		return finalValue.subtract(changeValue);
	}
	
	/**
	 * Get value after change.
	 * <p>
	 * @return value after change
	 */
	public BigDecimal getFinalValue() {
		if( finalValue != null ) {
			return finalValue;
		}
		if ( initialValue == null || changeValue == null ) {
			throw new IllegalStateException("Unable to calculate");
		}
		return initialValue.add(changeValue);
	}
	
	/**
	 * Get difference between changed and unchanged values.
	 * <p>
	 * @return the difference
	 */
	public BigDecimal getChangeValue() {
		if ( changeValue != null ) {
			return changeValue;
		}
		if ( initialValue == null || finalValue == null ) {
			throw new IllegalStateException("Unable to calculate");
		}
		return finalValue.subtract(initialValue);
	}

	public void setInitialValue(BigDecimal value) {
		checkScale(value);
		if ( finalValue != null && changeValue != null ) {
			throw new IllegalStateException("Two of three components are defined");
		}
		initialValue = value;
	}
	
	public void setInitialValue(String value) {
		setInitialValue(new BigDecimal(value));
	}

	public void setFinalValue(BigDecimal value) {
		checkScale(value);
		if ( initialValue != null && changeValue != null ) {
			throw new IllegalStateException("Two of three components are defined");
		}
		finalValue = value;
	}
	
	public void setFinalValue(String value) {
		setFinalValue(new BigDecimal(value));
	}

	public void setChangeValue(BigDecimal value) {
		checkScale(value);
		if ( initialValue != null && finalValue != null ) {
			throw new IllegalStateException("Two of three components are defined");
		}
		changeValue = value;
	}
	
	public void setChangeValue(String value) {
		setChangeValue(new BigDecimal(value));
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
		if ( other == null || other.getClass() != BDValueTriplet.class ) {
			return false;
		}
		BDValueTriplet o = (BDValueTriplet) other;
		return new EqualsBuilder()
			.append(changeValue, o.changeValue)
			.append(finalValue, o.finalValue)
			.append(initialValue, o.initialValue)
			.isEquals();
	}

}
