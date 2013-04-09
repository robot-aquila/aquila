package ru.prolib.aquila.quik.subsys.order;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.BusinessEntities.EditableOrders;
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
			TransEvent e = (TransEvent) event;
			T2QTransStatus status = e.getStatus();
			
			if ( status == T2QTransStatus.DONE ) {
				
				
			} else if ( status == T2QTransStatus.SENT
					 || status == T2QTransStatus.RECV )
			{
				// Эти статусы не являются ошибкой и никак не обрабатываются 
				
			} else {
				// Любой другой статус рассматривается как отклонение транзакции
				
			}
		}
	}

}
