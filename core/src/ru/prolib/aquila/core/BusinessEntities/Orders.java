package ru.prolib.aquila.core.BusinessEntities;

import java.util.List;

import ru.prolib.aquila.core.EventType;

/**
 * Интерфейс хранилища заявок.
 * <p>
 * 2012-10-16<br>
 * $Id: Orders.java 490 2013-02-05 19:42:02Z whirlwind $
 */
public interface Orders {

	/**
	 * Проверить наличие заявки.
	 * <p>
	 * @param id идентификатор заявки
	 * @return true - есть заявка с таким идентификатором
	 */
	public boolean isOrderExists(long id);
	
	/**
	 * Получить список заявок.
	 * <p>
	 * @return список заявок
	 */
	public List<Order> getOrders();
	
	/**
	 * Получить количество заявок.
	 * <p>
	 * @return количество заявок
	 */
	public int getOrdersCount();
	
	/**
	 * Получить заявку по идентификатору.
	 * <p>
	 * @param id идентификатор заявки
	 * @return заявка
	 * @throws OrderNotExistsException
	 */
	public Order getOrder(long id) throws OrderException;
	
	/**
	 * Получить тип события: при поступлении информации о новой заявке.
	 * <p>
	 * @return тип события
	 */
	public EventType OnOrderAvailable();
	
	/**
	 * Перехватчик событий соответствующего типа от всех заявок.
	 * <p>
	 * @return тип события
	 */
	public EventType OnOrderCancelFailed();
	
	/**
	 * Перехватчик событий соответствующего типа от всех заявок.
	 * <p>
	 * @return тип события
	 */
	public EventType OnOrderCancelled();
	
	/**
	 * Перехватчик событий соответствующего типа от всех заявок.
	 * <p>
	 * @return тип события
	 */
	public EventType OnOrderChanged();
	
	/**
	 * Перехватчик событий соответствующего типа от всех заявок.
	 * <p>
	 * @return тип события
	 */
	public EventType OnOrderDone();
	
	/**
	 * Перехватчик событий соответствующего типа от всех заявок.
	 * <p>
	 * @return тип события
	 */
	public EventType OnOrderFailed();
	
	/**
	 * Перехватчик событий соответствующего типа от всех заявок.
	 * <p>
	 * @return тип события
	 */
	public EventType OnOrderFilled();
	
	/**
	 * Перехватчик событий соответствующего типа от всех заявок.
	 * <p>
	 * @return тип события
	 */
	public EventType OnOrderPartiallyFilled();
	
	/**
	 * Перехватчик событий соответствующего типа от всех заявок.
	 * <p>
	 * @return тип события
	 */
	public EventType OnOrderRegistered();
	
	/**
	 * Перехватчик событий соответствующего типа от всех заявок.
	 * <p>
	 * @return тип события
	 */
	public EventType OnOrderRegisterFailed();
	
}
