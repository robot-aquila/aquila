package ru.prolib.aquila.core.BusinessEntities;

/**
 * Интерфейс обработчика заявок.
 * <p>
 * 2012-12-11<br>
 * $Id: OrderProcessor.java 490 2013-02-05 19:42:02Z whirlwind $
 */
public interface OrderProcessor {
	
	/**
	 * Разместить заявку на бирже для исполнения.
	 * <p>
	 * @param order заявка
	 * @throws OrderException - TODO:
	 */
	public void placeOrder(Order order) throws OrderException;
	
	/**
	 * Отменить заявку.
	 * <p>
	 * @param order заявка
	 * @throws OrderException - TODO:
	 */
	public void cancelOrder(Order order) throws OrderException;

}
