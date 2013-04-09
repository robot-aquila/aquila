package ru.prolib.aquila.quik.subsys.order;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.Counter;
import ru.prolib.aquila.quik.api.ApiService;
import ru.prolib.aquila.quik.api.TransEvent;
import ru.prolib.aquila.t2q.T2QTransStatus;

/**
 * Обработчик событий регистрации или отклонения новой заявки.
 * <p>
 * Обрабатывает события QUIK API, генерируемые в связи с обработкой
 * транзакции регистрации заявки.
 */
public class PlaceOrderHandler implements EventListener {
	protected final EditableOrders orders;
	protected final ApiService api;
	protected final Counter failedOrderId;
	protected final long transId;
	
	public PlaceOrderHandler(EditableOrders orders) {
		super();
		this.orders = orders;
		this.api = null;//api;
		this.failedOrderId = null;//failedOrderId;
		this.transId = 0;//transId;
	}

	@Override
	public void onEvent(Event event) {
		if ( event.isType(api.OnTransactionReply(transId)) ) {
			// TODO:
			//onTransactionReply((TransEvent) event);
		}
	}
	
	private void onTransactionReply(TransEvent event) throws OrderException {
		T2QTransStatus transStatus = event.getStatus();
		long transId = event.getTransId();
		if ( transStatus == T2QTransStatus.SENT
		  || transStatus == T2QTransStatus.RECV )
		{
			// Эти статусы не являются ошибкой и никак не обрабатываются
			return;
		}
		OrderStatus orderStatus;
		long orderId;
		if ( transStatus == T2QTransStatus.DONE ) {
			orderStatus = OrderStatus.ACTIVE;
			orderId = event.getOrderId();
		} else {
			orderStatus = OrderStatus.FAILED;
			orderId = failedOrderId.incrementAndGet();
		}
		EditableOrder order =
			orders.makePendingOrderAsRegisteredIfExists(transId, orderId);
		order.setAvailable(true);
		order.setStatus(orderStatus);
		order.resetChanges();
		orders.fireOrderAvailableEvent(order);
	}

}
