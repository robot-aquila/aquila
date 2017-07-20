package ru.prolib.aquila.core.BusinessEntities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.util.StringUtils;

public class FDecimal extends Number implements Comparable<FDecimal> {
	private static final long serialVersionUID = 1L;
	public static final int VERSION = 1;
	public static final FDecimal ZERO0 = FDecimal.of0(0);
	public static final FDecimal ZERO1 = FDecimal.of1(0);
	public static final FDecimal ZERO2 = FDecimal.of2(0);
	public static final FDecimal ZERO3 = FDecimal.of3(0);
	public static final FDecimal ZERO4 = FDecimal.of4(0);
	
	protected final BigDecimal value;
	protected final RoundingMode roundingMode;
	
	public static FDecimal of(String value) {
		return new FDecimal(value);
	}
	
	public static FDecimal of(String value, int scale) {
		return new FDecimal(value, scale);
	}
	
	public static FDecimal of(double value, int scale) {
		return new FDecimal(value, scale);
	}
	
	public static FDecimal of(String value, int scale, RoundingMode rm) {
		return new FDecimal(value, scale, rm);
	}
	
	public static FDecimal of(BigDecimal value) {
		return new FDecimal(value);
	}
	
	public static FDecimal of(BigDecimal value, int scale) {
		return new FDecimal(value).withScale(scale);
	}
	
	public static FDecimal of(BigDecimal value, int scale, RoundingMode rm) {
		return new FDecimal(value, rm).withScale(scale);
	}
	
	public static FDecimal of0(String value) {
		return of(value, 0);
	}
	
	public static FDecimal of1(String value) {
		return of(value, 1);
	}
	
	public static FDecimal of2(String value) {
		return of(value, 2);
	}
	
	public static FDecimal of3(String value) {
		return of(value, 3);
	}
	
	public static FDecimal of4(String value) {
		return of(value, 4);
	}
	
	public static FDecimal of0(double value) {
		return of(value, 0);
	}
	
	public static FDecimal of1(double value) {
		return of(value, 1);
	}

	public static FDecimal of2(double value) {
		return of(value, 2);
	}

	public static FDecimal of3(double value) {
		return of(value, 3);
	}

	public static FDecimal of4(double value) {
		return of(value, 4);
	}

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
	public int hashCode() {
		return new HashCodeBuilder(21, 479)
				.append(value)
				.append(roundingMode)
				.toHashCode();
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
	
	public FDecimal subtract(FDecimal subtrahend) {
		return new FDecimal(value.subtract(subtrahend.value), roundingMode);
	}
	
	public FDecimal add(FDecimal augend) {
		return new FDecimal(value.add(augend.value), roundingMode);
	}
	
	public FDecimal withZero() {
		return new FDecimal(0, value.scale(), roundingMode);
	}
	
	public FDecimal multiply(FDecimal multiplicand) {
		return new FDecimal(value.multiply(multiplicand.value)
			.setScale(Math.max(value.scale(), multiplicand.getScale()), roundingMode), roundingMode);
	}
	
	public FDecimal multiplyExact(FDecimal multiplicand) {
		return new FDecimal(value.multiply(multiplicand.value), roundingMode);
	}
	
	public FDecimal multiply(Long multiplicand) {
		return new FDecimal(value.multiply(new BigDecimal(multiplicand)),
				roundingMode).withScale(getScale());
	}
	
	public FDecimal negate() {
		return new FDecimal(value.negate(), roundingMode);
	}
	
	public FDecimal divide(FDecimal divisor) {
		int scale = Math.max(value.scale(), divisor.getScale());
		return new FDecimal(value.divide(divisor.value, scale, roundingMode), roundingMode);
	}
	
	public FDecimal divide(Long divisor) {
		return new FDecimal(value.divide(new BigDecimal(divisor), getScale(), roundingMode), roundingMode);
	}
	
	public FDecimal divide(FDecimal divisor, int scale, RoundingMode roundingMode) {
		return new FDecimal(value.divide(divisor.value, scale, roundingMode), this.roundingMode);
	}
	
	public FDecimal divide(Long divisor, int scale, RoundingMode roundingMode) {
		return new FDecimal(value.divide(new BigDecimal(divisor), scale, roundingMode), this.roundingMode);
	}
	
	public FDecimal abs() {
		return new FDecimal(value.abs(), roundingMode);
	}

	@Override
	public int intValue() {
		return value.intValue();
	}

	@Override
	public long longValue() {
		return value.longValue();
	}

	@Override
	public float floatValue() {
		return value.floatValue();
	}
}
