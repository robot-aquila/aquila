package ru.prolib.aquila.dde;

/**
 * Интерфейс обработчика транзакций DDE-сервиса.
 * <p>
 * 2012-07-15<br>
 * $Id: DDEService.java 232 2012-07-16 12:21:33Z whirlwind $
 */
public interface DDEService {
	
	/**
	 * Получить идентификатор сервиса.
	 * <p>
	 * @return идентификатор
	 */
	public String getName();
	
	/**
	 * Обработка подключения.
	 * <p>
	 * @param topic тема
	 * @return true - разрешить подключение, false - отклонить подключение
	 */
	public boolean onConnect(String topic);
	
	/**
	 * Обработка успешного подключения.
	 * <p>
	 * @param topic тема
	 */
	public void onConnectConfirm(String topic);
	
	/**
	 * Обработать входные данные.
	 * <p>
	 * @param topic тема
	 * @param item субъект
	 * @param dataBuffer данные DDE-транзакции XTYP_POKE
	 * @return true - данные обработаны, false - требуется разбор таблицы и
	 * вызов метода {@link #onTable(DDETable)}.
	 */
	public boolean onData(String topic, String item, byte[] dataBuffer);
	
	/**
	 * Обработать входную таблицу.
	 * <p>
	 * Метод вызывается только в случае, если метод
	 * {@link #onData(String, String, byte[])} вернул false.
	 * <p>
	 * @param table таблица сформированная в результате разбора последней
	 * транзакции XTYP_POKE.
	 */
	public void onTable(DDETable table);
	
	/**
	 * Обработать отключение.
	 * <p>
	 * @param topic тема
	 */
	public void onDisconnect(String topic);
	
	/**
	 * Обработать регистрацию сервиса в DDE-сервере.
	 */
	public void onRegister();
	
	/**
	 * Обработать удаление сервиса из DDE-сервера.
	 */
	public void onUnregister();

}
