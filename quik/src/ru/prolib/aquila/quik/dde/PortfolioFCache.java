package ru.prolib.aquila.quik.dde;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Кэш строки таблицы лимитов по деривативам.
 */
public class PortfolioFCache extends CacheEntry {
	private final String accountCode;
	private final String firmId;
	private final Double balance;
	private final Double cash;
	private final Double varMargin;

	/**
	 * Конструкто.
	 * <p>
	 * @param accountCode торговый счет
	 * @param firmId идентификатор фирмы
	 * @param balance баланс
	 * @param cash доступные средства в деньгах
	 * @param varMargin вариационка
	 */
	public PortfolioFCache(String accountCode, String firmId, Double balance,
			Double cash, Double varMargin)
	{
		super();
		this.accountCode = accountCode;
		this.firmId = firmId;
		this.balance = balance;
		this.cash = cash;
		this.varMargin = varMargin;
	}
	
	public String getAccountCode() {
		return accountCode;
	}
	
	public String getFirmId() {
		return firmId;
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
		if ( other.getClass() != PortfolioFCache.class ) {
			return false;
		}
		PortfolioFCache o = (PortfolioFCache) other;
		return new EqualsBuilder()
			.append(accountCode, o.accountCode)
			.append(firmId, o.firmId)
			.append(balance, o.balance)
			.append(cash, o.cash)
			.append(varMargin, o.varMargin)
			.isEquals();
	}
	
}
