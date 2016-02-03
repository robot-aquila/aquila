package ru.prolib.aquila.core.BusinessEntities;

import ru.prolib.aquila.core.EventQueue;

/**
 * Интерфейс модифицируемого терминала.
 * <p>
 * 2013-01-05<br>
 * $Id: EditableTerminal.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public interface EditableTerminal extends Terminal {
	
	/**
	 * Get event queue.
	 * <p>
	 * @return the event queue
	 */
	public EventQueue getEventQueue();
	
	/**
	 * Get security instance.
	 * <p>
	 * If security not exists then new security instance will be created.
	 * <p>
	 * @param symbol - symbol
	 * @return security instance
	 */
	public EditableSecurity getEditableSecurity(Symbol symbol);
	
	/**
	 * Get portfolio instance.
	 * <p>
	 * If portfolio not exists then new portfolio will be created.
	 * <p>
	 * @param account - account
	 * @return portfolio instance
	 */
	public EditablePortfolio getEditablePortfolio(Account account);
	
	/**
	 * Create new order instance.
	 * <p>
	 * @param account - account
	 * @param symbol - symbol
	 * @return new order instance
	 */
	public EditableOrder createOrder(Account account, Symbol symbol);
		
	/**
	 * Get order instance.
	 * <p>
	 * @param id - order ID
	 * @return order instance
	 * @throws OrderNotExistsException - if order not exists
	 */
	public EditableOrder getEditableOrder(long id)
		throws OrderNotExistsException;

	/**
	 * Set default portfolio.
	 * <p>
	 * @param portfolio - default portfolio instance
	 */
	public void setDefaultPortfolio(EditablePortfolio portfolio);
	
}
