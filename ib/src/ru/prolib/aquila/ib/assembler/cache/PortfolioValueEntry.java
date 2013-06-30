package ru.prolib.aquila.ib.assembler.cache;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.Account;

/**
 * Кэш-запись атрибута портфеля.
 */
public class PortfolioValueEntry extends CacheEntry {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(PortfolioValueEntry.class);
	}
	
	private final String accountName, key, currency, value;
	private final Account account;
	
	public PortfolioValueEntry(String accountName, String key,
			String currency, String value)
	{
		super();
		this.accountName = accountName;
		this.account = new Account(accountName);
		this.key = key;
		this.currency = currency;
		this.value = value;
	}
	
	/**
	 * Получить код торгового счета.
	 * <p>
	 * @return код счета
	 */
	public String getAccountName() {
		return accountName;
	}
	
	/**
	 * Получить объект торгового счета.
	 * <p>
	 * @return счет
	 */
	public Account getAccount() {
		return account;
	}
	
	/**
	 * Получить ключ значения.
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
	
	/**
	 * Конвертировать в вещественное значение.
	 * <p>
	 * @return вещественное или null, если конвертировать не удалось
	 */
	public Double getDouble() {
		try {
			return Double.parseDouble(value);
		} catch ( NumberFormatException e ) {
			Object args[] = { key, currency, value };
			logger.error("Cannot convert {}/{} value to double: {}", args);
			return null;
		}
	}
	
	/**
	 * Получить исходное значение.
	 * <p>
	 * @return исходное значение
	 */
	public String getString() {
		return value;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != PortfolioValueEntry.class ) {
			return false;
		}
		PortfolioValueEntry o = (PortfolioValueEntry) other;
		return new EqualsBuilder()
			.append(accountName, o.accountName)
			.append(key, o.key)
			.append(value, o.value)
			.append(currency, o.currency)
			.isEquals();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "["
			+ "acnt=" + accountName + ", "
			+ "key=" + key + ", "
			+ "cur=" + currency + ", "
			+ "val=" + value + "]";
	}

}
