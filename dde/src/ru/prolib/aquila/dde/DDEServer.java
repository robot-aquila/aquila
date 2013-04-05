package ru.prolib.aquila.dde;

/**
 * Интерфейс DDE-сервера.
 * <p>
 * 2012-07-15<br>
 * $Id: DDEServer.java 513 2013-02-11 01:17:18Z whirlwind $
 */
public interface DDEServer {
	
	/**
	 * Запустить сервер.
	 * <p>
	 * @throws DDEException
	 */
	public void start() throws DDEException;
	
	/**
	 * Остановить сервер.
	 * <p>
	 * @throws DDEException
	 */
	public void stop() throws DDEException;
	
	/**
	 * Дождаться завершения сервера.
	 * <p>
	 * @throws DDEException
	 */
	public void join() throws DDEException;
	
	/**
	 * Зарегистрировать сервис.
	 * <p>
	 * @param service сервис
	 * @throws DDEException
	 */
	public void registerService(DDEService service) throws DDEException;
	
	/**
	 * Удалить сервис.
	 * <p>
	 * @param name идентификатор сервиса
	 * @throws DDEException
	 */
	public void unregisterService(String name) throws DDEException;

}
