package ru.prolib.aquila.core.BusinessEntities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class FDecimal implements Comparable<FDecimal> {
	protected final BigDecimal value;
	protected final RoundingMode roundingMode;
	
	private static BigDecimal toBD(double value, int scale) {
		return new BigDecimal(String.format(Locale.US, "%." + scale + "f", value));
	}
	
	public FDecimal(BigDecimal value, RoundingMode roundingMode) {
		this.value = value;
		this.roundingMode = roundingMode;
	}
	
	public FDecimal(BigDecimal value) {
		this(value, RoundingMode.HALF_UP);
	}
	
	public FDecimal(double value, int scale, RoundingMode roundingMode) {
		this(toBD(value, scale), roundingMode);
	}
	
	public FDecimal(double value, int scale) {
		this(value, scale, RoundingMode.HALF_UP);
	}
	
	public FDecimal(String value, RoundingMode roundingMode) {
		this(new BigDecimal(value), roundingMode);
	}
	
	public FDecimal(String value) {
		this(value, RoundingMode.HALF_UP);
	}
	
	public FDecimal(){
		this("0.00");
	}
	
	public int getScale() {
		return value.scale();
	}
	
	public RoundingMode getRoundingMode() {
		return roundingMode;
	}

	public BigDecimal toBigDecimal() {
		return value;
	}
	
	public double doubleValue() {
		return value.doubleValue();
	}
	
	@Override
	public String toString() {
		return value.toString();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != FDecimal.class ) {
			return false;
		}
		FDecimal o = (FDecimal) other;
		return new EqualsBuilder()
			.append(value, o.value)
			.append(roundingMode, o.roundingMode)
			.isEquals();
	}

	@Override
	public int compareTo(FDecimal other) {
		if ( other == null ) {
			return 1;
		}
		return value.compareTo(other.value);
	}

}
