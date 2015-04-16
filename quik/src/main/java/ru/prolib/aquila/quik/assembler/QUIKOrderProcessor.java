package ru.prolib.aquila.quik.assembler;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.quik.*;

/**
 * QUIK order processor.
 */
public class QUIKOrderProcessor implements OrderProcessor {
	private final HandlerFactory factory;
	
	QUIKOrderProcessor(HandlerFactory factory) {
		super();
		this.factory = factory;
	}

	public QUIKOrderProcessor() {
		this(new HandlerFactory());
	}
	
	
	HandlerFactory getFactory() {
		return factory;
	}
	
	@Override
	public void cancelOrder(EditableTerminal t, Order o) throws OrderException {
		QUIKTerminal terminal = (QUIKTerminal) t;
		EditableOrder order = (EditableOrder) o;
		synchronized ( order ) {
			OrderStatus status = order.getStatus();
			if ( status.isFinal() || status == OrderStatus.CANCEL_SENT ) {
				return;
			} else if ( status != OrderStatus.ACTIVE ) {
				throw new OrderException("Rejected by status: " + status);
			}
			int transId = terminal.getOrderIdSequence().incrementAndGet();
			CancelHandler handler = factory.createCancelOrder(transId, order);
			terminal.getClient().setHandler(transId, handler);
			handler.cancelOrder();
		}
	}

	@Override
	public void placeOrder(EditableTerminal t, Order o) throws OrderException {
		QUIKTerminal terminal = (QUIKTerminal) t;
		EditableOrder order = (EditableOrder) o;
		synchronized ( order ) {
			OrderStatus status = order.getStatus();
			if ( status != OrderStatus.PENDING
				&& status != OrderStatus.CONDITION )
			{
				throw new OrderException("Rejected by status: " + status);
			}
			PlaceHandler handler = factory.createPlaceOrder(order);
			terminal.getClient().setHandler(order.getId(), handler);
			handler.placeOrder();
		}
	}

}
