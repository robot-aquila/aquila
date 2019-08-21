package ru.prolib.aquila.exante;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class XSymbol {
	private final String symbol, securityID, exchangeID, cfi, currency;
	
	public XSymbol(String symbol,
			String securityID,
			String exchangeID,
			String cfi_code,
			String currency)
	{
		this.symbol = symbol;
		this.securityID = securityID;
		this.exchangeID = exchangeID;
		this.cfi = cfi_code;
		this.currency = currency;
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	public String getSecurityID() {
		return securityID;
	}
	
	public String getExchangeID() {
		return exchangeID;
	}
	
	public String getCFICode() {
		return cfi;
	}
	
	public String getCurrency() {
		return currency;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(728181, 432763)
				.append(symbol)
				.append(securityID)
				.append(exchangeID)
				.append(cfi)
				.append(currency)
				.build();
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != XSymbol.class ) {
			return false;
		}
		XSymbol o = (XSymbol) other;
		return new EqualsBuilder()
				.append(o.symbol, symbol)
				.append(o.securityID, securityID)
				.append(o.exchangeID, exchangeID)
				.append(o.cfi, cfi)
				.append(o.currency, currency)
				.build();
	}

}
