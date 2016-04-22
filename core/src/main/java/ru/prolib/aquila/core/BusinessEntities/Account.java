package ru.prolib.aquila.core.BusinessEntities;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Торговый счет.
 * <p>
 * Объекты данного типа характеризуют торговый счет клиента (например депо).
 * В рамках одного портфеля могут быть доступны несколько клиентский счетов.
 * <p>
 * Некоторые терминалы комбинации кодов для идентификации отдельного
 * счета. Например, терминал QUIK для портфелей ММВБ использует комбинацию,
 * состоящую из кода фирмы, кода клиента и кода счета-депо.
 * <p>
 * 2012-09-05<br>
 * $Id$
 */
public class Account implements Comparable<Account> {
	/**
	 * Счет по-умолчанию.
	 * <p>
	 * Данный экземпляр счета используется в случае, если терминал не
	 * предусматривает разделение доступа по счетам.
	 */
	public static final Account DEFAULT = new Account("DEFAULT");
	
	private final String code;
	private final String subCode;
	private final String subCode2;
	
	public Account(String code, String subCode, String subCode2) {
		super();
		this.code = code;
		this.subCode = subCode;
		this.subCode2 = subCode2;
	}
	
	public Account(String code, String subCode) {
		this(code, subCode, null);
	}
	
	public Account(String code) {
		this(code, null, null);
	}
	
	/**
	 * Получить код счета.
	 * <p>
	 * @return код счета
	 */
	public String getCode() {
		return code;
	}
	
	/**
	 * Получить суб-код счета.
	 * <p>
	 * @return суб-код
	 */
	public String getSubCode() {
		return subCode;
	}
	
	/**
	 * Получить вторичный суб-код счета.
	 * <p>
	 * @return вторичный суб-код
	 */
	public String getSubCode2() {
		return subCode2;
	}
	
	@Override
	public String toString() {
		String result = (subCode2 == null ? "" : "@" + subCode2);
		if ( subCode != null || result.length() > 0 ) {
			result = "#" + subCode + result;
		}
		if ( code != null || result.length() > 0 ) {
			result = code + result;
		}
		return result.length() > 0 ? result : "null";
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == Account.class ) {
			Account o = (Account) other;
			return new EqualsBuilder()
				.append(code, o.code)
				.append(subCode, o.subCode)
				.append(subCode2, o.subCode2)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(code)
			.append(subCode)
			.append(subCode2)
			.toHashCode();
	}

	@Override
	public int compareTo(Account o) {
		return toString().compareTo(o.toString());
	}

}
