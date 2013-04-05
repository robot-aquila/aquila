package ru.prolib.aquila.ui.wrapper;

/**
 * Базовое исключение меню.
 * <p>
 * 2013-03-01<br>
 * $Id: MenuException.java 554 2013-03-01 13:43:04Z whirlwind $
 */
public class MenuException extends Exception {
	private static final long serialVersionUID = 7556954655826911342L;
	
	public MenuException() {
		super();
	}
	
	public MenuException(String msg) {
		super(msg);
	}
	
	public MenuException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public MenuException(Throwable t) {
		super(t);
	}

}
