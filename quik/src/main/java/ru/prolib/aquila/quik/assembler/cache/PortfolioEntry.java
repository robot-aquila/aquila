package ru.prolib.aquila.quik.assembler.cache;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.Account;

/**
 * Кэш строки таблицы портфелей.
 * <p>
 * Данный класс используется для представления строки портфеля с полным счетом.
 */
public class PortfolioEntry extends CacheEntry {
	private final Account account;
	private final Double balance;
	private final Double cash;
	private final Double varMargin;

	/**
	 * Конструкто.
	 * <p>
	 * @param account торговый счет
	 * @param balance баланс
	 * @param cash доступные средства в деньгах
	 * @param varMargin вариационка
	 */
	public PortfolioEntry(Account account, Double balance,
			Double cash, Double varMargin)
	{
		super();
		this.account = account;
		this.balance = balance;
		this.cash = cash;
		this.varMargin = varMargin;
	}
	
	public Account getAccount() {
		return account;
	}
	
	public Double getBalance() {
		return balance;
	}
	
	public Double getCash() {
		return cash;
	}
	
	public Double getVarMargin() {
		return varMargin;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null ) {
			return false;
		}
		if ( other.getClass() != PortfolioEntry.class ) {
			return false;
		}
		PortfolioEntry o = (PortfolioEntry) other;
		return new EqualsBuilder()
			.append(account, o.account)
			.append(balance, o.balance)
			.append(cash, o.cash)
			.append(varMargin, o.varMargin)
			.isEquals();
	}
	
}
