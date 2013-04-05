package ru.prolib.aquila.core.BusinessEntities;

/**
 * Интерфейс редактируемого набора стоп-заявок.
 * <p>
 * 2013-01-05<br>
 * $Id: EditableStopOrders.java 490 2013-02-05 19:42:02Z whirlwind $
 */
public interface EditableStopOrders extends StopOrders {
	
	/**
	 * Генерировать событие о появлении информации о новой стоп-заявке.
	 * <p>
	 * @param order заявка
	 */
	public void fireStopOrderAvailableEvent(Order order);
	
	/**
	 * Получить экземпляр редактируемой стоп-заявки.
	 * <p>
	 * @param id идентификатор стоп-заявки
	 * @return редактируемая заявка или null, если нет заявки с таким id
	 */
	public EditableOrder getEditableStopOrder(long id)
			throws OrderException;
	
	/**
	 * Зарегистрировать новую стоп-заявку.
	 * <p>
	 * @param order новая заявка
	 * @throws OrderAlreadyExistsException
	 */
	public void registerStopOrder(EditableOrder order)
			throws OrderException;
	
	/**
	 * Удалить стоп-заявку из набора.
	 * <p>
	 * @param order стоп-заявка
	 */
	public void purgeStopOrder(EditableOrder order);
	
	/**
	 * Удалить стоп-заявку из набора.
	 * <p>
	 * @param id идентификатор стоп-заявки
	 */
	public void purgeStopOrder(long id);
	
	/**
	 * Проверить ожидающую стоп-заявку.
	 * <p>
	 * @param transId идентификатор транзакции
	 * @return true - если существует ожидающая стоп-заявка
	 */
	public boolean isPendingStopOrder(long transId);
	
	/**
	 * Зарегистрировать ожидающую стоп-заявку.
	 * <p>
	 * @param order заявка
	 * @throws OrderAlreadyExistsException
	 */
	public void registerPendingStopOrder(EditableOrder order)
			throws OrderException;
	
	/**
	 * Удалить стоп-заявку из очереди ожидания.
	 * <p>
	 * @param order стоп-заявка
	 */
	public void purgePendingStopOrder(EditableOrder order);
	
	/**
	 * Удалить стоп-заявку из очереди ожидания.
	 * <p>
	 * @param transId идентификатор транзакции
	 */
	public void purgePendingStopOrder(long transId);
	
	/**
	 * Получить экземпляр ожидающей стоп-заявки.
	 * <p>
	 * @param transId идентификатор транзакции
	 * @return заявка или null, если нет заявки с указанным номером транзакции
	 */
	public EditableOrder getPendingStopOrder(long transId);
	
	/**
	 * Перевести стоп-заявку из списка ожидаемых в список зарегистрированных.
	 * <p>
	 * Выполняется, только если в хранилище есть ожидающая стоп-заявка с
	 * указанным номером транзакции. Номер заявки устанавливается для заявки,
	 * затем стоп-заявка удаляется из списка ожидающих и помещается в список
	 * зарегистрированных. Событие о появлении новой стоп-заявки <b>не
	 * генерируется</b>. 
	 * <p>
	 * @param transId номер транзакции ожидающей стоп-заявки
	 * @param orderId номер стоп-заявки, который следует использовать для
	 * регистрации
	 * @return перемещенная стоп-заявка или null, если стоп-заявки с указанным
	 * номером транзакции нет в списке ожидания
	 */
	public EditableOrder
		makePendingStopOrderAsRegisteredIfExists(long transId, long orderId)
			throws OrderException;


}
