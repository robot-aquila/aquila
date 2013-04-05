package ru.prolib.aquila.ChaosTheory;

/**
 * Базовое исключение заявки.
 */
public class OrderException extends Exception {
	private static final long serialVersionUID = 1L;

	public OrderException() {
		super();
	}

	public OrderException(String msg) {
		super(msg);
	}

	public OrderException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public OrderException(Throwable t) {
		super(t);
	}

}
