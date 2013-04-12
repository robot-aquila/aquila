package ru.prolib.aquila.ib.event;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.EventType;

/**
 * Событие: получен список торговых счетов.
 * <p>
 * 2012-11-26<br>
 * $Id: IBEventAccounts.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBEventAccounts extends IBEvent {
	private final String accounts;

	/**
	 * Конструктор.
	 * <p>
	 * @param type тип события
	 * @param accounts список счетов через запятую
	 */
	public IBEventAccounts(EventType type, String accounts ) {
		super(type);
		this.accounts = accounts;
	}
	
	/**
	 * Получить список счетов.
	 * <p>
	 * @return список счетов через запятую
	 */
	public String getAccounts() {
		return accounts;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == IBEventAccounts.class ) {
			IBEventAccounts o = (IBEventAccounts) other;
			return new EqualsBuilder()
				.append(accounts, o.accounts)
				.append(getType(), o.getType())
				.isEquals();
		} else {
			return false;
		}
	}

}
