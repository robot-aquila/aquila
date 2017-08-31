package ru.prolib.aquila.core.data.tseries.filler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.SecurityEvent;
import ru.prolib.aquila.core.BusinessEntities.SecurityException;
import ru.prolib.aquila.core.BusinessEntities.SecurityTickEvent;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.data.CSUtils;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.EditableTSeries;
import ru.prolib.aquila.core.data.ValueException;

public class CandleSeriesByLastTrade implements EventListener {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(CandleSeriesByLastTrade.class);
	}
	
	private final EditableTSeries<Candle> series;
	private final Terminal terminal;
	private final Symbol symbol;
	private final CSUtils utils;
	private Security security;
	private boolean started = false;
	
	/**
	 * Service constructor. For testing purposes only.
	 * <p>
	 * @param series - target series
	 * @param terminal - source terminal
	 * @param symbol - symbol to build candle series based on its last trades
	 * @param utils - candle series utils
	 */
	CandleSeriesByLastTrade(EditableTSeries<Candle> series,
			Terminal terminal, Symbol symbol, CSUtils utils)
	{
		this.series = series;
		this.terminal = terminal;
		this.symbol = symbol;
		this.utils = utils;
	}
	
	public CandleSeriesByLastTrade(EditableTSeries<Candle> series,
			Terminal terminal, Symbol symbol)
	{
		this(series, terminal, symbol, new CSUtils());
	}
	
	public EditableTSeries<Candle> getSeries() {
		return series;
	}
	
	public Terminal getTerminal() {
		return terminal;
	}
	
	public Symbol getSymbol() {
		return symbol;
	}
	
	public synchronized Security getSecurity() {
		return security;
	}
	
	/**
	 * Service method. For testing purposes only.
	 * <p>
	 * @param security - security instance
	 */
	synchronized void setSecurity(Security security) {
		this.security = security;
	}
	
	/**
	 * Service method. For testing purposes only.
	 * <p>
	 * @param started - true if started, false - otherwise
	 */
	synchronized void setStarted(boolean started) {
		this.started = started;
	}
	
	public synchronized boolean isStarted() {
		return started;
	}
	
	public synchronized CandleSeriesByLastTrade start() {
		if ( ! started ) {
			if ( security == null ) {
				terminal.lock();
				try {
					if ( terminal.isSecurityExists(symbol) ) {
						security = terminal.getSecurity(symbol);
						security.onLastTrade().addListener(this);
					} else {
						terminal.onSecurityAvailable().addListener(this);
					}
				} catch ( SecurityException e ) {
					logger.error("Unexpected exception: ", e);
				} finally {
					terminal.unlock();
				}				
			} else {
				security.onLastTrade().addListener(this);
			}
			started = true;
		}
		return this;
	}
	
	public synchronized CandleSeriesByLastTrade stop() {
		if ( started ) {
			terminal.onSecurityAvailable().removeListener(this);
			if ( security != null ) {
				security.onLastTrade().removeListener(this);
			}
			started = false;
		}
		return this;
	}

	@Override
	public synchronized void onEvent(Event event) {
		if ( ! started ) {
			return;
		}
		if ( event.isType(terminal.onSecurityAvailable()) ) {
			SecurityEvent e = (SecurityEvent) event;
			if ( symbol.equals(e.getSecurity().getSymbol()) ) {
				security = e.getSecurity();
				security.onLastTrade().addListener(this);
				terminal.onSecurityAvailable().removeListener(this);
			}
		} else if ( event.isType(security.onLastTrade()) ) {
			SecurityTickEvent e = (SecurityTickEvent) event;
			try {
				utils.aggregate(series, e.getTick());
			} catch ( ValueException exception ) {
				logger.error("Unexpected exception: ", e);
			}
		}
	}

}
