package ru.prolib.aquila.core.BusinessEntities.CommonModel;

import java.util.List;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.Order;
import ru.prolib.aquila.core.BusinessEntities.OrderException;
import ru.prolib.aquila.core.BusinessEntities.OrderNotExistsException;
import ru.prolib.aquila.core.utils.Counter;

public interface Orders {

	public abstract boolean isOrderExists(int id);

	public abstract List<Order> getOrders();

	public abstract Order getOrder(int id) throws OrderException;

	public abstract EventType OnOrderAvailable();

	public abstract void fireEvents(EditableOrder order);

	public abstract EditableOrder getEditableOrder(int id)
			throws OrderNotExistsException;

	public abstract void purgeOrder(int id);

	public abstract EventType OnOrderCancelFailed();

	public abstract EventType OnOrderCancelled();

	public abstract EventType OnOrderChanged();

	public abstract EventType OnOrderDone();

	public abstract EventType OnOrderFailed();

	public abstract EventType OnOrderFilled();

	public abstract EventType OnOrderPartiallyFilled();

	public abstract EventType OnOrderRegistered();

	public abstract EventType OnOrderRegisterFailed();

	public abstract int getOrdersCount();

	/**
	 * Create new order instance.
	 * <p>
	 * This method creates and registers a new order instance.
	 * The order id will be automatically assigned after creation.
	 * <p>
	 * @param terminal - the terminal owner of the order. 
	 * @return new order instance
	 */
	public abstract EditableOrder createOrder(EditableTerminal terminal);

	public abstract EventType OnOrderTrade();
	
	/**
	 * Get order ID sequence.
	 * <p>
	 * @return ID sequence
	 */
	public Counter getIdSequence();

}