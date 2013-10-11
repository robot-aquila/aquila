package ru.prolib.aquila.core.data.filler;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.*;

/**
 * Сервис формирования свечей на основании сделок инструмента.
 * <p>
 * Подписывается на получение сделок по инструменту и перенаправляет сделки
 * в агрегатор свечей.
 */
class CandleByTrades implements Starter, EventListener {
	private static final Logger logger;
	private final Security security;
	private final EditableCandleSeries candles;
	
	static {
		logger = LoggerFactory.getLogger(LoggerFactory.class);
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param security инструмент-источник сделок
	 * @param candles целевой агрегатор
	 */
	public CandleByTrades(Security security, EditableCandleSeries candles) {
		super();
		this.security = security;
		this.candles = candles;
	}
	
	Security getSecurity() {
		return security;
	}
	
	EditableCandleSeries getCandles() {
		return candles;
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
		try {
			candles.aggregate(e.getTrade(), true);
		} catch ( ValueException ex ) {
			logger.error("Unexpected exception: ", ex);
		}
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
			.append(o.candles, candles)
			.isEquals();
	}

}
