package ru.prolib.aquila.ib.assembler.cache;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.Account;

import com.ib.client.*;

/**
 * Кэш-запись позиции.
 * <p>
 * Инкапсулирует данные, полученные через метод updatePortfolio.
 */
public class PositionEntry extends CacheEntry
	implements Comparable<PositionEntry>
{
	private final Contract contract;
	private final Long qty;
	private final Double marketValue, bookValue, varMargin;
	private final Account account;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param contract контракт
	 * @param position текущая позиция
	 * @param marketValue рыночная стоимость позиции
	 * @param averageCost балансовая цена за единицу
	 * @param realizedPNL вариационка реализованной ранее позиции
	 * @param accountName код торгового счета
	 */
	public PositionEntry(Contract contract, int position, double marketValue,
			double averageCost, double realizedPNL, String accountName)
	{
		super();
		this.contract = contract;
		this.qty = (long) position;
		this.marketValue = marketValue;
		this.bookValue = averageCost * position;
		// TODO: тут проверить какие значения приходят при шорте
		this.varMargin = marketValue - bookValue + realizedPNL;
		this.account = new Account(accountName);
	}
	
	public Contract getContract() {
		return contract;
	}
	
	public int getContractId() {
		return contract.m_conId;
	}
	
	public Long getQty() {
		return qty;
	}
	
	public Double getMarketValue() {
		return marketValue;
	}
	
	public Double getBookValue() {
		return bookValue;
	}
	
	public Account getAccount() {
		return account;
	}
	
	public Double getVarMargin() {
		return varMargin;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != PositionEntry.class ) {
			return false;
		}
		PositionEntry o = (PositionEntry) other;
		return new EqualsBuilder()
			.append(o.account, account)
			.append(o.bookValue, bookValue)
			.append(o.contract, contract)
			.append(o.marketValue, marketValue)
			.append(o.qty, qty)
			.append(o.varMargin, varMargin)
			.isEquals();
	}

	@Override
	public int compareTo(PositionEntry o) {
		return new CompareToBuilder()
			.append(o.contract.m_conId, contract.m_conId)
			.append(o.contract.m_primaryExch, contract.m_primaryExch)
			.toComparison();
	}

}
