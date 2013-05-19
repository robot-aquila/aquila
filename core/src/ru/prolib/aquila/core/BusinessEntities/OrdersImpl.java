package ru.prolib.aquila.core.BusinessEntities;

import java.util.*;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.BusinessEntities.utils.*;
import ru.prolib.aquila.core.BusinessEntities.validator.*;

/**
 * Реализация набора заявок.
 * <p>
 * Данный класс представляет собой хранилище заявок, предназначенное для
 * использования поставщиками сервиса заявок. Реализуемый в рамках хранилища
 * интерфейс {@link Orders} позволяет так же использовать данный класс
 * потребителями сервиса заявок.
 * <p>
 * Фактически, хранилище оперирует двумя наборами заявок: заявками в очереди
 * ожидания поступления данных и собственно заявками, данные о которых уже
 * поступили. Идентификация ожидающих заявок выполняется по номеру транзакции.
 * Подразумевается, что номер транзакции назначается поставщиком сервиса заявок
 * при программном создании заявки. Идентификация подтвержденных заявок
 * выполняется по номеру заявки.
 * <p>
 * Данный класс не выполняет какого-либо контроля за состоянием заявок и
 * своевременным переводом заявки из ожидающих в подтвержденные, а служит
 * исключительно хранилищем. Алгоритм управления заявками должна быть
 * реализована в рамках поставщика сервиса заявок.
 * <p>
 * 2012-10-17<br>
 * $Id: OrdersImpl.java 562 2013-03-06 15:22:54Z whirlwind $
 */
public class OrdersImpl implements EditableOrders, EventListener {
	private final Map<Long, EditableOrder> pending;
	private final Map<Long, EditableOrder> orders;
	private final EventDispatcher dispatcher;
	private final EventType onAvailable;
	private final EventType onCancelFailed;
	private final EventType onCancelled;
	private final EventType onChanged;
	private final EventType onDone;
	private final EventType onFailed;
	private final EventType onFilled;
	private final EventType onPartiallyFilled;
	private final EventType onRegistered;
	private final EventType onRegisterFailed;
	
	/**
	 * Создать хранилище заявок.
	 * <p>
	 * @param dispatcher диспетчер событий
	 * @param onAvailable тип события: доступна новая заявка
	 * @param onCancelFailed тип события: при провале отмены заявки
	 * @param onCancelled тип события: при отмене заявки
	 * @param onChanged тип события: при изменении атрибутов заявки
	 * @param onDone тип события: при сведении или отмене заявки
	 * @param onFailed тип события: при провале операции в связи с заявкой
	 * @param onFilled тип события: при сведении заявки
	 * @param onPartiallyFilled тип события: при частичном сведении заявки
	 * @param onRegistered тип события: при регистрации заявки
	 * @param onRegisterFailed тип события: при ошибке регистрации заявки
	 */
	public OrdersImpl(EventDispatcher dispatcher, EventType onAvailable,
			EventType onCancelFailed, EventType onCancelled,
			EventType onChanged, EventType onDone, EventType onFailed,
			EventType onFilled, EventType onPartiallyFilled,
			EventType onRegistered, EventType onRegisterFailed)
	{
		super();
		pending = new LinkedHashMap<Long, EditableOrder>();
		orders = new LinkedHashMap<Long, EditableOrder>();
		this.dispatcher = dispatcher;
		this.onAvailable = onAvailable;
		this.onCancelFailed = onCancelFailed;
		this.onCancelled = onCancelled;
		this.onChanged = onChanged;
		this.onDone = onDone;
		this.onFailed = onFailed;
		this.onFilled = onFilled;
		this.onPartiallyFilled = onPartiallyFilled;
		this.onRegistered = onRegistered;
		this.onRegisterFailed = onRegisterFailed;
	}
	
	/**
	 * Получить диспетчер событий.
	 * <p>
	 * @return диспетчер событий
	 */
	public EventDispatcher getEventDispatcher() {
		return dispatcher;
	}

	@Override
	public synchronized boolean isOrderExists(long id) {
		return orders.containsKey(id);
	}

