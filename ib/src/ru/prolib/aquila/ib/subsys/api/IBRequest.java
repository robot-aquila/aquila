package ru.prolib.aquila.ib.subsys.api;

/**
 * Базовый интерфейс запроса к IB API.
 * <p>
 * 2012-11-19<br>
 * $Id: IBRequest.java 499 2013-02-07 10:43:25Z whirlwind $
 */
public interface IBRequest {
	
	/**
	 * Выполнить запрос.
	 */
	public void start();
	
	/**
	 * Остановить запрос (для подписок).
	 */
	public void stop();

}
