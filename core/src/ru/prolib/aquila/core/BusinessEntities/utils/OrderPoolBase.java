package ru.prolib.aquila.core.BusinessEntities.utils;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Основание пула заявок.
 * <p>
 * Реализует базовые методы работы с пулом заявок.
 */
public class OrderPoolBase implements EventListener {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(OrderPoolBase.class);
	}
	
	private final Set<Order> pending, active, done;
	
	/**
	 * Служебный конструктор.
	 * <p>
	 * @param pending хранилище новых
	 * @param active хранилище активных
	 * @param done  хранилище завершенных
	 */
	OrderPoolBase(Set<Order> pending, Set<Order> active, Set<Order> done) {
		super();
		this.pending = pending;
		this.active = active;
		this.done = done;
	}
	
	public OrderPoolBase() {
		this(new LinkedHashSet<Order>(), new LinkedHashSet<Order>(),
				new LinkedHashSet<Order>());
	}
	
	/**
	 * Заявка из пула?
	 * <p>
	 * @param order заявка
	 * @return true - заявка пула, false - не из пула
	 */
	public synchronized boolean isPooled(Order order) {
		return isPending(order) || isActive(order) || isDone(order);
	}

	/**
	 * Новая заявка пула?
	 * <p>
	 * @param order заявка
	 * @return true - новая заявка, false - нет или не из пула
	 */
	public synchronized boolean isPending(Order order) {
		return pending.contains(order);
	}

	/**
	 * Активная заявка пула?
	 * <p>
	 * @param order заявка
	 * @return true - активная, false - нет или не из пула
	 */
	public synchronized boolean isActive(Order order) {
		return active.contains(order);
	}

	/**
	 * Заявка пула финализирована?
	 * <p>
	 * @param order заявка
	 * @return true - финализирована, false - нет или не из пула
	 */
	public synchronized boolean isDone(Order order) {
		return done.contains(order);
	}

	/**
	 * Запустить новые заявки в работу.
	 * <p>
	 * @throws OrderException
	 */
	public synchronized void placeOrders() throws OrderException {
		Set<Order> set = new LinkedHashSet<Order>(pending);
		for ( Order order : set ) {
			synchronized ( order ) {
				OrderStatus status = order.getStatus(); 
				if ( status == OrderStatus.PENDING ) {
					order.getTerminal().placeOrder(order);
				}
				active.add(order);
				pending.remove(order);
			}
		}
	}
	
	/**
	 * Снять все активные заявки.
	 */
	public synchronized void cancelOrders() {
		for ( Order order : active ) {
			synchronized ( order ) {
				OrderStatus status = order.getStatus();
				if ( status == OrderStatus.CONDITION
					|| status == OrderStatus.ACTIVE )
				{
					try {
						order.getTerminal().cancelOrder(order);
					} catch ( OrderException e ) {
						logger.error("Error cancel order: ", e);
					}
				}
			}
		}
	}
	
	/**
	 * Добавить заявку в пул.
	 * <p>
	 * @param order заявка
	 * @return возвращает заявку-аргумент
	 */
	public synchronized Order add(Order order) {
		synchronized ( order ) {
			OrderStatus status = order.getStatus();
			if ( status == OrderStatus.PENDING ) {
				order.OnDone().addListener(this);
				pending.add(order);
			} else if ( status == OrderStatus.CONDITION
					|| status == OrderStatus.ACTIVE
					|| status == OrderStatus.SENT )
			{
				order.OnDone().addListener(this);
				active.add(order);
			} else if ( status.isFinal()
					|| status == OrderStatus.CANCEL_SENT )
			{
				done.add(order);
			}
			return order;
		}
	}

	@Override
	public void onEvent(Event event) {
		if ( event instanceof OrderEvent ) {
			OrderEvent e = (OrderEvent) event;
			Order order = e.getOrder();
			if ( event.isType(order.OnDone()) ) {
				order.OnDone().removeListener(this);
				synchronized ( this ) {
					boolean removed = false;
					if ( pending.remove(order) ) removed = true;
					if ( active.remove(order) ) removed = true;
					if ( removed ) done.add(order);
				}
			}
		}
	}
	
	/**
	 * Получить набор новых заявок.
	 * <p>
	 * Служебный, только для тестов.
	 * <p>
	 * @return набор заявок
	 */
	Set<Order> getPendingOrders() {
		return pending;
	}
	
	/**
	 * Получить набор активированных заявок.
	 * <p>
	 * Служебный, только для тестов.
	 * <p>
	 * @return набор заявок
	 */
	Set<Order> getActiveOrders() {
		return active;
	}
	
	/**
	 * Получить набор финализированных заявок.
	 * <p>
	 * Служебный, только для тестов.
	 * <p>
	 * @return набор заявок
	 */
	Set<Order> getDoneOrders() {
		return done;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != OrderPoolBase.class ) {
			return false;
		}
		OrderPoolBase o = (OrderPoolBase) other;
		synchronized  ( this ) {
			return new EqualsBuilder()
				.append(o.active, active)
				.append(o.done, done)
				.append(o.pending, pending)
				.isEquals();
		}
	}

}
