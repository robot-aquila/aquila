package ru.prolib.aquila.core.BusinessEntities;

/**
 * The order processing interface.
 * <p>
 * 2012-12-11<br>
 * $Id: OrderProcessor.java 490 2013-02-05 19:42:02Z whirlwind $
 */
public interface OrderProcessor {
	
	/**
	 * Place order for execution.
	 * <p>
	 * @param terminal - the terminal.
	 * @param order - the order.
	 * @throws OrderException - TODO:
	 */
	public void placeOrder(EditableTerminal terminal, Order order)
			throws OrderException;
	
	/**
	 * Cancel order.
	 * <p>
	 * @param terminal - the terminal.
	 * @param order - the order.
	 * @throws OrderException - TODO:
	 */
	public void cancelOrder(EditableTerminal terminal, Order order)
			throws OrderException;

}
