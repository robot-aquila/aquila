package ru.prolib.aquila.core.BusinessEntities;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCEventImpl;

/**
 * Событие портфеля.
 * <p>
 * 2012-09-05<br>
 * $Id$
 */
public class PortfolioEvent extends OSCEventImpl {
	private final Portfolio portfolio;

	/**
	 * Создать событие.
	 * <p>
	 * @param type экземпляр типа
	 * @param portfolio портфель
	 */
	public PortfolioEvent(EventType type, Portfolio portfolio) {
		super(type, portfolio);
		this.portfolio = portfolio;
	}
	
	/**
	 * Получить экземпляр портфеля.
	 * <p>
	 * @return портфель
	 */
	public Portfolio getPortfolio() {
		return portfolio;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null ) {
			return false;
		}
		if ( other.getClass() == this.getClass() ) {
			PortfolioEvent o = (PortfolioEvent)other;
			return o.getType() == getType()
				&& o.getPortfolio() == getPortfolio()
				&& new EqualsBuilder()
					.append(o.getUpdatedTokens(), getUpdatedTokens())
					.isEquals();
		}
		return false;
	}

}
