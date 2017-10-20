package ru.prolib.aquila.core;

/**
 * Интерфейс пускового механизма.
 * <p>
 * 2012-08-18<br>
 * $Id: Starter.java 490 2013-02-05 19:42:02Z whirlwind $
 */
public interface Starter {

	/**
	 * Запустить в работу.
	 * <p>
	 * @throws StarterException - error at starting.
	 */
	public void start() throws StarterException;

	/**
	 * Остановить работу.
	 * <p>
	 * @throws StarterException - error at stopping.
	 */
	public void stop() throws StarterException;

}