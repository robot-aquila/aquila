package ru.prolib.aquila.core.BusinessEntities;

import java.util.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.utils.*;

/**
 * Хранилище заявок.
 * <p>
 * Данный класс представляет собой хранилище заявок, предназначенное для
 * использования поставщиками данных. Реализуемый в рамках хранилища
 * интерфейс {@link Orders} позволяет так же использовать данный класс
 * потребителями сервиса заявок.
 */
public class OrdersImpl implements EditableOrders {
	private final Map<Integer, EditableOrder> orders;
	private final OrdersEventDispatcher dispatcher;
	
	/**
	 * Создать хранилище заявок.
	 * <p>
	 * @param dispatcher диспетчер событий
	 */
	public OrdersImpl(OrdersEventDispatcher dispatcher) {
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

	@Override
	public synchronized boolean isOrderExists(int id) {
		return orders.containsKey(id);
	}

	@Override
	public synchronized List<Order> getOrders() {
		return new LinkedList<Order>(orders.values());
	}

	@Override
	public synchronized Order getOrder(int id) throws OrderException {
		return getEditableOrder(id);
	}

	@Override
	public EventType OnOrderAvailable() {
		return dispatcher.OnAvailable();
	}

	@Override
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

	@Override
	public synchronized EditableOrder getEditableOrder(int id)
		throws OrderNotExistsException
	{
		EditableOrder order = orders.get(id);
		if ( order == null ) {
			throw new OrderNotExistsException(id);
		}
		return order;
	}
	
	@Override
	public synchronized void purgeOrder(int id) {
		EditableOrder order = orders.get(id);
		if ( order != null ) {
			dispatcher.stopRelayFor(order);
		}
		orders.remove(id);
	}

	@Override
	public EventType OnOrderCancelFailed() {
		return dispatcher.OnCancelFailed();
	}

	@Override
	public EventType OnOrderCancelled() {
		return dispatcher.OnCancelled();
	}

	@Override
	public EventType OnOrderChanged() {
		return dispatcher.OnChanged();
	}

	@Override
	public EventType OnOrderDone() {
		return dispatcher.OnDone();
	}

	@Override
	public EventType OnOrderFailed() {
		return dispatcher.OnFailed();
	}

	@Override
	public EventType OnOrderFilled() {
		return dispatcher.OnFilled();
	}

	@Override
	public EventType OnOrderPartiallyFilled() {
		return dispatcher.OnPartiallyFilled();
	}

	@Override
	public EventType OnOrderRegistered() {
		return dispatcher.OnRegistered();
	}

	@Override
	public EventType OnOrderRegisterFailed() {
		return dispatcher.OnRegisterFailed();
	}

	@Override
	public synchronized int getOrdersCount() {
		return orders.size();
	}

	@Override
	public synchronized void registerOrder(int id, EditableOrder order)
			throws OrderAlreadyExistsException
	{
		if ( orders.containsKey(id) ) {
			throw new OrderAlreadyExistsException(order);
		}
		order.setId(id);
		orders.put(id, order);
		dispatcher.startRelayFor(order);
	}

	@Override
	public synchronized EditableOrder createOrder(EditableTerminal terminal) {
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
	
	private final void add(List<OrderStateHandler> list,
			OrderEventDispatcher dispatcher, OrderStateValidator validator,
			EventType targetType)
	{
		list.add(new OrderStateHandler(dispatcher, validator, targetType));
	}
	
	/**
	 * Установить экземпляр зарегистрированной заявки.
	 * <p>
	 * Только для тестирования.
	 * <p>
	 * @param id номер заявки
	 * @param order экземпляр заявки
	 */
	protected synchronized void setOrder(int id, EditableOrder order) {
		orders.put(id, order);
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != OrdersImpl.class ) {
			return false;
		}
		OrdersImpl o = (OrdersImpl) other;
		return new EqualsBuilder()
			.append(o.orders, orders)
			.isEquals();
	}

	@Override
	public EventType OnOrderTrade() {
		return dispatcher.OnTrade();
	}

}
