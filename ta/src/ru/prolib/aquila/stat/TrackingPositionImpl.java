package ru.prolib.aquila.stat;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

import ru.prolib.aquila.ChaosTheory.Order;
import ru.prolib.aquila.ChaosTheory.ServiceLocator;
import ru.prolib.aquila.ChaosTheory.ServiceLocatorException;
import ru.prolib.aquila.ta.ValueException;

/**
 * Отслеживание позиции.
 * Накапливает информацию начиная с момента открытия позиции и до ее закрытия.
 */
@Deprecated
public class TrackingPositionImpl extends Observable
	implements TrackingPosition
{
	private final ServiceLocator locator;
	private final LinkedList<TrackingPositionChange> changes;
	private boolean closed = false;
	
	public TrackingPositionImpl(ServiceLocator serviceLocator) {
		super();
		locator = serviceLocator;
		changes = new LinkedList<TrackingPositionChange>();
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.stat.TrackingPosition#getChanges()
	 */
	@Override
	public List<TrackingPositionChange> getChanges() {
		return new LinkedList<TrackingPositionChange>(changes);
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.stat.TrackingPosition#addChange(ru.prolib.aquila.ChaosTheory.Order)
	 */
	@Override
	public void addChange(Order order) throws TrackingException {
		if ( isClosed() ) {
			throw new TrackingException("Position already closed");
		}
		if ( order.getQty() == 0 ) {
			throw new TrackingException("Zero order qty");
		}
		if ( order.getStatus() != Order.FILLED ) {
			throw new TrackingException("Only filled orders allowed");
		}
		if ( ! order.isLimitOrder() ) {
			throw new TrackingException("Only limit-orders allowed");
		}
		try {
			changes.add(new TrackingPositionChange(order,
						locator.getMarketData().getLastBarIndex()));
		} catch ( ServiceLocatorException e ) {
			throw new TrackingException(e.getMessage(), e);
		}
		
		int balance = 0;
		for ( int i = 0; i < changes.size(); i ++ ) {
			Order o = changes.get(i).getOrder();
			if ( o.isBuy() ) {
				balance += o.getQty();
			} else {
				balance -= o.getQty();
			}
		}
		if ( isLong() ) {
			if ( balance <= 0 ) {
				closed = true;
			}
		} else {
			if ( balance >= 0 ) {
				closed = true;
			}
		}
		setChanged();
		if ( closed == true ) {
			notifyObservers(EVENT_CLOSED);
		} else {
			notifyObservers(EVENT_CHANGED);
		}
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.stat.TrackingPosition#isClosed()
	 */
	@Override
	public boolean isClosed() {
		return closed;
	}

	@Override
	public TrackingPositionChange getFirstChange() {
		return changes.getFirst();
	}

	@Override
	public TrackingPositionChange getLastChange() {
		return changes.getLast();
	}

	@Override
	public boolean isShort() {
		return getFirstChange().getOrder().isSell();
	}

	@Override
	public boolean isLong() {
		return getFirstChange().getOrder().isBuy();
	}

}
