package ru.prolib.aquila.core.BusinessEntities;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class FMoney extends FDecimal {
	protected final String currencyCode;
	
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
		this("0", "USD");
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

}
