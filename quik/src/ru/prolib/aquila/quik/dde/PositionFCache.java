package ru.prolib.aquila.quik.dde;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Кэш строки таблицы позиций по деривативам.
 */
public class PositionFCache extends CacheEntry {
	private final String accountCode;
	private final String firmId;
	private final String secShortName;
	private final Long openQty;
	private final Long currentQty;
	private final Double varMargin;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param accountCode код торгового счета
	 * @param firmId код фирмы
	 * @param secShortName краткое наименование инструмента
	 * @param openQty входящее кол-во
	 * @param currentQty текущее кол-во
	 * @param varMargin вариационка
	 */
	public PositionFCache(String accountCode, String firmId,
			String secShortName, Long openQty, Long currentQty,
			Double varMargin)
	{
		super();
		this.accountCode = accountCode;
		this.firmId = firmId;
		this.secShortName = secShortName;
		this.openQty = openQty;
		this.currentQty = currentQty;
		this.varMargin = varMargin;
	}
	
	public String getAccountCode() {
		return accountCode;
	}
	
	public String getFirmId() {
		return firmId;
	}
	
	public String getSecurityShortName() {
		return secShortName;
	}
	
	public Long getOpenQty() {
		return openQty;
	}
	
	public Long getCurrentQty() {
		return currentQty;
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
		if ( other.getClass() != PositionFCache.class ) {
			return false;
		}
		PositionFCache o = (PositionFCache) other;
		return new EqualsBuilder()
			.append(accountCode, o.accountCode)
			.append(firmId, o.firmId)
			.append(secShortName, o.secShortName)
			.append(openQty, o.openQty)
			.append(currentQty, o.currentQty)
			.append(varMargin, o.varMargin)
			.isEquals();
	}

}
