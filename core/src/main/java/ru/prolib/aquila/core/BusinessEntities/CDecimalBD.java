package ru.prolib.aquila.core.BusinessEntities;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Consistent decimal based on {@link java.math.BigDecimal BigDecimal}.
 */
public class CDecimalBD extends CDecimalAbstract {
	public static final String RUB = "RUB";
	public static final String USD = "USD";
	public static final String EUR = "EUR";
	public static final String CAD = "CAD";
	public static final CDecimal ZERO = of("0");
	public static final CDecimal ZERO_RUB2 = ofRUB2("0");
	public static final CDecimal ZERO_RUB5 = ofRUB5("0");
	public static final CDecimal ZERO_USD2 = ofUSD2("0");
	public static final CDecimal ZERO_USD5 = ofUSD5("0");
	
	public static CDecimal of(String value, String unit, RoundingMode roundingMode) {
		return new CDecimalBD(value, unit, roundingMode);
	}
	
	public static CDecimal of(String value, String unit) {
		return of(value, unit, RoundingMode.HALF_UP);
	}
	
	public static CDecimal of(String value) {
		return of(value, null);
	}
	
	public static CDecimal of(Long value) {
		return new CDecimalBD(new BigDecimal(value));
	}
	
	public static CDecimal ofRUB2(String value) {
		return of(value, RUB).withScale(2, RoundingMode.UNNECESSARY);
	}
	
	public static CDecimal ofRUB5(String value) {
		return of(value, RUB).withScale(5, RoundingMode.UNNECESSARY);
	}
	
	public static CDecimal ofUSD2(String value) {
		return of(value, USD).withScale(2, RoundingMode.UNNECESSARY);
	}
	
	public static CDecimal ofUSD5(String value) {
		return of(value, USD).withScale(5, RoundingMode.UNNECESSARY);
	}
	
	private final BigDecimal value;
	private final RoundingMode roundingMode;
	private final String unit;
	
	public CDecimalBD(BigDecimal value, String unit, RoundingMode roundingMode) {
		if ( value == null ) {
			throw new NullPointerException("Value cannot be null");
		}
		if ( roundingMode == null ) {
			throw new NullPointerException("Rounding mode cannot be null");
		}
		this.value = value;
		this.unit = unit;
		this.roundingMode = roundingMode;
	}
	
	public CDecimalBD(BigDecimal value, String unit) {
		this(value, unit, RoundingMode.HALF_UP);
	}
	
	public CDecimalBD(BigDecimal value) {
		this(value, null, RoundingMode.HALF_UP);
	}
	
	public CDecimalBD(String value, String unit, RoundingMode roundingMode) {
		this(new BigDecimal(value), unit, roundingMode);
	}
	
	public CDecimalBD(String value, String unit) {
		this(value, unit, RoundingMode.HALF_UP);
	}
	
	public CDecimalBD(String value) {
		this(value, null, RoundingMode.HALF_UP);
	}
	
	@Override
	public String getUnit() {
		return unit;
	}

	@Override
	public RoundingMode getRoundingMode() {
		return roundingMode;
	}

	@Override
	public int getScale() {
		return value.scale();
	}

	@Override
	public CDecimal abs() {
		return new CDecimalBD(value.abs(), unit, roundingMode);
	}

	@Override
	public CDecimal add(CDecimal augend) {
		if ( ! isSameUnitAs(augend) ) {
			throwMustBeOfSameUnit(augend);
		}
		return new CDecimalBD(value.add(augend.toBigDecimal()), unit, roundingMode);
	}
	
	@Override
	public CDecimal add(Long augend) {
		if ( ! isAbstract() ) {
			throwMustBeAbstract();
		}
		return new CDecimalBD(value.add(new BigDecimal(augend)), unit, roundingMode);
	}

	@Override
	public CDecimal divide(CDecimal divisor) {
		int scale = Math.max(value.scale(), divisor.getScale());
		String unit = null;
		if ( ! isSameUnitAs(divisor) ) {
			if ( divisor.isAbstract() ) {
				unit = getUnit();
			} else {
				// this is abstract and divisor is not or unit mismatch
				throwMustBeOfSameUnit(divisor);
			}
		}
		return new CDecimalBD(value.divide(divisor.toBigDecimal(),
				scale,
				roundingMode), unit, roundingMode);
	}

	@Override
	public CDecimal divide(Long divisor) {
		return new CDecimalBD(value.divide(new BigDecimal(divisor), getScale(), roundingMode),
				unit,
				roundingMode);
	}

	@Override
	public CDecimal divideExact(CDecimal divisor, int scale) {
		String unit = null;
		if ( ! isSameUnitAs(divisor) ) {
			if ( divisor.isAbstract() ) {
				unit = getUnit();
			} else {
				// this is abstract and divisor is not or unit mismatch
				throwMustBeOfSameUnit(divisor);
			}
		}
		return new CDecimalBD(value.divide(divisor.toBigDecimal(),
				scale,
				roundingMode), unit, roundingMode);
	}
	
