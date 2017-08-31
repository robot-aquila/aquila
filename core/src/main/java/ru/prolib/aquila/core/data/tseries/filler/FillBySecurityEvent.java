package ru.prolib.aquila.core.data.tseries.filler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.SecurityEvent;
import ru.prolib.aquila.core.BusinessEntities.SecurityException;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.data.EditableTSeries;

public abstract class FillBySecurityEvent<T> implements EventListener {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(FillBySecurityEvent.class);
	}
	
	protected final EditableTSeries<T> series;
	protected final Terminal terminal;
	protected final Symbol symbol;
	protected Security security;
	protected boolean started = false;

	public FillBySecurityEvent(EditableTSeries<T> series,
			Terminal terminal, Symbol symbol)
	{
		this.series = series;
		this.terminal = terminal;
		this.symbol = symbol;
	}

	public EditableTSeries<T> getSeries() {
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
	protected synchronized void setSecurity(Security security) {
		this.security = security;
	}

	/**
	 * Service method. For testing purposes only.
	 * <p>
	 * @param started - true if started, false - otherwise
	 */
	protected synchronized void setStarted(boolean started) {
		this.started = started;
	}

	public synchronized boolean isStarted() {
		return started;
	}

	public synchronized FillBySecurityEvent<T> start() {
		if ( ! started ) {
			if ( security == null ) {
				terminal.lock();
				try {
					if ( terminal.isSecurityExists(symbol) ) {
						security = terminal.getSecurity(symbol);
						startListening(security);
					} else {
						terminal.onSecurityAvailable().addListener(this);
					}
				} catch ( SecurityException e ) {
					logger.error("Unexpected exception: ", e);
				} finally {
					terminal.unlock();
				}				
			} else {
				startListening(security);
			}
			started = true;
		}
		return this;
	}

	public synchronized FillBySecurityEvent<T> stop() {
		if ( started ) {
			terminal.onSecurityAvailable().removeListener(this);
			if ( security != null ) {
				stopListening(security);
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
				startListening(security);
				terminal.onSecurityAvailable().removeListener(this);
			}
		} else {
			processEvent(event);
		}
	}

	protected abstract void processEvent(Event event);
	protected abstract void stopListening(Security security);
	protected abstract void startListening(Security security);

}