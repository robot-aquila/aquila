package ru.prolib.aquila.core.BusinessEntities.CommonModel;

import java.util.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.*;

/**
 * Хранилище заявок.
 * <p>
 * Данный класс представляет собой хранилище заявок, предназначенное для
 * использования поставщиками данных. Реализуемый в рамках хранилища
 * интерфейс {@link Orders} позволяет так же использовать данный класс
 * потребителями сервиса заявок.
 */
public class Orders {
	private final Map<Integer, EditableOrder> orders;
	private final OrdersEventDispatcher dispatcher;
	
	/**
	 * Создать хранилище заявок.
	 * <p>
	 * @param dispatcher диспетчер событий
	 */
	public Orders(OrdersEventDispatcher dispatcher) {
		super();
		orders = new LinkedHashMap<Integer, EditableOrder>();
		this.dispatcher = dispatcher;
	}
	
	/**
	 * Получить диспетчер событий.
	 * <p>
	 * @return диспетчер событий
	 */
	public OrdersEventDispatcher getEventDispatcher() {
		return dispatcher;
	}

	public boolean isOrderExists(int id) {
		return orders.containsKey(id);
	}

	public List<Order> getOrders() {
		return new LinkedList<Order>(orders.values());
	}

	public Order getOrder(int id) throws OrderException {
		return getEditableOrder(id);
	}

	public EventType OnOrderAvailable() {
		return dispatcher.OnAvailable();
	}

	public void fireEvents(EditableOrder order) {
		synchronized ( order ) {
			if ( order.isAvailable() ) {
				order.fireChangedEvent();
			} else {
				order.setAvailable(true);
				dispatcher.fireAvailable(order);
			}
			order.resetChanges();
		}
	}

	public EditableOrder getEditableOrder(int id)
		throws OrderNotExistsException
	{
		EditableOrder order = orders.get(id);
		if ( order == null ) {
			throw new OrderNotExistsException(id);
		}
		return order;
	}
	
	public void purgeOrder(int id) {
		EditableOrder order = orders.get(id);
		if ( order != null ) {
			dispatcher.stopRelayFor(order);
		}
		orders.remove(id);
	}

	public EventType OnOrderCancelFailed() {
		return dispatcher.OnCancelFailed();
	}

	public EventType OnOrderCancelled() {
		return dispatcher.OnCancelled();
	}

	public EventType OnOrderChanged() {
		return dispatcher.OnChanged();
	}

	public EventType OnOrderDone() {
		return dispatcher.OnDone();
	}

	public EventType OnOrderFailed() {
		return dispatcher.OnFailed();
	}

	public EventType OnOrderFilled() {
		return dispatcher.OnFilled();
	}

	public EventType OnOrderPartiallyFilled() {
		return dispatcher.OnPartiallyFilled();
	}

	public EventType OnOrderRegistered() {
		return dispatcher.OnRegistered();
	}

	public EventType OnOrderRegisterFailed() {
		return dispatcher.OnRegisterFailed();
	}

	public int getOrdersCount() {
		return orders.size();
	}

	public void registerOrder(int id, EditableOrder order)
			throws OrderAlreadyExistsException
	{
		if ( orders.containsKey(id) ) {
			throw new OrderAlreadyExistsException(order);
		}
		order.setId(id);
		orders.put(id, order);
		dispatcher.startRelayFor(order);
	}

	public EditableOrder createOrder(EditableTerminal<?> terminal) {
		OrderEventDispatcher d =
			new OrderEventDispatcher(terminal.getEventSystem()); 

		List<OrderStateHandler> h = new Vector<OrderStateHandler>();
		add(h, d, new OrderIsRegistered(), d.OnRegistered());
		add(h, d, new OrderIsRegisterFailed(), d.OnRegisterFailed());
		add(h, d, new OrderIsCancelled(), d.OnCancelled());
		add(h, d, new OrderIsCancelFailed(), d.OnCancelFailed());
		add(h, d, new OrderIsFilled(), d.OnFilled());
		add(h, d, new OrderIsPartiallyFilled(), d.OnPartiallyFilled());
		add(h, d, new OrderIsChanged(), d.OnChanged());
		add(h, d, new OrderIsDone(), d.OnDone());
		add(h, d, new OrderIsFailed(), d.OnFailed());
		return new OrderImpl(d, h, terminal);
	}
	
	private void add(List<OrderStateHandler> list,
			OrderEventDispatcher dispatcher, OrderStateValidator validator,
			EventType targetType)
	{
		list.add(new OrderStateHandler(dispatcher, validator, (EventTypeSI) targetType));
	}
	
	/**
	 * Установить экземпляр зарегистрированной заявки.
	 * <p>
	 * Только для тестирования.
	 * <p>
	 * @param id номер заявки
	 * @param order экземпляр заявки
	 */
	protected void setOrder(int id, EditableOrder order) {
		orders.put(id, order);
	}

	public EventType OnOrderTrade() {
		return dispatcher.OnTrade();
	}

}
