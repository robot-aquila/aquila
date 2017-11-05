package ru.prolib.aquila.core.BusinessEntities;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Consistent decimal.
 * <p>
 * Consistent decimal is a decimal number which may be abstract or tied to
 * units. This can be used to represent any kind of numeric values like
 * physical values, money or just abstract numbers. Basic consistency control
 * makes possible to get more strict control when doing math operations.
 * <p>
 * Different implementations are focused on specific tasks or special cases
 * of use. For example, the basic implementation is universal and can be used
 * to represent any possible values. Implementation based on long may be used
 * to reduce memory usages for cases when such precision is enough to pass
 * huge amount of small numbers (like tick data).
 */
public interface CDecimal extends Comparable<CDecimal> {
	
	String getUnit();
	RoundingMode getRoundingMode();
	int getScale();
	CDecimal abs();
	
	/**
	 * Get result of adding this value to augend.
	 * <p>
	 * @param augend - value to be added to this. Augend must be of same
	 * unit as this.
	 * @return this + augend. The scale of result is max(this scale, augend
	 * scale)
	 * @throws IllegalArgumentException - augend is of different unit
	 */
	CDecimal add(CDecimal augend);
	
	/**
	 * Get result of adding this abstract value to augend.
	 * <p>
	 * @param augend - value to be added to this.
	 * @return this + augend. The scale of result is scale of this.
	 * @throws IllegalStateException - this value is not abstract
	 */
	CDecimal add(Long augend);
	
	/**
	 * Get result of dividing this by divisor.
	 * <p>
	 * @param divisor - value by which this value is to be divided. If divisor
	 * of same unit as this the the result is abstract. If divisor is abstract
	 * and this is not abstract then the result is of same unit as this. If
	 * both are abstract then the result is also abstract. If divisor is not
	 * abstract and this is abstract then exception will be thrown.
	 * @return this / divisor. The scale of result is max(this scale, divisor
	 * scale)
	 * @throws IllegalArgumentException - non-abstract divisor when this is
	 * abstract
	 */
	CDecimal divide(CDecimal divisor);
	
	/**
	 * Get result of dividing this value by abstract divisor.
	 * <p>
	 * @param divisor - value by which this value is to be divided.
	 * @return this / divisor in units of this. The scale of result is scale of
	 * this.
	 */
	CDecimal divide(Long divisor);
	
	/**
	 * Get result of dividing this by divisor exact to specified scale.
	 * <p>
	 * The rounding mode of this will be used to round the result.
	 * <p>
	 * @param divisor - value by which this value is to be divided. If divisor
	 * of same unit as this the the result is abstract. If divisor is abstract
	 * and this is not abstract then the result is of same unit as this. If
	 * both are abstract then the result is also abstract. If divisor is not
	 * abstract and this is abstract then exception will be thrown.
	 * @param scale - scale to round the result
	 * @return this / divisor.
	 * @throws IllegalArgumentException - non-abstract divisor when this is
	 * abstract
	 */
	CDecimal divideExact(CDecimal divisor, int scale);
	
	/**
	 * Get result of dividing this by divisor exact to specified scale and
	 * rounding mode.
	 * <p>
	 * @param divisor - value by which this value is to be divided. If divisor
	 * of same unit as this then the result is abstract. If divisor is abstract
	 * and this is not abstract then the result is of same unit as this. If
	 * both are abstract the the result is also abstract.  If divisor is not
	 * abstract and this is abstract then exception will be thrown.
	 * @param scale - scale to round the result
	 * @param roundingMode -  rounding mode to round the result. It will be
	 * used to round the result of this operation but the result will be
	 * created with rounding mode of this object.
	 * @return this / divisor
	 * @throws IllegalArgumentException - non-abstract divisor when this is
	 * abstract
	 */
	CDecimal divideExact(CDecimal divisor, int scale, RoundingMode roundingMode);
	
	/**
	 * Get result of dividing this value by abstract divisor exact to specified
	 * scale.
	 * <p>
	 * @param divisor - value by which this value is to be divided
	 * @param scale - scale to round the result
	 * @return this / divisor in units of this.
	 */
	CDecimal divideExact(Long divisor, int scale);
	
	/**
	 * Get result of multiplying this by multiplier.
	 * <p>
	 * @param multiplier - value by which this is to be multiplied.
	 * Multiplier must be abstract.
	 * @return this * multiplier in units of this. The scale of result is
	 * max(this scale, multiplier scale)
	 * @throws IllegalArgumentException - non-abstract multiplier
	 */
	CDecimal multiply(CDecimal multiplier);
	
	/**
	 * Get result of multiplying this by abstract multiplier.
	 * <p>
	 * @param multiplier - value by which this is to be multiplied
	 * @return this * multiplier in units of this. The scale of result is
	 * scale of this.
	 */
	CDecimal multiply(Long multiplier);
	
	/**
	 * Get result of multiplying this by multiplier with maximum precision.
	 * <p>
	 * @param multiplier - value by which this is to be multiplied.
	 * Multiplier must be abstract.
	 * @return this * multiplier in units of this. The scale of result is
	 * this scale + multiplier scale.
	 * @throws IllegalArgumentException - non-abstract multiplier
	 */
	CDecimal multiplyExact(CDecimal multiplier);
	
	CDecimal subtract(CDecimal subtrahend);
	
	CDecimal negate();
	CDecimal withScale(int scale);
	CDecimal withScale(int scale, RoundingMode roundingMode);
	CDecimal withZero();
	CDecimal withUnit(String unit);
	BigDecimal toBigDecimal();
	CDecimal max(CDecimal other);
	CDecimal min(CDecimal other);
	CDecimal toAbstract();
	boolean isAbstract();
	boolean isSameUnitAs(CDecimal other);
	
	/**
	 * Check that other value is not null and return it or use this as default.
	 * <p>
	 * @param other - value to check
	 * @return this value if other is null, otherwise return other value 
	 */
	CDecimal whenNull(CDecimal other);
	
}
