package ru.prolib.aquila.core.BusinessEntities;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Consistent decimal.
 * <p>
 * Consistent decimal is suitable to represent any possible types of
 * arbitrary-precision numbers.  It is a decimal number which may be
 * abstract or tied to units (for example currency code, symbol code,
 * pcs., percents, etc...). Abstract numbers do not tied to a unit
 * (the unit value is null). Any math operation which requires an
 * argument expects an abstract number or number with same unit.
 * If operand is of different unit then exception will be thrown.
 * Such approach provides minimal coherence checking.
 * <p>
 * Different implementations are focused on specific tasks or special
 * cases of use. For example, a basic implementation is universal and
 * can be used to represent any possible values. L64 implementation
 * is based on long and may be used to reduce memory usages for cases
 * when such precision is enough to pass huge amount of small numbers
 * (like tick data).
 */
public interface CDecimal extends Comparable<CDecimal> {
	
	String getUnit();
	RoundingMode getRoundingMode();
	int getScale();
	CDecimal abs();
	CDecimal add(CDecimal augent);
	CDecimal add(Long augend);
	CDecimal divide(CDecimal divisor);
	CDecimal divide(Long divisor);
	CDecimal divideExact(CDecimal divisor, int scale);
	CDecimal divideExact(Long divisor, int scale);
	CDecimal multiply(CDecimal multiplicand);
	CDecimal multiply(Long multiplicand);
	CDecimal multiplyExact(CDecimal multiplicand);
	CDecimal negate();
	CDecimal subtract(CDecimal subtrahend);
	CDecimal subtract(Long subtrahend);
	CDecimal withScale(int scale);
	CDecimal withZero();
	CDecimal withUnit(String unit);
	BigDecimal toBigDecimal();
	
}
