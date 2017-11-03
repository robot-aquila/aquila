package ru.prolib.aquila.core.utils;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.FDecimal;

/**
 * Same as {@link BDValueTriplet} but for
 * {@link ru.prolib.aquila.core.BusinessEntities.FDecimal FDecimal} value class. 
 */
@Deprecated
public class FDValueTriplet {
	private FDecimal initialValue, finalValue, changeValue;
	
	private void checkScale(FDecimal value) {
		int scale;
		if ( initialValue != null ) {
			scale = initialValue.getScale();
		} else if ( finalValue != null ) {
			scale = finalValue.getScale();
		} else if ( changeValue != null ) {
			scale = changeValue.getScale();
		} else {
			return;
		}
		if ( value.getScale() != scale ) {
			throw new IllegalArgumentException("Scale mismatch: " + value
					+ " (expected: " + scale + ")");
		}
	}
	
	/**
	 * Get value before change.
	 * <p>
	 * @return value before change
	 */
	public FDecimal getInitialValue() {
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
	public FDecimal getFinalValue() {
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
	public FDecimal getChangeValue() {
		if ( changeValue != null ) {
			return changeValue;
		}
		if ( initialValue == null || finalValue == null ) {
			throw new IllegalStateException("Unable to calculate");
		}
		return finalValue.subtract(initialValue);
	}
	
	public void setInitialValue(FDecimal value) {
		checkScale(value);
		if ( finalValue != null && changeValue != null ) {
			throw new IllegalStateException("Two of three components are defined");
		}
		initialValue = value;
	}
	
	public void setInitialValue(String value) {
		setInitialValue(new FDecimal(value));
	}

	public void setFinalValue(FDecimal value) {
		checkScale(value);
		if ( initialValue != null && changeValue != null ) {
			throw new IllegalStateException("Two of three components are defined");
		}
		finalValue = value;
	}
	
	public void setFinalValue(String value) {
		setFinalValue(new FDecimal(value));
	}

	public void setChangeValue(FDecimal value) {
		checkScale(value);
		if ( initialValue != null && finalValue != null ) {
			throw new IllegalStateException("Two of three components are defined");
		}
		changeValue = value;
	}
	
	public void setChangeValue(String value) {
		setChangeValue(new FDecimal(value));
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
		if ( other == null || other.getClass() != FDValueTriplet.class ) {
			return false;
		}
		FDValueTriplet o = (FDValueTriplet) other;
		return new EqualsBuilder()
			.append(changeValue, o.changeValue)
			.append(finalValue, o.finalValue)
			.append(initialValue, o.initialValue)
			.isEquals();
	}
	
}
