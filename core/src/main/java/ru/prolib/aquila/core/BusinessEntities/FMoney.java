package ru.prolib.aquila.core.BusinessEntities;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class FMoney extends FDecimal {
	public static final int VERSION = 1;
	public static final String USD = "USD";
	public static final String EUR = "EUR";
	public static final String RUB = "RUB";
	public static final String JPY = "JPY";
	public static final String CAD = "CAD";
	
	public static final FMoney ZERO_USD0 = FMoney.of(0, 0, USD);
	public static final FMoney ZERO_USD1 = FMoney.ofUSD1(0);
	public static final FMoney ZERO_USD2 = FMoney.ofUSD2(0);
	public static final FMoney ZERO_USD3 = FMoney.ofUSD3(0);
	public static final FMoney ZERO_USD4 = FMoney.ofUSD4(0);
	public static final FMoney ZERO_USD5 = FMoney.ofUSD5(0);
	
	public static final FMoney ZERO_EUR0 = FMoney.of(0, 0, EUR);
	public static final FMoney ZERO_EUR1 = FMoney.ofEUR1(0);
	public static final FMoney ZERO_EUR2 = FMoney.ofEUR2(0);
	public static final FMoney ZERO_EUR3 = FMoney.ofEUR3(0);
	public static final FMoney ZERO_EUR4 = FMoney.ofEUR4(0);
	public static final FMoney ZERO_EUR5 = FMoney.ofEUR5(0);
	
	public static final FMoney ZERO_RUB0 = FMoney.of(0, 0, RUB);
	public static final FMoney ZERO_RUB1 = FMoney.ofRUB1(0);
	public static final FMoney ZERO_RUB2 = FMoney.ofRUB2(0);
	public static final FMoney ZERO_RUB3 = FMoney.ofRUB3(0);
	public static final FMoney ZERO_RUB4 = FMoney.ofRUB4(0);
	public static final FMoney ZERO_RUB5 = FMoney.ofRUB5(0);
	
	protected final String currencyCode;
	
	public static FDecimal of(String value) {
		throw new UnsupportedOperationException();
	}
	
	public static FDecimal of(String value, int scale) {
		throw new UnsupportedOperationException();
	}
	
	public static FDecimal of(double value, int scale) {
		throw new UnsupportedOperationException();
	}
	
	public static FDecimal of(String value, int scale, RoundingMode roundingMode) {
		throw new UnsupportedOperationException();
	}
	
	public static FDecimal of(BigDecimal value) {
		throw new UnsupportedOperationException();
	}

	public static FDecimal of(BigDecimal value, int scale) {
		throw new UnsupportedOperationException();
	}

	public static FDecimal of(BigDecimal value, int scale, RoundingMode roundingMode) {
		throw new UnsupportedOperationException();
	}
	
	public static FDecimal of0(String value) {
		throw new UnsupportedOperationException();
	}
	
	public static FDecimal of1(String value) {
		throw new UnsupportedOperationException();
	}

	public static FDecimal of2(String value) {
		throw new UnsupportedOperationException();
	}

	public static FDecimal of3(String value) {
		throw new UnsupportedOperationException();
	}

	public static FDecimal of4(String value) {
		throw new UnsupportedOperationException();
	}
	
	public static FDecimal of0(double value) {
		throw new UnsupportedOperationException();
	}

	public static FDecimal of1(double value) {
		throw new UnsupportedOperationException();
	}

	public static FDecimal of2(double value) {
		throw new UnsupportedOperationException();
	}

	public static FDecimal of3(double value) {
		throw new UnsupportedOperationException();
	}

	public static FDecimal of4(double value) {
		throw new UnsupportedOperationException();
	}
	
	public static FMoney of(String value, String currencyCode) {
		return new FMoney(value, currencyCode);
	}
	
	public static FMoney of(String value, int scale, String currencyCode) {
		return new FMoney(value, scale, currencyCode);
	}
	
	public static FMoney of(double value, int scale, String currencyCode) {
		return new FMoney(value, scale, currencyCode);
	}
	
	public static FMoney of(BigDecimal value, String currencyCode) {
		return new FMoney(value, currencyCode);
	}
	
	public static FMoney of(BigDecimal value, int scale, String currencyCode) {
		return new FMoney(value, currencyCode).withScale(scale);
	}
	
	public static FMoney ofUSD(String value, int scale) {
		return of(value, scale, USD);
	}
	
	public static FMoney ofUSD(double value, int scale) {
		return of(value, scale, USD);
	}
	
	public static FMoney ofUSD1(String value) {
		return ofUSD(value, 1);
	}

	public static FMoney ofUSD2(String value) {
		return ofUSD(value, 2);
	}

	public static FMoney ofUSD3(String value) {
		return ofUSD(value, 3);
	}

	public static FMoney ofUSD4(String value) {
		return ofUSD(value, 4);
	}

	public static FMoney ofUSD5(String value) {
		return ofUSD(value, 5);
	}

	public static FMoney ofUSD1(double value) {
		return ofUSD(value, 1);
	}

	public static FMoney ofUSD2(double value) {
		return ofUSD(value, 2);
	}

	public static FMoney ofUSD3(double value) {
		return ofUSD(value, 3);
	}

	public static FMoney ofUSD4(double value) {
		return ofUSD(value, 4);
	}

	public static FMoney ofUSD5(double value) {
		return ofUSD(value, 5);
	}

	public static FMoney ofEUR(String value, int scale) {
		return of(value, scale, EUR);
	}
	
	public static FMoney ofEUR(double value, int scale) {
		return of(value, scale, EUR);
	}
	
	public static FMoney ofEUR1(String value) {
		return ofEUR(value, 1);
	}

	public static FMoney ofEUR2(String value) {
		return ofEUR(value, 2);
	}

	public static FMoney ofEUR3(String value) {
		return ofEUR(value, 3);
	}

	public static FMoney ofEUR4(String value) {
		return ofEUR(value, 4);
	}

	public static FMoney ofEUR5(String value) {
		return ofEUR(value, 5);
	}

	public static FMoney ofEUR1(double value) {
		return ofEUR(value, 1);
	}

	public static FMoney ofEUR2(double value) {
		return ofEUR(value, 2);
	}

	public static FMoney ofEUR3(double value) {
		return ofEUR(value, 3);
	}

	public static FMoney ofEUR4(double value) {
		return ofEUR(value, 4);
	}

	public static FMoney ofEUR5(double value) {
		return ofEUR(value, 5);
	}

	public static FMoney ofRUB(String value, int scale) {
		return of(value, scale, RUB);
	}
	
	public static FMoney ofRUB(double value, int scale) {
		return of(value, scale, RUB);
	}

	public static FMoney ofRUB1(String value) {
		return ofRUB(value, 1);
	}

	public static FMoney ofRUB2(String value) {
		return ofRUB(value, 2);
	}

	public static FMoney ofRUB3(String value) {
		return ofRUB(value, 3);
	}

	public static FMoney ofRUB4(String value) {
		return ofRUB(value, 4);
	}

	public static FMoney ofRUB5(String value) {
		return ofRUB(value, 5);
	}

	public static FMoney ofRUB1(double value) {
		return ofRUB(value, 1);
	}

	public static FMoney ofRUB2(double value) {
		return ofRUB(value, 2);
	}

	public static FMoney ofRUB3(double value) {
		return ofRUB(value, 3);
	}

	public static FMoney ofRUB4(double value) {
		return ofRUB(value, 4);
	}

	public static FMoney ofRUB5(double value) {
		return ofRUB(value, 5);
	}

	private void checkCurrency(FMoney argument) {
		if ( ! currencyCode.equals(argument.currencyCode) ) {
			throw new IllegalArgumentException("Currency mismatch: "
				+ argument.currencyCode + " (expected " + currencyCode + ")");
		}
	}

	public FMoney(BigDecimal value, RoundingMode roundingMode, String currency) {
		super(value, roundingMode);
		this.currencyCode = currency;
	}
	
	public FMoney(BigDecimal value, String currency) {
		super(value);
		this.currencyCode = currency;
	}
	
	public FMoney(double value, int scale, RoundingMode roundingMode, String currency) {
		super(value, scale, roundingMode);
		this.currencyCode = currency;
	}
	
	public FMoney(double value, int scale, String currency) {
		super(value, scale);
		this.currencyCode = currency;
	}
	
	public FMoney(String value, RoundingMode roundingMode, String currency) {
		super(value, roundingMode);
		this.currencyCode = currency;
	}
	
	public FMoney(String value, String currency) {
		super(value, RoundingMode.HALF_UP);
		this.currencyCode = currency;
	}
	
	public FMoney(String value, int scale, RoundingMode roundingMode, String currency) {
		super(value, scale, roundingMode);
		this.currencyCode = currency;
	}
	
	public FMoney(String value, int scale, String currency) {
		super(value, scale);
		this.currencyCode = currency;
	}
	
	public FMoney() {
		this("0", USD);
	}
	
	public String getCurrencyCode() {
		return currencyCode;
	}
	
	@Override
	public int compareTo(FDecimal other) {
		if ( other == null ) {
			return 1;
		}
		if ( other.getClass() == FMoney.class ) {
			FMoney o = (FMoney) other;
			int r = super.compareTo(o);
			if ( r == 0 ) {
				r = currencyCode.compareTo(o.currencyCode);
				if ( r == 0 ) {
					return 0;
				} else {
					return r > 0 ? 1 : -1;
				}
			} else {
				return r;
			}
		} else {
			return super.compareTo(other);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if( other == null || other.getClass() != FMoney.class ) {
			return false;
		}
		FMoney o = (FMoney) other;
		return new EqualsBuilder()
			.append(value, o.value)
			.append(roundingMode, o.roundingMode)
			.append(currencyCode, o.currencyCode)
			.isEquals();
	}
	
	@Override
	public FMoney withScale(int scale) {
		return new FMoney(value.setScale(scale, roundingMode), roundingMode, currencyCode);
	}
	
	@Override
	public FMoney subtract(FDecimal subtrahend) {
		return new FMoney(value.subtract(subtrahend.value), roundingMode, currencyCode);
	}
	
	public FMoney subtract(FMoney subtrahend) {
		checkCurrency(subtrahend);
		return new FMoney(value.subtract(subtrahend.value), roundingMode, currencyCode);
	}
	
	@Override
	public FMoney add(FDecimal augend) {
		return new FMoney(value.add(augend.value), roundingMode, currencyCode);
	}
	
	public FMoney add(FMoney augend) {
		checkCurrency(augend);
		return new FMoney(value.add(augend.value), roundingMode, currencyCode);
	}
	
	public String toStringWithCurrency() {
		return super.toString() + " " + currencyCode;
	}
	
	@Override
	public FMoney withZero() {
		return new FMoney(0, value.scale(), roundingMode, currencyCode);
	}
	
	@Override
	public FMoney multiply(FDecimal multiplicand) {
		FDecimal x = super.multiply(multiplicand);
		return new FMoney(x.value, roundingMode, currencyCode);
	}
	
	@Override
	public FMoney multiplyExact(FDecimal multiplicand) {
		FDecimal x = super.multiplyExact(multiplicand);
		return new FMoney(x.value, roundingMode, currencyCode);
	}
	
	public FMoney multiply(FMoney multiplicand) {
		FMoney x = multiply((FDecimal) multiplicand);
		checkCurrency(multiplicand);
		return x;
	}
	
	public FMoney multiplyExact(FMoney multiplicand) {
		FMoney x = multiplyExact((FDecimal) multiplicand);
		checkCurrency(multiplicand);
		return x;
	}
	
	@Override
	public FMoney multiply(Long multiplicand) {
		FDecimal x = super.multiply(multiplicand);
		return new FMoney(x.value, roundingMode, currencyCode);
	}
	
	@Override
	public FMoney negate() {
		FDecimal x = super.negate();
		return new FMoney(x.value, roundingMode, currencyCode);
	}

	@Override
	public FMoney divide(FDecimal divisor) {
		FDecimal x = super.divide(divisor);
		return new FMoney(x.value, x.roundingMode, currencyCode);
	}
	
	public FMoney divide(FMoney divisor) {
		FDecimal x = super.divide(divisor);
		checkCurrency(divisor);
		return new FMoney(x.value, x.roundingMode, currencyCode);
	}
	
	@Override
	public FMoney divide(Long divisor) {
		FDecimal x = super.divide(divisor);
		return new FMoney(x.value, x.roundingMode, currencyCode);
	}
	
	@Override
	public FMoney divide(FDecimal divisor, int scale, RoundingMode roundingMode) {
		FDecimal x = super.divide(divisor, scale, roundingMode);
		return new FMoney(x.value, x.roundingMode, currencyCode);
	}

	public FMoney divide(FMoney divisor, int scale, RoundingMode roundingMode) {
		FDecimal x = super.divide(divisor, scale, roundingMode);
		checkCurrency(divisor);
		return new FMoney(x.value, x.roundingMode, currencyCode);
	}
	
	@Override
	public FMoney divide(Long divisor, int scale, RoundingMode roundingMode) {
		FDecimal x = super.divide(divisor, scale, roundingMode);
		return new FMoney(x.value, x.roundingMode, currencyCode);
	}
	
	@Override
	public FMoney abs() {
		FDecimal x = super.abs();
		return new FMoney(x.value, x.roundingMode, currencyCode);
	}

}
