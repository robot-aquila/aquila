package ru.prolib.aquila.quik.api;

/**
 * Интерфейс обработчика транзакции QUIK.
 */
public interface QUIKTransactionHandler {

	/**
	 * Обработать статус транзакции.
	 * <p>
	 * @param response информация о транзакции
	 */
	public void handle(QUIKResponse response);

}
