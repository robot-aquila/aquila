package ru.prolib.aquila.core.data.filler;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.Tick;

/**
 * Бары по цене последней сделки инструмента.
 * <p>
 * Подписывается на изменения цены последней сделки инструмента. При получении
 * события, формирует тик по времени терминала и направляет данные аггрегатору
 * баров. 
 * <p>
 * TODO: Нет никакой возможности отфильтровать изменения цены последней сделки
 * от изменений других атрибутов. В связи с этим, формирование свечей данным
 * классом может выполняться некорректно. Но этот вопрос требует доработки
 * модели инструмента.
 */
public class CandleByLastPrice implements EventListener, Starter {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(CandleByLastPrice.class);
	}
	
	private final Security security;
	private final CandleAggregator aggregator;
	
	public CandleByLastPrice(Security security, CandleAggregator aggregator) {
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
		logger.warn("TODO: The last price changes not filtered of another changes");
		security.OnChanged().addListener(this);
	}

	@Override
	public void stop() throws StarterException {
		security.OnChanged().removeListener(this);
	}

	@Override
	public void onEvent(Event event) {
		aggregator.add(new Tick(security.getTerminal().getCurrentTime(),
				security.getLastPrice()));
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != CandleByLastPrice.class ) {
			return false;
		}
		CandleByLastPrice o = (CandleByLastPrice) other;
		return new EqualsBuilder()
			.appendSuper(o.security == security)
			.appendSuper(o.aggregator == aggregator)
			.isEquals();
	}

}
