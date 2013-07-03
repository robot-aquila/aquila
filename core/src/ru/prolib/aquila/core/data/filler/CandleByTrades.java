package ru.prolib.aquila.core.data.filler;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.SecurityTradeEvent;

/**
 * Сервис формирования свечей на основании сделок инструмента.
 */
class CandleByTrades implements Starter, EventListener {
	private final Security security;
	private final CandleAggregator aggregator;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param security инструмент-источник сделок
	 * @param aggregator целевой агрегатор
	 */
	public CandleByTrades(Security security, CandleAggregator aggregator) {
		super();
		this.security = security;
		this.aggregator = aggregator;
	}
	
	Security getSecurity() {
		return security;
	}
	
	CandleAggregator getAggregator() {
		return aggregator;
	}

	@Override
	public void start() throws StarterException {
		security.OnTrade().addListener(this);
	}

	@Override
	public void stop() throws StarterException {
		security.OnTrade().removeListener(this);
	}

	@Override
	public void onEvent(Event event) {
		SecurityTradeEvent e = (SecurityTradeEvent) event;
		aggregator.add(e.getTrade());
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != CandleByTrades.class ) {
			return false;
		}
		CandleByTrades o = (CandleByTrades) other;
		return new EqualsBuilder()
			.appendSuper(o.security == security)
			.append(o.aggregator, aggregator)
			.isEquals();
	}

}
