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
	private final EventDispatcher dispatcher;
	private final EventTypeMap<Long> onTransReplyMap;
	private final EventType onConnStatus, onOrderStatus, onTradeStatus;
	
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
	
	/**
	 * Получить диспетчер событий.
	 * <p>
	 * @return диспетчер событий
	 */
	public EventDispatcher getEventDispatcher() {
		return dispatcher;
	}
	
	/**
	 * Получить ассоциативную карту типов событий для транзакций.
	 * <p>
	 * @return карта типов событий
	 */
	public EventTypeMap<Long> getEventTypeMap() {
		return onTransReplyMap;
	}
	
	/**
	 * Получить тип события: при смене статуса подключения к терминалу.
	 * <p>
	 * @return тип события
	 */
	public EventType OnConnStatus() {
		return onConnStatus;
	}
	
	/**
	 * Получить тип события: при получении информации о заявке.
	 * <p>
	 * @return тип события
	 */
	public EventType OnOrderStatus() {
		return onOrderStatus;
	}
	
	/**
	 * Получить тип события: при получении информации о сделке.
	 * <p>
	 * @return тип события
	 */
	public EventType OnTradeStatus() {
		return onTradeStatus;
	}
	
	/**
	 * Получить тип события: ответ на транзакцию.
	 * <p>
	 * @param transId номер транзакции
	 * @return тип события
	 */
	public EventType OnTransReply(long transId) {
		return onTransReplyMap.get(transId);
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
		EventType type = onTransReplyMap.get(transId);
		dispatcher.dispatchForCurrentList(new TransEvent(type,
				status, transId, orderId, msg));
		if ( status != T2QTransStatus.SENT && status != T2QTransStatus.RECV ) {
			onTransReplyMap.remove(transId);
			dispatcher.removeListeners(type);
		}
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