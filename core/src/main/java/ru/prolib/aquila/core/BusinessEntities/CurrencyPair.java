package ru.prolib.aquila.core.BusinessEntities;

import java.util.Currency;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Валютная пара.
 */
public class CurrencyPair {
	private final Currency base, counter;
	
	public CurrencyPair(Currency base, Currency counter) {
		super();
		this.base = base;
		this.counter = counter;
	}
	
	public CurrencyPair(String baseCode, String counterCode) {
		this(Currency.getInstance(baseCode), Currency.getInstance(counterCode));
	}
	
	/**
	 * Получить базисную валюту.
	 * <p>
	 * @return валюта
	 */
	public Currency getBaseCurrency() {
		return base;
	}
	
	/**
	 * Получить контрвалюту.
	 * <p>
	 * @return валюта
	 */
	public Currency getCounterCurrency() {
		return counter;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != CurrencyPair.class ) {
			return false;
		}
		CurrencyPair o = (CurrencyPair) other;
		return new EqualsBuilder()
			.append(o.base, base)
			.append(o.counter, counter)
			.isEquals();
	}

}
