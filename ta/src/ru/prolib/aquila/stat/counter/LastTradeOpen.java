package ru.prolib.aquila.stat.counter;

import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.ChaosTheory.ServiceLocator;
import ru.prolib.aquila.ChaosTheory.ServiceLocatorException;
import ru.prolib.aquila.stat.TrackingTrades;
import ru.prolib.aquila.stat.TradeEvent;
import ru.prolib.aquila.stat.TradeReportException;
import ru.prolib.aquila.ta.ValueException;
import ru.prolib.aquila.ta.ds.MarketData;

/**
 * Определяет время открытия последнего трейда.
 * 
 * 2012-02-05
 * $Id: LastTradeOpen.java 198 2012-02-06 13:04:25Z whirlwind $
 */
public class LastTradeOpen extends Observable
	implements Counter<Date>, Observer
{
	private static final Logger logger = LoggerFactory.getLogger(LastTradeOpen.class);
	private Date value;
	private TrackingTrades tracking;
	private MarketData data;
	
	public LastTradeOpen() {
		super();
	}

	@Override
	public void update(Observable o, Object arg) {
		if ( arg instanceof TradeEvent ) {
			TradeEvent event = (TradeEvent) arg;
			if ( event.getEventId() == TradeEvent.NEW_TRADE ) {
				try {
					value = data.getTime()
						.get(event.getTradeReport().getFirstChange().getBar());
					setChanged();
					notifyObservers();
				} catch ( TradeReportException e ) {
					error(e);
					value = null;
				} catch ( ValueException e ) {
					error(e);
					value = null;
				}
			}
		}
	}

	@Override
	public Date getValue() {
		return value;
	}

	@Override
	public void startService(ServiceLocator locator) throws CounterException {
		if ( tracking != null ) {
			throw new CounterServiceAlreadyStartedException();
		}
		try {
			data = locator.getMarketData();
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
			data = null;
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
