package ru.prolib.aquila.core.utils;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.FDecimal;
import ru.prolib.aquila.core.BusinessEntities.FMoney;

/**
 * Same as {@link BDValueTriplet} but for
 * {@link ru.prolib.aquila.core.BusinessEntities.FMoney FMoney} value class. 
 */
@Deprecated
public class FMValueTriplet {
	private FMoney initialValue, finalValue, changeValue;
	
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
	
	private void checkScaleAndCurrency(FMoney value) {
		checkScale(value);
		String currencyCode;
		if ( initialValue != null ) {
			currencyCode = initialValue.getCurrencyCode();
		} else if ( finalValue != null ) {
			currencyCode = finalValue.getCurrencyCode();
		} else if( changeValue != null ) {
			currencyCode = changeValue.getCurrencyCode();
		} else {
			return;
		}
		if ( ! value.getCurrencyCode().equals(currencyCode) ) {
			throw new IllegalArgumentException("Currency mismatch: "
				+ value.getCurrencyCode()
				+ " (expected: " + currencyCode + ")");
		}
	}
	
	/**
	 * Get value before change.
	 * <p>
	 * @return value before change
	 */
	public FMoney getInitialValue() {
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
	public FMoney getFinalValue() {
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
	public FMoney getChangeValue() {
		if ( changeValue != null ) {
			return changeValue;
		}
		if ( initialValue == null || finalValue == null ) {
			throw new IllegalStateException("Unable to calculate");
		}
		return finalValue.subtract(initialValue);
	}

	public void setInitialValue(FMoney value) {
		checkScaleAndCurrency(value);
		if ( finalValue != null && changeValue != null ) {
			throw new IllegalStateException("Two of three components are defined");
		}
		initialValue = value;
	}

	public void setFinalValue(FMoney value) {
		checkScaleAndCurrency(value);
		if ( initialValue != null && changeValue != null ) {
			throw new IllegalStateException("Two of three components are defined");
		}
		finalValue = value;
	}
	
	public void setChangeValue(FMoney value) {
		checkScaleAndCurrency(value);
		if ( initialValue != null && finalValue != null ) {
			throw new IllegalStateException("Two of three components are defined");
		}
		changeValue = value;
	}

	@Override
	public String toString() {
		return "[i=" + (initialValue == null ? null : initialValue.toStringWithCurrency())
			+ " c=" + (changeValue == null ? null : changeValue.toStringWithCurrency())
			+ " f=" + (finalValue == null ? null : finalValue.toStringWithCurrency())
			+ "]";
	}

	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != FMValueTriplet.class ) {
			return false;
		}
		FMValueTriplet o = (FMValueTriplet) other;
		return new EqualsBuilder()
			.append(changeValue, o.changeValue)
			.append(finalValue, o.finalValue)
			.append(initialValue, o.initialValue)
			.isEquals();
	}

}