	@Override
	public CDecimal divideExact(CDecimal divisor, int scale, RoundingMode roundingMode) {
		String unit = null;
		if ( ! isSameUnitAs(divisor) ) {
			if ( divisor.isAbstract() ) {
				unit = getUnit();
			} else {
				throwMustBeOfSameUnit(divisor);
			}
		}
		return new CDecimalBD(value.divide(divisor.toBigDecimal(), scale, roundingMode),
				unit,
				getRoundingMode());
	}

	@Override
	public CDecimal divideExact(Long divisor, int scale) {
		return new CDecimalBD(value.divide(new BigDecimal(divisor), scale, roundingMode),
				unit,
				roundingMode);
	}

	@Override
	public CDecimal multiply(CDecimal multiplier) {
		int scale = Math.max(value.scale(), multiplier.getScale());
		if ( ! multiplier.isAbstract() ) {
			throwMustBeAbstract(multiplier);
		}
		return new CDecimalBD(value.multiply(multiplier.toBigDecimal())
				.setScale(scale, roundingMode),
			unit,
			roundingMode);
	}

	@Override
	public CDecimal multiply(Long multiplier) {
		return new CDecimalBD(value.multiply(new BigDecimal(multiplier)), unit, roundingMode);
	}

	@Override
	public CDecimal multiplyExact(CDecimal multiplier) {
		if ( ! multiplier.isAbstract() ) {
			throwMustBeAbstract(multiplier);
		}
		return new CDecimalBD(value.multiply(multiplier.toBigDecimal()),
				unit,
				roundingMode);
	}
	
	@Override
	public CDecimal negate() {
		return new CDecimalBD(value.negate(), unit, roundingMode);
	}

	@Override
	public CDecimal subtract(CDecimal subtrahend) {
		if ( ! isSameUnitAs(subtrahend) ) {
			this.throwMustBeOfSameUnit(subtrahend);
		}
		return new CDecimalBD(value.subtract(subtrahend.toBigDecimal()),
				unit,
				roundingMode);
	}

	@Override
	public CDecimal withScale(int scale) {
		return new CDecimalBD(value.setScale(scale, roundingMode), unit, roundingMode);
	}
	
	@Override
	public CDecimal withScale(int scale, RoundingMode roundingMode) {
		return new CDecimalBD(value.setScale(scale, roundingMode), unit, this.roundingMode);
	}

	@Override
	public CDecimal withZero() {
		return new CDecimalBD(BigDecimal.ZERO.setScale(getScale(), roundingMode), unit, roundingMode);
	}

	@Override
	public CDecimal withUnit(String unit) {
		return new CDecimalBD(value, unit, roundingMode);
	}

	@Override
	public BigDecimal toBigDecimal() {
		return value;
	}
	
	@Override
	public CDecimal max(CDecimal other) {
		return compareTo(other) >= 0 ? this : other;
	}
	
	@Override
	public CDecimal min(CDecimal other) {
		if ( other == null ) {
			return this;
		}
		return compareTo(other) <= 0 ? this : other;
	}
	
	@Override
	public CDecimal toAbstract() {
		return unit == null ? this : new CDecimalBD(value, null, roundingMode);
	}
	
	@Override
	public boolean isAbstract() {
		return unit == null;
	}
	
	@Override
	public boolean isSameUnitAs(CDecimal other) {
		if ( unit == null ) {
			return other.getUnit() == null;
		}
		return unit.equals(other.getUnit());
	}
	
	@Override
	public CDecimal whenNull(CDecimal other) {
		return other == null ? this : other;
	}
	
	@Override
	public CDecimal sqrt(int scale) {
		BigDecimal two = new BigDecimal(2L);
		BigDecimal a = value;
		BigDecimal x0 = BigDecimal.ZERO;
		BigDecimal x1 = value.divide(two, RoundingMode.FLOOR);
		while ( ! x0.equals(x1) ) {
			x0 = x1;
			x1 = a.divide(x0, scale, RoundingMode.HALF_UP)
					.add(x0)
					.divide(two, scale, RoundingMode.HALF_UP);
		}
		return new CDecimalBD(x1, unit, roundingMode);
	}
	
	@Override
	public CDecimal pow(int n) {
		return new CDecimalBD(value.pow(n), unit, roundingMode);
	}
	
	private void throwMustBeOfSameUnit(CDecimal other) {
		String unit1 = this.unit, unit2 = other.getUnit();
		throw new IllegalArgumentException("Inconsistent values. The first one is in "
				+ unit1 + " second is in " + unit2);
	}
	
	private void throwMustBeAbstract() {
		throw new IllegalStateException("This value expected to be abstract but is in " + unit);
	}
	
	private void throwMustBeAbstract(CDecimal other) {
		throw new IllegalArgumentException("Operand expected to be abstract but is in "
				+ other.getUnit());
	}

}
