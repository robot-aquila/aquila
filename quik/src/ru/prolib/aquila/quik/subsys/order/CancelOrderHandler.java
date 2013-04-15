package ru.prolib.aquila.quik.subsys.order;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditableOrders;
import ru.prolib.aquila.core.BusinessEntities.OrderException;
import ru.prolib.aquila.core.BusinessEntities.OrderStatus;
import ru.prolib.aquila.quik.api.TransEvent;
import ru.prolib.aquila.quik.subsys.QUIKServiceLocator;
import ru.prolib.aquila.t2q.T2QTransStatus;

/**
 * Обработчик событий исполнения или отклонения транзакции отмены заявки.
 * <p>
 * Обрабатывает события QUIK API, генерируемые в связи с обработкой
 * транзакции регистрации заявки. Если на момент события заявка не находится
 * в состоянии {@link OrderStatus#ACTIVE}, то событие игнорируется.
 */
public class CancelOrderHandler implements EventListener {
	protected final EditableOrders orders;
	protected final QUIKServiceLocator locator;
	protected final long orderId;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param locator сервис-локатор
	 * @param orders хранилище заявок
	 * @param orderId идентификатор заявки (не передается в транзакции)
	 */
	public CancelOrderHandler(QUIKServiceLocator locator,
			EditableOrders orders, long orderId)
	{
		super();
		this.orders = orders;
		this.locator = locator;
		this.orderId = orderId;
	}

	@Override
	public void onEvent(Event event) {
		try {
			onTransactionReply((TransEvent) event);
		} catch ( OrderException e ) {
			throw new RuntimeException("Unexpected exception", e);
		}
	}
	
	private void onTransactionReply(TransEvent event) throws OrderException {
		T2QTransStatus transStatus = event.getStatus();
		if ( transStatus == T2QTransStatus.SENT
		  || transStatus == T2QTransStatus.RECV )
		{
			// Эти статусы не являются ошибкой и никак не обрабатываются
			return;
		}
		EditableOrder order = orders.getEditableOrder(orderId);
		if ( order.getStatus() != OrderStatus.ACTIVE ) {
			return;
		}
		if ( transStatus == T2QTransStatus.DONE ) {
			order.setStatus(OrderStatus.CANCELLED);
		} else {
			order.setStatus(OrderStatus.FAILED);
		}
		order.fireChangedEvent();
		order.resetChanges();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == CancelOrderHandler.class ) {
			CancelOrderHandler o = (CancelOrderHandler) other;
			return new EqualsBuilder()
				.append(locator, o.locator)
				.append(orders, o.orders)
				.append(orderId, o.orderId)
				.isEquals();
		} else {
			return false;
		}
	}

}