	@Override
	public synchronized List<Order> getOrders() {
		return new LinkedList<Order>(orders.values());
	}

	@Override
	public synchronized Order getOrder(long id) throws OrderException {
		return getEditableOrder(id);
	}

	@Override
	public EventType OnOrderAvailable() {
		return onAvailable;
	}

	@Override
	public void fireOrderAvailableEvent(Order order) {
		dispatcher.dispatch(new OrderEvent(onAvailable, order));
	}

	@Override
	public synchronized EditableOrder getEditableOrder(long id)
		throws OrderNotExistsException
	{
		EditableOrder order = orders.get(id);
		if ( order == null ) {
			throw new OrderNotExistsException(id);
		}
		return order;
	}
	
	@Override
	public synchronized void purgeOrder(long id) {
		EditableOrder order = orders.get(id);
		if ( order != null ) {
			order.OnCancelFailed().removeListener(this);
			order.OnCancelled().removeListener(this);
			order.OnChanged().removeListener(this);
			order.OnDone().removeListener(this);
			order.OnFailed().removeListener(this);
			order.OnFilled().removeListener(this);
			order.OnPartiallyFilled().removeListener(this);
			order.OnRegistered().removeListener(this);
			order.OnRegisterFailed().removeListener(this);
		}
		orders.remove(id);
	}

	@Override
	public synchronized boolean isPendingOrder(long transId) {
		return pending.containsKey(transId);
	}

	@Override
	public synchronized void purgePendingOrder(long transId) {
		pending.remove(transId);
	}

	@Override
	public synchronized EditableOrder getPendingOrder(long transId)
		throws OrderNotExistsException
	{
		EditableOrder order = pending.get(transId);
		if ( order == null ) {
			throw new OrderNotExistsException(transId);
		}
		return order;
	}

	@Override
	public EventType OnOrderCancelFailed() {
		return onCancelFailed;
	}

	@Override
	public EventType OnOrderCancelled() {
		return onCancelled;
	}

	@Override
	public EventType OnOrderChanged() {
		return onChanged;
	}

	@Override
	public EventType OnOrderDone() {
		return onDone;
	}

	@Override
	public EventType OnOrderFailed() {
		return onFailed;
	}

	@Override
	public EventType OnOrderFilled() {
		return onFilled;
	}

	@Override
	public EventType OnOrderPartiallyFilled() {
		return onPartiallyFilled;
	}

	@Override
	public EventType OnOrderRegistered() {
		return onRegistered;
	}

	@Override
	public EventType OnOrderRegisterFailed() {
		return onRegisterFailed;
	}

	@Override
	public void onEvent(Event event) {
		if ( event instanceof OrderEvent ) {
			Order order = ((OrderEvent) event).getOrder();
			EventType map[][] = {
					{ order.OnCancelFailed(), onCancelFailed },
					{ order.OnCancelled(), onCancelled },
					{ order.OnChanged(), onChanged },
					{ order.OnDone(), onDone },
					{ order.OnFailed(), onFailed },
					{ order.OnFilled(), onFilled },
					{ order.OnPartiallyFilled(), onPartiallyFilled },
					{ order.OnRegistered(), onRegistered },
					{ order.OnRegisterFailed(), onRegisterFailed },
			};
			for ( int i = 0; i < map.length; i ++ ) {
				if ( event.isType(map[i][0]) ) {
					dispatcher.dispatch(new OrderEvent(map[i][1], order));
					break;
				}
			}
		}
	}

	@Override
	public synchronized int getOrdersCount() {
		return orders.size();
	}

	@Override
	public synchronized void registerOrder(long id, EditableOrder order)
			throws OrderAlreadyExistsException
	{
		if ( orders.containsKey(id) ) {
			throw new OrderAlreadyExistsException(id);
		}
		order.setId(id);
		orders.put(id, order);
		order.OnCancelFailed().addListener(this);
		order.OnCancelled().addListener(this);
		order.OnChanged().addListener(this);
		order.OnDone().addListener(this);
		order.OnFailed().addListener(this);
		order.OnFilled().addListener(this);
		order.OnPartiallyFilled().addListener(this);
		order.OnRegistered().addListener(this);
		order.OnRegisterFailed().addListener(this);
	}

