package ru.prolib.aquila.quik.api;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.t2q.T2QTrade;

/**
 * Событие в связи с получением информации по сделке.
 */
public class TradeEvent extends EventImpl {
	private final T2QTrade trade;

	public TradeEvent(EventType type, T2QTrade trade) {
		super(type);
		this.trade = trade;
	}
	
	/**
	 * Получить информацию по сделке.
	 * <p>
	 * @return информацию по сделке.
	 */
	public T2QTrade getTrade() {
		return trade;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == TradeEvent.class ) {
			TradeEvent o = (TradeEvent) other;
			return new EqualsBuilder()
				.append(getType(), o.getType())
				.append(trade, o.trade)
				.isEquals();
		} else {
			return false;
		}
	}

}
