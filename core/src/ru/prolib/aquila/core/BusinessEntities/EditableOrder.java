package ru.prolib.aquila.core.BusinessEntities;

import java.util.Date;


/**
 * Интерфейс модифицируемой заявки.
 * <p>
 * 2012-09-22<br>
 * $Id: EditableOrder.java 542 2013-02-23 04:15:34Z whirlwind $
 */
public interface EditableOrder extends Order, Editable {
	
	/**
	 * Установить идентификатор заявки.
	 * <p>
	 * @param id идентификатор заявки
	 */
	public void setId(Integer id);
	
	/**
	 * Установить направление заявки.
	 * <p>
	 * @param dir направление заявки
	 */
	public void setDirection(Direction dir);
	
	/**
	 * Установить тип заявки.
	 * <p>
	 * @param type тип заявки
	 */
	public void setType(OrderType type);
	
	/**
	 * Установить торговый счет.
	 * <p>
	 * @param account торговый счет
	 */
	public void setAccount(Account account);
	
	/**
	 * Установить дескриптор торгуемого инструмента.
	 * <p>
	 * @param descr дескриптор инструмена
	 */
	public void setSecurityDescriptor(SecurityDescriptor descr);
	
	/**
	 * Установить количество заявки.
	 * <p>
	 * @param qty количество заявки
	 */
	public void setQty(Long qty);
	
	/**
	 * Установить статус заявки.
	 * <p>
	 * @param status новый статус заявки
	 */
	public void setStatus(OrderStatus status);
	
	/**
	 * Установить неисполненный остаток заявки.
	 * <p>
	 * @param qty неисполненный остаток
	 */
	public void setQtyRest(Long qty);
	
	/**
	 * Установить цену заявки.
	 * <p>
	 * @param price цена
	 */
	public void setPrice(Double price);
	
	/**
	 * Установить стоимость исполненной части заявки.
	 * <p>
	 * см. {@link Order#getExecutedVolume()}.
	 * <p>
	 * @param value стоимость
	 */
	public void setExecutedVolume(Double value);
	
	/**
	 * Установить среднюю цену исполненной части заявки.
	 * <p>
	 * см. {@link Order#getAvgExecutedPrice()}.
	 * <p>
	 * @param value средняя цена
	 */
	public void setAvgExecutedPrice(Double value);
	
	/**
	 * Получить предыдущий статус заявки.
	 * <p>
	 * @return статус, актуальный на момент до смены статусы на текущий 
	 */
	public OrderStatus getPreviousStatus();
	
	/**
	 * Установить время выставления заявки.
	 * <p>
	 * @param time время выставления заявки
	 */
	public void setTime(Date time);
	
	/**
	 * Установить время последнего изменения заявки.
	 * <p>
	 * Подразумевается время фиксации финального статуса заявки: снятие,
	 * сведение, частичное сведение.
	 * <p>
	 * @param time время последнего изменения
	 */
	public void setLastChangeTime(Date time);
	
	/**
	 * Добавить сделку по заявке.
	 * <p>
	 * @param trade сделка
	 */
	public void addTrade(Trade trade);
	
	/**
	 * Генерировать событие о новой сделке по заявке.
	 * <p>
	 * @param trade сделка, по которой генерируется событие
	 */
	public void fireTradeEvent(Trade trade);
	
	/**
	 * Удалить всех наблюдателей всех типов событий.
	 * <p>
	 * Данный метод должен вызываться после перевода заявки в одно из финальных
	 * состояний, после которых гарантировано не последует никаких событий.
	 * Данный метод очищает списки наблюдателей всех типов событий и таким
	 * образом разрывает связь между объектами, которая не позволяет сборщику
	 * мусора уничтожить объект, если в нем больше нет необходимости.
	 */
	public void clearAllEventListeners();

}
