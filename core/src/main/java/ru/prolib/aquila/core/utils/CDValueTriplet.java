package ru.prolib.aquila.core.utils;

import java.math.RoundingMode;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;

public class CDValueTriplet {
	private final Integer scale;
	private final String unit;
	private CDecimal initialValue, finalValue, changeValue;
	
	public CDValueTriplet(Integer scale, String unit) {
		this.scale = scale;
		this.unit = unit;
	}
	
	public CDValueTriplet(Integer scale) {
		this(scale, null);
	}
	
	public CDValueTriplet() {
		this(null, null);
	}
	
	private Integer getScale() {
		if ( scale != null ) {
			return scale;
		} else if ( initialValue != null ) {
			return initialValue.getScale();
		} else if ( finalValue != null ) {
			return finalValue.getScale();
		} else if ( changeValue != null ) {
			return changeValue.getScale();
		} else {
			return null;
		}
	}
	
	private String getUnit() {
		if ( unit != null ) {
			return unit;
		} else if ( initialValue != null ) {
			return initialValue.getUnit();
		} else if ( finalValue != null ) {
			return finalValue.getUnit();
		} else if ( changeValue != null ) {
			return changeValue.getUnit();
		} else {
			return null;
		}
	}
	
	private CDecimal checkScale(CDecimal value) {
		Integer scale = getScale(), vScale = value.getScale();
		if ( scale == null || scale == vScale ) {
			return value;
		} else if ( scale < vScale ) {
			throw new IllegalArgumentException("Scale mismatch: " + value
					+ " (expected: " + scale + ")");
		} else {
			return value.withScale(scale, RoundingMode.UNNECESSARY);
		}
	}
	
	private CDecimal checkScaleAndUnit(CDecimal value) {
		value = checkScale(value);
		String unit = getUnit(), vUnit = value.getUnit();
		if ( ! new EqualsBuilder()
				.append(unit, vUnit)
				.isEquals() )
		{
			throw new IllegalArgumentException("Unit mismatch: "
					+ vUnit + " (expected: " + unit + ")");
		}
		return value;
	}
	
	/**
	 * Get value before change.
	 * <p>
	 * @return value before change
	 */
	public CDecimal getInitialValue() {
		if ( initialValue != null ) {
			return initialValue;
		}
		if ( finalValue == null || changeValue == null ) {
			throw new IllegalStateException("Unable to calculate");
		}
		return finalValue.subtract(changeValue).withScale(getScale(), RoundingMode.UNNECESSARY);
	}

	/**
	 * Get value after change.
	 * <p>
	 * @return value after change
	 */
	public CDecimal getFinalValue() {
		if ( finalValue != null ) {
			return finalValue;
		}
		if ( initialValue == null || changeValue == null ) {
			throw new IllegalStateException("Unable to calculate");
		}
		return initialValue.add(changeValue).withScale(getScale(), RoundingMode.UNNECESSARY);
	}
	
	/**
	 * Get difference between changed and unchanged values.
	 * <p>
	 * @return the difference
	 */
	public CDecimal getChangeValue() {
		if ( changeValue != null ) {
			return changeValue;
		}
		if ( initialValue == null || finalValue == null ) {
			throw new IllegalStateException("Unable to calculate");
		}
		return finalValue.subtract(initialValue);
	}
	
	public void setInitialValue(CDecimal value) {
		value = checkScaleAndUnit(value);
		if ( finalValue != null && changeValue != null ) {
			throw new IllegalStateException("Two of three components are defined");
		}
		initialValue = value;
	}
	
	public void setFinalValue(CDecimal value) {
		value = checkScaleAndUnit(value);
		if ( initialValue != null && changeValue != null ) {
			throw new IllegalStateException("Two of three components are defined");
		}
		finalValue = value;
	}
	
	public void setChangeValue(CDecimal value) {
		value = checkScaleAndUnit(value);
		if ( initialValue != null && finalValue != null ) {
			throw new IllegalStateException("Two of three components are defined");
		}
		changeValue = value;
	}
	
	@Override
	public String toString() {
		return "[i=" + (initialValue == null ? null : initialValue.toString())
			+ " c=" + (changeValue == null ? null : changeValue.toString())
			+ " f=" + (finalValue == null ? null : finalValue.toString())
			+ "]";
	}

	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != CDValueTriplet.class ) {
			return false;
		}
		CDValueTriplet o = (CDValueTriplet) other;
		return new EqualsBuilder()
			.append(changeValue, o.changeValue)
			.append(finalValue, o.finalValue)
			.append(initialValue, o.initialValue)
			.isEquals();
	}

}
