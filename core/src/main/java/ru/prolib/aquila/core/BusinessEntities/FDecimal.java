package ru.prolib.aquila.core.BusinessEntities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.springframework.util.StringUtils;

public class FDecimal implements Comparable<FDecimal> {
	protected final BigDecimal value;
	protected final RoundingMode roundingMode;
	
	/**
	 * Convert double value to big decimal with specified scale.
	 * <p>
	 * @param value - double value to convert
	 * @param scale - the scale to use
	 * @return decimal value
	 */
	public static BigDecimal toBigDecimal(double value, int scale) {
		return new BigDecimal(String.format(Locale.US, "%." + scale + "f", value));
	}
	
	/**
	 * Convert string representation of the double to big decimal.
	 * <p>
	 * This method for safe conversion from string representation to big
	 * decimal. The string value may contains scientific notation. The scale is
	 * automatically detected by trailing non-zero numbers. Use the
	 * {@link #toBigDecimal(String, int, RoundingMode)} method to force specific
	 * scale.
	 * <p>
	 * @param doubleValue - string representation of a double value
	 * @return decimal value
	 * @throws NumberFormatException - invalid number format
	 */
	public static BigDecimal toBigDecimal(String doubleValue) {
		doubleValue = new BigDecimal(doubleValue).toPlainString();
		if ( doubleValue.contains(".") ) {
			doubleValue = StringUtils.trimTrailingCharacter(doubleValue, '0');
			doubleValue = StringUtils.trimTrailingCharacter(doubleValue, '.');
		}
		return new BigDecimal(doubleValue);
	}
	
	/**
	 * Convert string representation of the double to big decimal.
	 * <p>
	 * This method for safe conversion from string representation to big decimal
	 * value with forced scale. The string value may contains scientific
	 * notation. Use the {@link #toBigDecimal(String)} to detect the scale
	 * automatically.
	 * <p>
	 * @param doubleValue - string representation of a double value
	 * @param scale - scale to use
	 * @param roundingMode - rounding mode used if truncation of the value is needed
	 * @return decimal value
	 * @throws NumberFormatException - invalid number format
	 */
	public static BigDecimal toBigDecimal(String doubleValue, int scale, RoundingMode roundingMode) {
		return toBigDecimal(doubleValue).setScale(scale, roundingMode);
	}
	
	public FDecimal(BigDecimal value, RoundingMode roundingMode) {
		this.value = value;
		this.roundingMode = roundingMode;
	}
	
	public FDecimal(BigDecimal value) {
		this(value, RoundingMode.HALF_UP);
	}
	
	public FDecimal(double value, int scale, RoundingMode roundingMode) {
		this(toBigDecimal(value, scale), roundingMode);
	}
	
	public FDecimal(double value, int scale) {
		this(value, scale, RoundingMode.HALF_UP);
	}
	
	public FDecimal(String value, RoundingMode roundingMode) {
		this(toBigDecimal(value), roundingMode);
	}
	
	public FDecimal(String value) {
		this(value, RoundingMode.HALF_UP);
	}
	
	public FDecimal(String value, int scale, RoundingMode roundingMode) {
		this(toBigDecimal(value, scale, roundingMode), roundingMode);
	}
	
	public FDecimal(String value, int scale) {
		this(value, scale, RoundingMode.HALF_UP);
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
	
	public FDecimal withScale(int scale) {
		return new FDecimal(value.setScale(scale, roundingMode), roundingMode);
	}

}
