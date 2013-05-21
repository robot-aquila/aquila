package ru.prolib.aquila.quik.subsys.order;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.quik.api.TransEvent;
import ru.prolib.aquila.quik.subsys.QUIKServiceLocator;
import ru.prolib.aquila.t2q.T2QTransStatus;

/**
 * Обработчик событий регистрации или отклонения новой заявки.
 * <p>
 * Обрабатывает события QUIK API, генерируемые в связи с обработкой
 * транзакции регистрации заявки.
 */
public class PlaceOrderHandler implements EventListener {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(PlaceOrderHandler.class);
	}
	
	protected final EditableOrders orders;
	protected final QUIKServiceLocator locator;
	
	public PlaceOrderHandler(QUIKServiceLocator locator,EditableOrders orders) {
		super();
		this.orders = orders;
		this.locator = locator;
	}

	@Override
	public void onEvent(Event event) {
		try {
			onTransactionReply((TransEvent) event);
		} catch ( Exception e ) {
			throw new RuntimeException("Unexpected exception", e);
		}
	}
	
	private void onTransactionReply(TransEvent event) throws Exception {
		T2QTransStatus transStatus = event.getStatus();
		if ( transStatus == T2QTransStatus.SENT
		  || transStatus == T2QTransStatus.RECV )
		{
			// Эти статусы не являются ошибкой и никак не обрабатываются
			return;
		}

		// Независимо от результата, заявка становится доступной для обработки.
		// Это необходимо, что бы обеспечить возможность отработать обработчикам
		// событий регистрации и провала регистрации.
		EditableOrder order;
		OrderStatus orderStatus;
		long orderId;
		if ( transStatus == T2QTransStatus.DONE ) {
			orderStatus = OrderStatus.ACTIVE;
			orderId = event.getOrderId();
		} else {
			orderStatus = OrderStatus.FAILED;
			orderId = locator.getFailedOrderNumerator().decrementAndGet();
		}
		// Когда заявка становится доступной, она еще остается ожидающей.
		long transId = event.getTransId();
		order = orders.movePendingOrder(transId, orderId);
		order.setAvailable(true);
		orders.fireOrderAvailableEvent(order);
		// А теперь генерируется событие о смене статуса заявки.
		order.setStatus(orderStatus);
		order.fireChangedEvent();
		order.resetChanges();
		logger.debug("Trans {} order {} response status {}",
				new Object[] { transId, orderId, orderStatus });
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == PlaceOrderHandler.class ) {
			PlaceOrderHandler o = (PlaceOrderHandler) other;
			return new EqualsBuilder()
				.append(locator, o.locator)
				.append(orders, o.orders)
				.isEquals();
		} else {
			return false;
		}
	}

}
