package ru.prolib.aquila.dde;

/**
 * Типовое исключение пакета.
 * <p>
 * 2012-07-15<br>
 * $Id: DDEException.java 513 2013-02-11 01:17:18Z whirlwind $
 */
public class DDEException extends Exception {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Создать исключение
	 * <p>
	 * @param msg текст сообщения
	 */
	public DDEException(String msg) {
		super(msg);
	}
	
	/**
	 * Создать исключение
	 * <p>
	 * @param msg текст сообщения
	 * @param t базовое исключение
	 */
	public DDEException(String msg, Throwable t) {
		super(msg, t);
	}
	
	/**
	 * Создать исключение
	 * <p>
	 * @param t базовое исключение
	 */
	public DDEException(Throwable t) {
		super(t);
	}
	
	/**
	 * Создать исключение
	 */
	public DDEException() {
		super();
	}

}
