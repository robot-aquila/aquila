package ru.prolib.aquila.quik.api;

import ru.prolib.aquila.t2q.*;

/**
 * Интерфейс базового обработчика данных QUIK API.
 */
public interface QUIKMainHandler {

	/**
	 * Обработать смену статуса подключения.
	 * <p>
	 * @param status текущий статус подключения
	 */
	public void connectionStatus(T2QConnStatus status);
	
	/**
	 * Обработать данные заявки.
	 * <p>
	 * @param order дескриптор заявки
	 */
	public void orderStatus(T2QOrder order);
	
	/**
	 * Обработать данные сделки.
	 * <p>
	 * @param trade дескриптор сделки
	 */
	public void tradeStatus(T2QTrade trade);

}
