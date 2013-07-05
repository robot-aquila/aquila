package ru.prolib.aquila.core.report.trades;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.report.*;

/**
 * Селектор сделок по счету.
 * <p>
 * Утверждает сделки по заявкам указанного торгового счета. 
 */
public class AccountTradeSelector implements TradeSelector {
	private final Account account;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param account торговый счет
	 */
	public AccountTradeSelector(Account account) {
		super();
		this.account = account;
	}

	@Override
	public boolean mustBeAdded(Trade trade, Order order) {
		return account.equals(order.getAccount());
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != AccountTradeSelector.class ) {
			return false;
		}
		AccountTradeSelector o = (AccountTradeSelector) other;
		return new EqualsBuilder()
			.append(o.account, account)
			.isEquals();
	}

}
