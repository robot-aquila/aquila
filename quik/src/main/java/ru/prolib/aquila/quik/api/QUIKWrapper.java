package ru.prolib.aquila.quik.api;

import java.util.*;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.t2q.*;

/**
 * Обработчик данных QUIK API (точка входа).
 * <p>
 * Основная задача - отложить спецификацию обработчика данных до момента после
 * инстанцирования терминала (T2Q требует указания обработчика в момент
 * инстанцирования сервиса). Осуществляет трансляцию вызовов базовому
 * обработчику или соответствующим узкоспециализированным обработчикам.
 */
public class QUIKWrapper implements T2QHandler {
	private QUIKMainHandler hMain;
	private final Map<Integer, QUIKTransactionHandler> hTrans;
	
	public QUIKWrapper() {
		super();
		hTrans = new Hashtable<Integer, QUIKTransactionHandler>();
	}

	@Override
	public void OnConnStatus(T2QConnStatus status) {
		getMainHandler().connectionStatus(status);
	}

	@Override
	public void OnOrderStatus(T2QOrder order) {
		getMainHandler().orderStatus(order);
	}

	@Override
	public void OnTradeStatus(T2QTrade trade) {
		getMainHandler().tradeStatus(trade);
	}

	@Override
	public void OnTransReply(T2QTransStatus status, long transId, Long orderId,
			String msg)
	{
		int id = (int) transId;
		QUIKTransactionHandler handler = getHandler(id);
		if ( handler != null ) {
			handler.handle(new QUIKResponse(status, id, orderId, msg));
		}
	}
	
	/**
	 * Установить обработчик транзакции.
	 * <p>
	 * @param transId номер транзакции
	 * @param handler обработчик
	 */
	public void setHandler(int transId, QUIKTransactionHandler handler) {
		synchronized ( hTrans ) {
			hTrans.put(transId, handler);
		}
	}
	
	/**
	 * Получить обработчик транзакции.
	 * <p>
	 * @param transId номер транзакции
	 * @return обработчик или null, если нет соответствующего обработчика
	 */
	public QUIKTransactionHandler getHandler(int transId) {
		synchronized ( hTrans ) {
			return hTrans.get(transId);
		}
	}
	
	/**
	 * Удалить обработчик транзакции.
	 * <p>
	 * @param transId номер транзакции
	 */
	public void removeHandler(int transId) {
		synchronized ( hTrans ) {
			hTrans.remove(transId);
		}
	}
	
	/**
	 * Установить основной обработчик данных.
	 * <p>
	 * @param handler обработчик данных
	 */
	public synchronized void setMainHandler(QUIKMainHandler handler) {
		hMain = handler;
	}
	
	/**
	 * Получить основной обработчик данных.
	 * <p>
	 * @return основной обработчик или null, если обработчик не задан
	 */
	public synchronized QUIKMainHandler getMainHandler() {
		return hMain;
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != QUIKWrapper.class ) {
			return false;
		}
		QUIKWrapper o = (QUIKWrapper) other;
		return new EqualsBuilder()
			.append(o.hMain, hMain)
			.append(o.hTrans, hTrans)
			.isEquals();
	}

}
