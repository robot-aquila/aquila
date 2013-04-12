package ru.prolib.aquila.ib.event;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.EventType;

/**
 * Событие: обновление атрибута счета.
 * <p>
 * 2012-11-26<br>
 * $Id: IBEventUpdateAccount.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBEventUpdateAccount extends IBEvent {
	private final String key;
	private final String value;
	private final String currency;
	private final String account;

	/**
	 * Конструктор.
	 * <p>
	 * @param type тип события
	 * @param key тип значения
	 * @param value значение
	 * @param currency валюта для денежных значений
	 * @param account торговый счет
	 */
	public IBEventUpdateAccount(EventType type, String key,
			String value, String currency, String account)
	{
		super(type);
		this.key = key;
		this.value = value;
		this.currency = currency;
		this.account = account;
	}
	
	/**
	 * Конструктор на основании существующего события.
	 * <p>
	 * @param type тип события
	 * @param event событие-основание
	 */
	public IBEventUpdateAccount(EventType type, IBEventUpdateAccount event) {
		this(type, event.key, event.value, event.currency, event.account);
	}
	
	/**
	 * Получить тип значени.
	 * <p>
	 * @return идентификатор типа
	 */
	public String getKey() {
		return key;
	}
	
	/**
	 * Получить строковое представление значения.
	 * <p>
	 * @return значение
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * Получить код валюты.
	 * <p>
	 * @return код валюты
	 */
	public String getCurrency() {
		return currency;
	}
	
	/**
	 * Получить код счета.
	 * <p>
	 * @return код счета
	 */
	public String getAccount() {
		return account;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == IBEventUpdateAccount.class ) {
			IBEventUpdateAccount o = (IBEventUpdateAccount) other;
			return new EqualsBuilder()
				.append(getType(), o.getType())
				.append(key, o.key)
				.append(value, o.value)
				.append(currency, o.currency)
				.append(account, o.account)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return "IBUpdAcc " + account + ":" + key + "=" + value + " " + currency;
	}

}
