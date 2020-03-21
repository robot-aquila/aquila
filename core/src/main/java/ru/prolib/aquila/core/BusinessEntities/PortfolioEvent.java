package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;

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
	protected final Portfolio portfolio;

	/**
	 * Создать событие.
	 * <p>
	 * @param type экземпляр типа
	 * @param portfolio портфель
	 * @param time - time of event
	 */
	public PortfolioEvent(EventType type, Portfolio portfolio, Instant time) {
		super(type, portfolio, time);
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
					.append(o.time, time)
					.isEquals();
		}
		return false;
	}

}
