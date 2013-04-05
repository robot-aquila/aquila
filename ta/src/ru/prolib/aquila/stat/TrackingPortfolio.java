package ru.prolib.aquila.stat;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.ChaosTheory.Order;
import ru.prolib.aquila.ChaosTheory.PortfolioOrders;
import ru.prolib.aquila.ChaosTheory.ServiceLocator;
import ru.prolib.aquila.ChaosTheory.ServiceLocatorException;

/**
 * Статистика по трейдам и портфелю.
 *
 * Такие параметры как просадка, доходность могут изменяться только в случае
 * открытой позиции. Следовательно, показатели по портфелю так же можно собирать
 * в процессе сбора информации о трейде либо на этапе закрытия трейда.
 */
@Deprecated
public class TrackingPortfolio extends Observable implements Observer {
	public static final Integer EVENT_SERVICE_STARTED = 0x1;
	public static final Integer EVENT_SERVICE_STOPPED = 0x2;
	public static final Integer EVENT_POSITION_OPENED = 0x03;
	public static final Integer EVENT_POSITION_CLOSED = 0x04;
	public static final Integer EVENT_POSITION_CHANGED = 0x05;
	
	private final static Logger logger = LoggerFactory.getLogger(TrackingPortfolio.class);
	private final LinkedList<TrackingPosition> trades;
	private TrackingPosition current = null;
	private ServiceLocator locator = null;
	
	public TrackingPortfolio() {
		super();
		trades = new LinkedList<TrackingPosition>();
	}
	
	/**
	 * Начать отслеживание позиции.
	 * 
	 * @param locator
	 * @throws TrackingException сервис уже запущен или неудалось
	 * инициализировать сервис
	 */
	public synchronized void startService(ServiceLocator locator)
		throws TrackingException
	{
		if ( this.locator != null ) {
			throw new TrackingException("Service already started");
		}
		this.locator = locator;
		try {
			locator.getPortfolioOrders().addObserver(this);
		} catch ( ServiceLocatorException e ) {
			throw new TrackingException(e.getMessage(), e);
		}
	}
	
	/**
	 * Прекратить отслеживание позиции.
	 * 
	 * @throws TrackingException ошибка остановки сервиса
	 */
	public synchronized void stopService() throws TrackingException {
		if ( locator != null ) {
			try {
				locator.getPortfolioOrders().deleteObserver(this);
			} catch ( ServiceLocatorException e ) {
				throw new TrackingException(e.getMessage(), e);
			}
			locator = null;
		}
	}
	
	/**
	 * Получить список трейдов.
	 * 
	 * @return
	 */
	public synchronized List<TrackingPosition> getTrades() {
		return new LinkedList<TrackingPosition>(trades);
	}
	
	/**
	 * Получить текущую активную позицию.
	 * 
	 * @return
	 */
	public synchronized TrackingPosition getCurrentTrade() {
		return current;
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
				synchronized ( this ) {
					try {
						if ( current == null ) {
							trades.add(current =
								new TrackingPositionImpl(locator));
							current.addChange(order);
						} else {
							current.addChange(order);
							if ( current.isClosed() ) {
								current = null;
							}
						}
					} catch ( TrackingException e ) {
						logger.error(e.getMessage());
					}
				}
			}
		} else {
			logger.error("Unknown observable instance: {}", arg0);
		}
	}
	

}
