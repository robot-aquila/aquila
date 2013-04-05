package ru.prolib.aquila.ib.getter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.ib.event.IBEventUpdateAccount;

/**
 * Геттер вещественного на основе события {@link IBEventUpdateAccount}.
 * <p>
 * 2012-12-02<br>
 * $Id: IBGetAccountDouble.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBGetAccountDouble implements G<Double> {
	private final String currency;
	private final String key;
	
	/**
	 * Конструктор.
	 * <p>
	 * В данном случае создается объект, который не фильтрует значения по коду
	 * валюты. То есть любое значение, ключ которого соответствует указанному,
	 * будет преобразовано в вещественное независимо от того, в какой валюте
	 * оно выражено. 
	 * <p>
	 * @param key ключ, по которому идентифицируется значение
	 */
	public IBGetAccountDouble(String key) {
		this(key, null);
	}

	/**
	 * Конструктор.
	 * <p>
	 * В данном случае преобразование выполняется только для тех значений,
	 * которые совпадают как по ключу так и по коду валюты.
	 * <p>
	 * @param key ключ, по которому идентифицируется значение
	 * @param currency код валюты
	 */
	public IBGetAccountDouble(String key, String currency) {
		super();
		this.key = key;
		this.currency = currency;
	}
	
	/**
	 * Получить ключ.
	 * <p>
	 * @return ключ
	 */
	public String getKey() {
		return key;
	}
	
	/**
	 * Получить код валюты.
	 * <p>
	 * @return код валюты
	 */
	public String getCurrency() {
		return currency;
	}

	@Override
	public Double get(Object source) {
		if ( !(source instanceof IBEventUpdateAccount) ) {
			return null;
		}
		IBEventUpdateAccount event = (IBEventUpdateAccount) source;
		if ( ! key.equals(event.getKey()) ) {
			return null;
		}
		if ( currency != null && ! currency.equals(event.getCurrency()) ) {
			return null;
		}
		try {
			return Double.parseDouble(event.getValue());
		} catch ( Exception e ) {
			// TODO: куда-то проинформировать
		}
		return null;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other != null && other.getClass() == getClass() ) {
			return fieldsEquals(other);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121203, 195457)
			.append(key)
			.append(currency)
			.toHashCode();
	}
	
	protected boolean fieldsEquals(Object other) {
		IBGetAccountDouble o = (IBGetAccountDouble) other;
		return new EqualsBuilder()
			.append(key, o.key)
			.append(currency, o.currency)
			.isEquals();
	}

}
