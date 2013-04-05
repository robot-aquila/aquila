package ru.prolib.aquila.ib.subsys.account;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.Portfolio;
import ru.prolib.aquila.core.utils.Validator;

/**
 * Валидатор доступности портфеля IB.
 * <p>
 * Портфель считается доступным, когда кэш и баланс портфеля отличны от null.
 * <p>
 * 2012-12-30<br>
 * $Id: IBIsPortfolioAvailable.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBIsPortfolioAvailable implements Validator {
	
	/**
	 * Конструктор.
	 */
	public IBIsPortfolioAvailable() {
		super();
	}

	@Override
	public boolean validate(Object object) {
		if ( object instanceof Portfolio ) {
			Portfolio portfolio = (Portfolio) object;
			return portfolio.getCash() != null
				&& portfolio.getBalance() != null;
		}
		return false;
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass()== IBIsPortfolioAvailable.class;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121231, 175427)
			.append(IBIsPortfolioAvailable.class)
			.toHashCode();
	}

}
