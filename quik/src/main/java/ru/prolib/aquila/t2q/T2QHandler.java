package ru.prolib.aquila.t2q;

/**
 * Интерфейс обработчика событий TRANS2QUIK.
 * <p>
 * Потребитель сервиса реализует данный обработчик и передает его в метод
 * инстанцирования сервиса.
 * <p>
 * 2013-01-29<br>
 * $Id: T2QHandler.java 576 2013-03-14 12:07:25Z whirlwind $
 */
public interface T2QHandler {

	/**
	 * Обработать смену статуса соединения.
	 * <p>
	 * @param status статус подключения
	 */
	public void OnConnStatus(T2QConnStatus status);
	
	/**
	 * Обработать результат транзакции.
	 * <p>
	 * @param status статус транзакции
	 * @param transId номер транзакции
	 * @param orderId номер порожденной заявки или null, если транзакция не
	 * связана с новой заявкой 
	 * @param msg сообщение от торговой системы или сервера QUIK
	 */
	public void OnTransReply(T2QTransStatus status, long transId, Long orderId,
			String msg);
	
	/**
	 * Обработать информацию о заявке.
	 * <p>
	 * @param order детали заявки
	 */
	public void OnOrderStatus(T2QOrder order);
	
	/**
	 * Обработать информацию о сделке.
	 * <p>
	 * @param trade детали сделки
	 */
	public void OnTradeStatus(T2QTrade trade);

}
