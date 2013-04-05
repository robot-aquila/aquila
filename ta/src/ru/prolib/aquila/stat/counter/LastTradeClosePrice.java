package ru.prolib.aquila.stat.counter;

import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.ChaosTheory.ServiceLocator;
import ru.prolib.aquila.ChaosTheory.ServiceLocatorException;
import ru.prolib.aquila.stat.TrackingTrades;
import ru.prolib.aquila.stat.TradeEvent;
import ru.prolib.aquila.stat.TradeReportException;

/**
 * Определяет цену последней сделки в трейде.
 * 
 * 2012/02/09
 * $Id: LastTradeClosePrice.java 200 2012-02-11 14:03:38Z whirlwind $
 */
public class LastTradeClosePrice extends Observable
	implements Counter<Double>, Observer
{
	private static final Logger logger = LoggerFactory.getLogger(LastTradeClosePrice.class);
	private Double value;
	private TrackingTrades tracking;
	
	public LastTradeClosePrice() {
		super();
	}

	@Override
	public void update(Observable o, Object arg) {
		if ( arg instanceof TradeEvent ) {
			TradeEvent event = (TradeEvent) arg;
			if ( event.getEventId() == TradeEvent.TRADE_CLOSED ) {
				try {
					value = event.getTradeReport().getLastChange().getPrice();
					setChanged();
					notifyObservers();
				} catch ( TradeReportException e ) {
					error(e);
					value = null;
				}
			}
		}
	}

	@Override
	public Double getValue() {
		return value;
	}

	@Override
	public void startService(ServiceLocator locator) throws CounterException {
		if ( tracking != null ) {
			throw new CounterServiceAlreadyStartedException();
		}
		try {
			tracking = locator.getTrackingTrades();
		} catch ( ServiceLocatorException e ) {
			throw new CounterException(e);
		}
		tracking.addObserver(this);
	}

	@Override
	public void stopService() throws CounterException {
		if ( tracking != null ) {
			tracking.deleteObserver(this);
			tracking = null;
		}
	}
	
	private void error(Exception e) {
		if ( logger.isDebugEnabled() ) {
			logger.error("Could not obtain counter", e);
		} else {
			logger.error("Could not obtain counter: {}", e.getMessage());
		}
	}

}