	@Override
	public synchronized boolean hasPendingOrders() {
		return pending.size() > 0;
	}

	@Override
	public synchronized void
		registerPendingOrder(long transId, EditableOrder order)
			throws OrderAlreadyExistsException
	{
		if ( pending.containsKey(transId) ) {
			throw new OrderAlreadyExistsException(transId);
		}
		order.setTransactionId(transId);
		pending.put(transId, order);
	}

	@Override
	public synchronized EditableOrder
		movePendingOrder(long transId, long orderId) throws OrderException
	{
		if ( isOrderExists(orderId) ) {
			throw new OrderAlreadyExistsException(orderId);
		}
		if ( ! isPendingOrder(transId) ) {
			throw new OrderNotExistsException(transId);
		}
		EditableOrder order = getPendingOrder(transId);
		registerOrder(orderId, order);
		purgePendingOrder(transId);
		return order;
	}

	@Override
	public synchronized EditableOrder createOrder(EditableTerminal terminal) {
		EventDispatcher d = terminal.getEventSystem()
			.createEventDispatcher("Order");
		List<OrderEventHandler> h = new Vector<OrderEventHandler>();
		h.add(new OrderEventHandler(d, new OrderIsRegistered(),
				d.createType("OnRegister")));
		h.add(new OrderEventHandler(d, new OrderIsRegisterFailed(),
				d.createType("OnRegisterFailed")));
		h.add(new OrderEventHandler(d, new OrderIsCancelled(),
				d.createType("OnCancelled")));
		h.add(new OrderEventHandler(d, new OrderIsCancelFailed(),
				d.createType("OnCancelFailed")));
		h.add(new OrderEventHandler(d, new OrderIsFilled(),
				d.createType("OnFilled")));
		h.add(new OrderEventHandler(d, new OrderIsPartiallyFilled(),
				d.createType("OnPartiallyFilled")));
		h.add(new OrderEventHandler(d, new OrderIsChanged(),
				d.createType("OnChanged")));
		h.add(new OrderEventHandler(d, new OrderIsDone(),
				d.createType("OnDone")));
		h.add(new OrderEventHandler(d, new OrderIsFailed(),
				d.createType("OnFailed")));
		return new OrderImpl(d, h.get(0).getEventType(), 
				h.get(1).getEventType(), h.get(2).getEventType(),
				h.get(3).getEventType(), h.get(4).getEventType(),
				h.get(5).getEventType(), h.get(6).getEventType(),
				h.get(7).getEventType(), h.get(8).getEventType(),
				d.createType("OnTrade"), h, terminal);
	}
	
	/**
	 * Установить экземпляр зарегистрированной заявки.
	 * <p>
	 * Только для тестирования.
	 * <p>
	 * @param id номер заявки
	 * @param order экземпляр заявки
	 */
	protected synchronized void setOrder(long id, EditableOrder order) {
		orders.put(id, order);
	}
	
	/**
	 * Установить экземпляр ожидающей заявки.
	 * <p>
	 * Только для тестирования.
	 * <p>
	 * @param transId номер транзакции
	 * @param order экземпляр заявки
	 */
	protected synchronized
		void setPendingOrder(long transId, EditableOrder order)
	{
		pending.put(transId, order);
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
			.append(o.dispatcher, dispatcher)
			.append(o.onAvailable, onAvailable)
			.append(o.onCancelFailed, onCancelFailed)
			.append(o.onCancelled, onCancelled)
			.append(o.onChanged, onChanged)
			.append(o.onDone, onDone)
			.append(o.onFailed, onFailed)
			.append(o.onFilled, onFilled)
			.append(o.onPartiallyFilled, onPartiallyFilled)
			.append(o.onRegistered, onRegistered)
			.append(o.onRegisterFailed, onRegisterFailed)
			.append(o.orders, orders)
			.append(o.pending, pending)
			.isEquals();
	}

}
