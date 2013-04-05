package ru.prolib.aquila.stat.counter;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import ru.prolib.aquila.ChaosTheory.ServiceLocator;
import ru.prolib.aquila.ChaosTheory.ServiceLocatorException;
import ru.prolib.aquila.stat.PositionChange;
import ru.prolib.aquila.stat.TrackingTrades;
import ru.prolib.aquila.stat.TradeEvent;
import ru.prolib.aquila.stat.TradeReport;

/**
 * Расчитывает доход/убыток по последнему трейду в пунктах.
 * Расчет выполняется каждый раз после получения уведомления
 * {@link ru.prolib.aquila.stat.TradeEvent#TRADE_CLOSED}.
 * После перерасчета выполняет уведомление наблюдателей.
 * 
 * 2012-02-05
 * $Id: LastTradeYieldPoints.java 197 2012-02-05 20:21:19Z whirlwind $
 */
public class LastTradeYieldPoints extends Observable
	implements Counter<Double>, Observer
{
	private Double value;
	private TrackingTrades tracking;
	
	public LastTradeYieldPoints() {
		super();
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if ( arg1 instanceof TradeEvent ) {
			TradeEvent e = (TradeEvent) arg1;
			TradeReport trade = e.getTradeReport();
			if ( e.getEventId() == TradeEvent.TRADE_CLOSED ) {
				double total = 0.0d;
				List<PositionChange> changes = trade.getChanges();
				for ( int i = 0; i < changes.size(); i ++ ) {
					PositionChange change = changes.get(i);
					total += (change.getPrice() * change.getQty());
				}
				value = (total * -1);
				setChanged();
				notifyObservers();
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

}
