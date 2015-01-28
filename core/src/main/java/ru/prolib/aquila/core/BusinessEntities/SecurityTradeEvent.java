package ru.prolib.aquila.core.BusinessEntities;

import ru.prolib.aquila.core.EventType;

/**
 * Событие, возникающее при поступлении новой сделки по инструменту.
 * <p>
 * 2012-06-01<br>
 * $Id: SecurityTradeEvent.java 223 2012-07-04 12:26:58Z whirlwind $
 */
public class SecurityTradeEvent extends SecurityEvent {
	private final Trade trade;

	/**
	 * Создать событие
	 * <p>
	 * @param type тип события
	 * @param security инструмент
	 * @param trade сделка
	 */
	public SecurityTradeEvent(EventType type, Security security, Trade trade) {
		super(type, security);
		this.trade = trade;
	}

	/**
	 * Получить сделку по инструменту
	 * <p>
	 * @return сделка
	 */
	public Trade getTrade() {
		return trade;
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
			SecurityTradeEvent o = (SecurityTradeEvent)other;
			return o.getType() == getType()
				&& o.getSecurity() == getSecurity()
				&& trade.equals(o.trade);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + trade.toString() + "]";
	}

}
