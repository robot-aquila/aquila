package ru.prolib.aquila.stat;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.ChaosTheory.Order;
import ru.prolib.aquila.ChaosTheory.OrderException;
import ru.prolib.aquila.ChaosTheory.PortfolioOrders;
import ru.prolib.aquila.ChaosTheory.ServiceLocator;
import ru.prolib.aquila.ChaosTheory.ServiceLocatorException;
import ru.prolib.aquila.ta.ValueException;
import ru.prolib.aquila.ta.ds.MarketData;

/**
 * 2012-02-02
 * $Id: TrackingTradesImpl.java 196 2012-02-02 20:24:38Z whirlwind $
 * 
 * В идеале этот механизм лучше всего привязать к сделкам, а не к заявкам.
 * Но это потребует усложнения реализации для тестирования и доработки квикового
 * портфеля. Так что оставим эту идею до лучших времен.
 */
public class TrackingTradesImpl extends Observable
	implements TrackingTrades,Observer
{
	private static final Logger logger = LoggerFactory.getLogger(TrackingTradesImpl.class);
	private TradeReport current;
	private PortfolioOrders orders;
	private MarketData data;
	
	public TrackingTradesImpl() {
		super();
	}

	@Override
	public void startService(ServiceLocator locator) throws TrackingException {
		if ( orders != null ) {
			throw new TrackingServiceAlreadyStartedException();
		}
		try {
			data = locator.getMarketData();
			orders = locator.getPortfolioOrders();
		} catch ( ServiceLocatorException e ) {
			orders = null;
			throw new TrackingException(e);
		}
		current = null;
		orders.addObserver(this);
	}

	@Override
	public void stopService() throws TrackingException {
		if ( orders != null ) {
			orders.deleteObserver(this);
			orders = null;
			data = null;
		}
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		if ( arg0 instanceof PortfolioOrders ) {
			Order order = (Order) arg1;
			if ( order.isLimitOrder() ) {
				order.addObserver(this);
			}
		} else if ( arg0 instanceof Order ) {
			Order order = (Order) arg0;
			order.deleteObserver(this);
			if ( order.getStatus() == Order.FILLED ) {
				String msg = "Error counting trade";
				try {
					filledOrder(order);
				} catch ( ValueException e ) {
					error(msg, e);
				} catch ( OrderException e ) {
					error(msg, e);
				} catch ( TradeReportException e ) {
					error(msg, e);
				}
			}
		} else {
			logger.error("Unknown observable instance: {}", arg0);
		}
	}
	
	private void error(String msg, Exception e) {
		if ( logger.isDebugEnabled() ) {
			logger.error(msg, e);
		} else {
			logger.error(msg + e.getMessage());
		}
	}
	
	private int countCurrentPosition() {
		List<PositionChange> changes = current.getChanges();
		int position = 0;
		for ( int i = 0; i < changes.size(); i ++ ) {
			position += changes.get(i).getQty();
		}
		return position;
	}
	
	private void filledOrder(Order order)
		throws ValueException, OrderException, TradeReportException
	{
		if ( current == null ) {
			newTrade(createChange(order));
		} else {
			int posCurr = countCurrentPosition();
			int posOrder = order.isBuy() ? order.getQty() : -order.getQty(); 
			int posNext = posCurr + posOrder;
			if ( posNext == 0 ) {
				// Позиция закрыта
				tradeClosed(createChange(order));
			} else if ( (current.isLong() && posNext > 0)
					 || (current.isShort() && posNext < 0 ) )
			{
				// Позиция была изменена.
				tradeChange(createChange(order));
			} else {
				// Перевертыш: текущий трейд закрываем в ноль
				tradeClosed(createChange(-posCurr, order));
				// А образовавшаяся разница идет в новый трейд
				newTrade(createChange(posNext, order));
			}
		}
	}
	
	private void tradeChange(PositionChange change) {
		current.addChange(change);
		setChanged();
		notifyObservers(TradeEvent.tradeChange(current));
	}
	
	private void tradeClosed(PositionChange lastChange) {
		current.addChange(lastChange);
		setChanged();
		notifyObservers(TradeEvent.tradeClosed(current));
		current = null;
	}
	
	private void newTrade(PositionChange firstChange) {
		current = new TradeReport();
		current.addChange(firstChange);
		setChanged();
		notifyObservers(TradeEvent.newTrade(current));
	}
	
	private PositionChange createChange(int qty, double price, String comment)
		throws ValueException
	{
		return new PositionChange(data.getLastBarIndex(), qty, price, comment);
	}
	
	private PositionChange createChange(int qty, Order order )
		throws ValueException, OrderException
	{
		return createChange(qty, order.getPrice(), order.getComment());
	}
	
	private PositionChange createChange(Order order)
		throws ValueException, OrderException
	{
		return createChange(order.isBuy() ? order.getQty() : -order.getQty(),
				order.getPrice(), order.getComment());
	}

	
}
