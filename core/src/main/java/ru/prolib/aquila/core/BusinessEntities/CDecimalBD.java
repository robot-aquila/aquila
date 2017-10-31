package ru.prolib.aquila.core.BusinessEntities;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Consistent decimal based on {@link java.math.BigDecimal BigDecimal}.
 */
public class CDecimalBD extends CDecimalAbstract {
	private final BigDecimal value;
	private final RoundingMode roundingMode;
	private final String unit;
	
	public CDecimalBD(BigDecimal value, String unit, RoundingMode roundingMode) {
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
		return new CDecimalBD(value.add(augend.toBigDecimal()),
				checkUnit(augend),
				roundingMode);
	}
	
	@Override
	public CDecimal add(Long augend) {
		return new CDecimalBD(value.add(new BigDecimal(augend)), unit, roundingMode);
	}

	@Override
	public CDecimal divide(CDecimal divisor) {
		return new CDecimalBD(value.divide(divisor.toBigDecimal(),
				Math.max(value.scale(), divisor.getScale()),
				roundingMode),
			checkUnit(divisor), roundingMode);
	}

	@Override
	public CDecimal divide(Long divisor) {
		return new CDecimalBD(value.divide(new BigDecimal(divisor), getScale(), roundingMode),
				unit,
				roundingMode);
	}

	@Override
	public CDecimal divideExact(CDecimal divisor, int scale) {
		return new CDecimalBD(value.divide(divisor.toBigDecimal(), scale, roundingMode),
				checkUnit(divisor),
				roundingMode);
	}

	@Override
	public CDecimal divideExact(Long divisor, int scale) {
		return new CDecimalBD(value.divide(new BigDecimal(divisor), scale, roundingMode),
				unit,
				roundingMode);
	}

	@Override
	public CDecimal multiply(CDecimal multiplicand) {
		return new CDecimalBD(value.multiply(multiplicand.toBigDecimal())
					.setScale(Math.max(value.scale(), multiplicand.getScale()), roundingMode),
				checkUnit(multiplicand),
				roundingMode);
	}

	@Override
	public CDecimal multiply(Long multiplicand) {
		return new CDecimalBD(value.multiply(new BigDecimal(multiplicand)), unit, roundingMode);
	}

	@Override
	public CDecimal multiplyExact(CDecimal multiplicand) {
		return new CDecimalBD(value.multiply(multiplicand.toBigDecimal()),
				checkUnit(multiplicand),
				roundingMode);
	}
	
	@Override
	public CDecimal negate() {
		return new CDecimalBD(value.negate(), unit, roundingMode);
	}

	@Override
	public CDecimal subtract(CDecimal subtrahend) {
		return new CDecimalBD(value.subtract(subtrahend.toBigDecimal()),
				checkUnit(subtrahend),
				roundingMode);
	}

	@Override
	public CDecimal subtract(Long subtrahend) {
		return new CDecimalBD(value.subtract(new BigDecimal(subtrahend)), unit, roundingMode);
	}

	@Override
	public CDecimal withScale(int scale) {
		return new CDecimalBD(value.setScale(scale, roundingMode), unit, roundingMode);
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
	
	private String checkUnit(CDecimal other) {
		String unit1 = this.unit, unit2 = other.getUnit();
		if ( unit1 == null ) {
			unit1 = unit2;
		} else if ( unit2 != null && ! unit2.equals(unit1) ) {
			throw new IllegalArgumentException("Inconsistent operands: the first one is " + unit1 + " but second is " + unit2);
		}
		return unit1;
	}

}
