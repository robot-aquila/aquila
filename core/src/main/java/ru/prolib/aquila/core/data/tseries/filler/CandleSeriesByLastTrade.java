package ru.prolib.aquila.core.data.tseries.filler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.SecurityTickEvent;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.EditableTSeries;
import ru.prolib.aquila.core.data.ValueException;

public class CandleSeriesByLastTrade extends FillBySecurityEvent<Candle> {
	static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(CandleSeriesByLastTrade.class);
	}
	
	private final CandleSeriesAggregator<Tick> aggregator;

	/**
	 * Service constructor. For testing purposes only.
	 * <p>
	 * @param series - target series
	 * @param terminal - source terminal
	 * @param symbol - symbol to build candle series based on its last trades
	 * @param aggregator - tick aggregator
	 */
	CandleSeriesByLastTrade(EditableTSeries<Candle> series,
			Terminal terminal, Symbol symbol, CandleSeriesAggregator<Tick> aggregator)
	{
		super(series, terminal, symbol);
		this.aggregator = aggregator;
	}
	
	public CandleSeriesByLastTrade(EditableTSeries<Candle> series,
			Terminal terminal, Symbol symbol)
	{
		this(series, terminal, symbol, CandleSeriesTickAggregator.getInstance());
	}
	
	@Override
	protected void startListening(Security security) {
		security.onLastTrade().addListener(this);
	}
	
	@Override
	protected void stopListening(Security security) {
		security.onLastTrade().removeListener(this);
	}
	
	@Override
	protected void processEvent(Event event) {
		if ( event.isType(security.onLastTrade()) ) {
			SecurityTickEvent e = (SecurityTickEvent) event;
			try {
				aggregator.aggregate(series, e.getTick());
			} catch ( ValueException exception ) {
				logger.error("Unexpected exception: ", e);
			}
		}
	}

}
