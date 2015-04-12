package ru.prolib.aquila.core.BusinessEntities.CommonModel;

import java.util.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.*;
import ru.prolib.aquila.core.utils.Counter;

/**
 * Хранилище заявок.
 * <p>
 * Данный класс представляет собой хранилище заявок, предназначенное для
 * использования поставщиками данных. Реализуемый в рамках хранилища
 * интерфейс {@link OrdersImpl} позволяет так же использовать данный класс
 * потребителями сервиса заявок.
 */
public class OrdersImpl implements Orders {
	private final Map<Integer, EditableOrder> orders;
	private final OrdersEventDispatcher dispatcher;
	private final OrderFactory factory;
	private final Counter idSeq;
	
	/**
	 * Constructor.
	 * <p>
	 * @param dispatcher - event dispatcher
	 * @param factory - order factory
	 * @param orderIdSeq - id sequence
	 */
	public OrdersImpl(OrdersEventDispatcher dispatcher, OrderFactory factory,
			Counter orderIdSeq)
	{
		super();
		orders = new LinkedHashMap<Integer, EditableOrder>();
		this.dispatcher = dispatcher;
		this.factory = factory;
		this.idSeq = orderIdSeq;
	}
	
	/**
	 * Get event dispatcher.
	 * <p>
	 * @return event dispatcher
	 */
	public OrdersEventDispatcher getEventDispatcher() {
		return dispatcher;
	}
	
	/**
	 * Get order factory.
	 * <p>
	 * @return order factory
	 */
	public OrderFactory getOrderFactory() {
		return factory;
	}
	
	/**
	 * Get order ID sequence.
	 * <p>
	 * @return ID sequence
	 */
	public Counter getIdSequence() {
		return idSeq;
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.BusinessEntities.CommonModel.Orders#isOrderExists(int)
	 */
	@Override
	public boolean isOrderExists(int id) {
		return orders.containsKey(id);
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.BusinessEntities.CommonModel.Orders#getOrders()
	 */
	@Override
	public List<Order> getOrders() {
		return new LinkedList<Order>(orders.values());
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.BusinessEntities.CommonModel.Orders#getOrder(int)
	 */
	@Override
	public Order getOrder(int id) throws OrderException {
		return getEditableOrder(id);
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.BusinessEntities.CommonModel.Orders#OnOrderAvailable()
	 */
	@Override
	public EventType OnOrderAvailable() {
		return dispatcher.OnAvailable();
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.BusinessEntities.CommonModel.Orders#fireEvents(ru.prolib.aquila.core.BusinessEntities.EditableOrder)
	 */
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

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.BusinessEntities.CommonModel.Orders#getEditableOrder(int)
	 */
	@Override
	public EditableOrder getEditableOrder(int id)
		throws OrderNotExistsException
	{
		EditableOrder order = orders.get(id);
		if ( order == null ) {
			throw new OrderNotExistsException(id);
		}
		return order;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.BusinessEntities.CommonModel.Orders#purgeOrder(int)
	 */
	@Override
	public void purgeOrder(int id) {
		EditableOrder order = orders.get(id);
		if ( order != null ) {
			dispatcher.stopRelayFor(order);
		}
		orders.remove(id);
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.BusinessEntities.CommonModel.Orders#OnOrderCancelFailed()
	 */
	@Override
	public EventType OnOrderCancelFailed() {
		return dispatcher.OnCancelFailed();
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.BusinessEntities.CommonModel.Orders#OnOrderCancelled()
	 */
	@Override
	public EventType OnOrderCancelled() {
		return dispatcher.OnCancelled();
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.BusinessEntities.CommonModel.Orders#OnOrderChanged()
	 */
	@Override
	public EventType OnOrderChanged() {
		return dispatcher.OnChanged();
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.BusinessEntities.CommonModel.Orders#OnOrderDone()
	 */
	@Override
	public EventType OnOrderDone() {
		return dispatcher.OnDone();
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.BusinessEntities.CommonModel.Orders#OnOrderFailed()
	 */
	@Override
	public EventType OnOrderFailed() {
		return dispatcher.OnFailed();
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.BusinessEntities.CommonModel.Orders#OnOrderFilled()
	 */
	@Override
	public EventType OnOrderFilled() {
		return dispatcher.OnFilled();
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.BusinessEntities.CommonModel.Orders#OnOrderPartiallyFilled()
	 */
	@Override
	public EventType OnOrderPartiallyFilled() {
		return dispatcher.OnPartiallyFilled();
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.BusinessEntities.CommonModel.Orders#OnOrderRegistered()
	 */
	@Override
	public EventType OnOrderRegistered() {
		return dispatcher.OnRegistered();
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.BusinessEntities.CommonModel.Orders#OnOrderRegisterFailed()
	 */
	@Override
	public EventType OnOrderRegisterFailed() {
		return dispatcher.OnRegisterFailed();
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.BusinessEntities.CommonModel.Orders#getOrdersCount()
	 */
	@Override
	public int getOrdersCount() {
		return orders.size();
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.BusinessEntities.CommonModel.Orders#createOrder(ru.prolib.aquila.core.BusinessEntities.EditableTerminal)
	 */
	@Override
	public EditableOrder createOrder(EditableTerminal terminal) {
		EditableOrder order = factory.createOrder(terminal);
		int id = idSeq.incrementAndGet();
		order.setId(id);
		orders.put(id, order);
		dispatcher.startRelayFor(order);
		return order;
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

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.BusinessEntities.CommonModel.Orders#OnOrderTrade()
	 */
	@Override
	public EventType OnOrderTrade() {
		return dispatcher.OnTrade();
	}

}
