package ru.prolib.aquila.quik.api;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.t2q.*;

/**
 * Транслятор событий QUIK API.
 * <p>
 * Транслирует вызовы методов обработчика в события.
 */
public class ApiServiceHandler implements T2QHandler {
	protected final EventDispatcher dispatcher;
	protected final EventTypeMap<Long> onTransReplyMap;
	protected final EventType onConnStatus, onOrderStatus, onTradeStatus;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param dispatcher диспетчер событий
	 * @param onTransReplyMap карта типов событий для отслеживания транзакций
	 * @param onConnStatus тип события смены статуса подключения
	 * @param onOrderStatus тип события смены статуса заявки
	 * @param onTradeStatus тип события информация о сделке
	 */
	public ApiServiceHandler(EventDispatcher dispatcher,
			EventTypeMap<Long> onTransReplyMap, EventType onConnStatus,
			EventType onOrderStatus, EventType onTradeStatus)
	{
		super();
		this.dispatcher = dispatcher;
		this.onTransReplyMap = onTransReplyMap;
		this.onConnStatus = onConnStatus;
		this.onOrderStatus = onOrderStatus;
		this.onTradeStatus = onTradeStatus;
	}

	@Override
	public void OnConnStatus(T2QConnStatus status) {
		dispatcher.dispatch(new ConnEvent(onConnStatus, status));
	}

	@Override
	public void OnOrderStatus(T2QOrder order) {
		dispatcher.dispatch(new OrderEvent(onOrderStatus, order));
	}

	@Override
	public void OnTradeStatus(T2QTrade trade) {
		dispatcher.dispatch(new TradeEvent(onTradeStatus, trade));
	}

	@Override
	public void OnTransReply(T2QTransStatus status,
			long transId, Long orderId, String msg)
	{
		dispatcher.dispatch(new TransEvent(onTransReplyMap.get(transId),
				status, transId, orderId, msg));
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == ApiServiceHandler.class ) {
			ApiServiceHandler o = (ApiServiceHandler) other;
			return new EqualsBuilder()
				.append(dispatcher, o.dispatcher)
				.append(onTransReplyMap, o.onTransReplyMap)
				.append(onConnStatus, o.onConnStatus)
				.append(onOrderStatus, o.onOrderStatus)
				.append(onTradeStatus, o.onTradeStatus)
				.isEquals();
		} else {
			return false;
		}
	}

}
