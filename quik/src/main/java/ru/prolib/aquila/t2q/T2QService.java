package ru.prolib.aquila.t2q;

/**
 * Интерфейс сервиса.
 * <p>
 * Поставщик сервиса предоставляет собственную реализацию данного интерфейса.
 * Конструктор специфической реализации неизвестен заранее. Для создания
 * экземпляра сервиса следует использовать фабрику сервисов, которая реализуется
 * поставщиком.
 * <p>
 * 2013-01-29<br>
 * $Id: T2QService.java 493 2013-02-06 05:37:55Z whirlwind $
 */
public interface T2QService {
	
	/**
	 * Инициировать подключение к терминалу.
	 * <p>
	 * @param connParam строка параметров подключения
	 */
	public void connect(String connParam) throws T2QException;
	
	/**
	 * Разорвать подключение к терминалу.
	 */
	public void disconnect();
	
	/**
	 * Отправить транзакцию.
	 * <p>
	 * @param transSpec спецификация транзакции
	 */
	public void send(String transSpec) throws T2QException;

}
