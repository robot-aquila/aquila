package ru.prolib.aquila.core.BusinessEntities;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.EventType;

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
	 * @param onAvailable тип события
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
		Order order = getEditableOrder(id);
		if ( order == null ) {
			throw new OrderNotExistsException(id);
		}
		return order;
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
	public synchronized EditableOrder getEditableOrder(long id) {
		return orders.get(id);
	}

	@Override
	public synchronized void registerOrder(EditableOrder order)
			throws OrderException
	{
		Long id = order.getId();
		if ( id == null ) {
			throw new OrderException("Order id was not specified");
		}
		if ( orders.containsKey(id) ) {
			throw new OrderAlreadyExistsException(id);
		}
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
	public synchronized void purgeOrder(EditableOrder order) {
		purgeOrder(order.getId());
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
	public synchronized void registerPendingOrder(EditableOrder order)
			throws OrderException
	{
		Long id = order.getTransactionId();
		if ( id == null ) {
			throw new OrderException("Order transaction id was not specified");
		}
		if ( pending.containsKey(id) ) {
			throw new OrderAlreadyExistsException(id);
		}
		pending.put(id, order);
	}

	@Override
	public synchronized void purgePendingOrder(EditableOrder order) {
		pending.remove(order.getTransactionId());
	}

	@Override
	public synchronized void purgePendingOrder(long transId) {
		pending.remove(transId);
	}

	@Override
	public synchronized EditableOrder getPendingOrder(long transId) {
		return pending.get(transId);
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
	public synchronized EditableOrder
		makePendingOrderAsRegisteredIfExists(long transId, long orderId)
	 		throws OrderException
	{
		EditableOrder order = null;
		if ( isPendingOrder(transId) ) {
			order = getPendingOrder(transId);
			order.setId(orderId);
			registerOrder(order);
			purgePendingOrder(transId);
		}
		return order;
	}

}
