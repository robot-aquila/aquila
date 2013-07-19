package ru.prolib.aquila.quik.assembler;

import org.apache.commons.lang3.builder.EqualsBuilder;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.quik.QUIKEditableTerminal;

/**
 * Обработчик заявок.
 */
public class QUIKOrderProcessor implements OrderProcessor {
	private final QUIKEditableTerminal terminal;
	private final HandlerFactory factory;
	
	/**
	 * Служебный конструктор.
	 * <p>
	 * @param terminal терминал
	 * @param factory фабрика обработчиков
	 */
	QUIKOrderProcessor(QUIKEditableTerminal terminal, HandlerFactory factory) {
		super();
		this.terminal = terminal;
		this.factory = factory;
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param terminal терминал
	 */
	public QUIKOrderProcessor(QUIKEditableTerminal terminal) {
		this(terminal, new HandlerFactory());
	}
	
	QUIKEditableTerminal getTerminal() {
		return  terminal;
	}
	
	HandlerFactory getFactory() {
		return factory;
	}
	
	@Override
	public void cancelOrder(Order o) throws OrderException {
		EditableOrder order = (EditableOrder) o;
		synchronized ( order ) {
			OrderStatus status = order.getStatus();
			if ( status.isFinal() || status == OrderStatus.CANCEL_SENT ) {
				return;
			} else if ( status != OrderStatus.ACTIVE ) {
				throw new OrderException("Rejected by status: " + status);
			}
			int transId = terminal.getOrderNumerator().incrementAndGet();
			CancelHandler handler = factory.createCancelOrder(transId, order);
			terminal.getClient().setHandler(transId, handler);
			handler.cancelOrder();
		}
	}

	@Override
	public void placeOrder(Order o) throws OrderException {
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

	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == QUIKOrderProcessor.class
			? fieldsEquals(other) : false;
	}
	
	protected boolean fieldsEquals(Object other) {
		QUIKOrderProcessor o = (QUIKOrderProcessor) other;
		return new EqualsBuilder()
			.appendSuper(terminal == o.terminal)
			.append(factory, o.factory)
			.isEquals();
	}

}
