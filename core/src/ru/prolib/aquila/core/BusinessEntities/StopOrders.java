package ru.prolib.aquila.core.BusinessEntities;

import java.util.List;

import ru.prolib.aquila.core.EventType;

/**
 * Интерфейс хранилища стоп-заявок.
 * <p>
 * 2012-10-18<br>
 * $Id: StopOrders.java 552 2013-03-01 13:35:35Z whirlwind $
 */
public interface StopOrders {
	
	/**
	 * Проверить наличие стоп-заявки.
	 * <p>
	 * @param id идентификатор стоп-заявки
	 * @return true - есть стоп-заявка с таким идентификатором
	 */
	public boolean isStopOrderExists(long id);
	
	/**
	 * Получить список стоп-заявок.
	 * <p>
	 * @return список стоп-заявок
	 */
	public List<Order> getStopOrders();
	
	/**
	 * Получить стоп-заявку по идентификатору.
	 * <p>
	 * @param id идентификатор стоп-заявки
	 * @return стоп-заявка
	 * @throws OrderNotExistsException
	 */
	public Order getStopOrder(long id) throws OrderException;
	
	/**
	 * Получить количество стоп-заявок.
	 * <p>
	 * @return количество заявок
	 */
	public int getStopOrdersCount();
	
	/**
	 * Получить тип события: при поступлении информации о новой стоп-заявке.
	 * <p>
	 * @return тип события
	 */
	public EventType OnStopOrderAvailable();
	
	/**
	 * Перехватчик событий соответствующего типа от всех стоп-заявок.
	 * <p>
	 * @return тип события
	 */
	public EventType OnStopOrderChanged();
	
	/**
	 * Перехватчик событий соответствующего типа от всех стоп-заявок.
	 * <p>
	 * @return тип события
	 */
	public EventType OnStopOrderCancelFailed();
	
	/**
	 * Перехватчик событий соответствующего типа от всех стоп-заявок.
	 * <p>
	 * @return тип события
	 */
	public EventType OnStopOrderCancelled();
	
	/**
	 * Перехватчик событий соответствующего типа от всех стоп-заявок.
	 * <p>
	 * @return тип события
	 */
	public EventType OnStopOrderDone();
	
	/**
	 * Перехватчик событий соответствующего типа от всех заявок.
	 * <p>
	 * @return тип события
	 */
	public EventType OnStopOrderFilled();
	
	/**
	 * Перехватчик событий соответствующего типа от всех стоп-заявок.
	 * <p>
	 * @return тип события
	 */
	public EventType OnStopOrderFailed();
	
	/**
	 * Перехватчик событий соответствующего типа от всех стоп-заявок.
	 * <p>
	 * @return тип события
	 */
	public EventType OnStopOrderRegistered();
	
	/**
	 * Перехватчик событий соответствующего типа от всех стоп-заявок.
	 * <p>
	 * @return тип события
	 */
	public EventType OnStopOrderRegisterFailed();


}
