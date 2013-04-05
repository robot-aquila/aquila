package ru.prolib.aquila.core.BusinessEntities;

/**
 * Интерфейс редактируемого хранилища заявок.
 * <p>
 * 2012-10-16<br>
 * $Id: EditableOrders.java 490 2013-02-05 19:42:02Z whirlwind $
 */
public interface EditableOrders extends Orders {
	
	/**
	 * Генерировать событие о появлении информации о новой заявке.
	 * <p>
	 * @param order заявка
	 */
	public void fireOrderAvailableEvent(Order order);
	
	/**
	 * Получить экземпляр редактируемой заявки.
	 * <p>
	 * @param id идентификатор заявки
	 * @return редактируемая заявка или null, если нет заявки с таким id
	 */
	public EditableOrder getEditableOrder(long id) throws OrderException;
	
	/**
	 * Зарегистрировать новую заявку.
	 * <p>
	 * @param order новая заявка
	 * @throws OrderAlreadyExistsException
	 */
	public void registerOrder(EditableOrder order) throws OrderException;
	
	/**
	 * Удалить заявку из набора.
	 * <p>
	 * @param order заявка
	 */
	public void purgeOrder(EditableOrder order);
	
	/**
	 * Удалить заявку из набора.
	 * <p>
	 * @param id идентификатор заявки
	 */
	public void purgeOrder(long id);
	
	/**
	 * Проверить ожидающую заявку.
	 * <p>
	 * @param transId идентификатор транзакции
	 * @return true - если существует ожидающая заявка
	 */
	public boolean isPendingOrder(long transId);
	
	/**
	 * Зарегистрировать ожидающую заявку.
	 * <p>
	 * @param order заявка
	 * @throws OrderAlreadyExistsException
	 */
	public void registerPendingOrder(EditableOrder order) throws OrderException;
	
	/**
	 * Удалить заявку из очереди ожидания.
	 * <p>
	 * @param order заявка
	 */
	public void purgePendingOrder(EditableOrder order);
	
	/**
	 * Удалить заявку из очереди ожидания.
	 * <p>
	 * @param transId идентификатор транзакции
	 */
	public void purgePendingOrder(long transId);
	
	/**
	 * Получить экземпляр ожидающей заявки.
	 * <p>
	 * @param transId идентификатор транзакции
	 * @return заявка или null, если нет заявки с указанным номером транзакции
	 */
	public EditableOrder getPendingOrder(long transId);
	
	/**
	 * Перевести заявку из списка ожидаемых в список зарегистрированных.
	 * <p>
	 * Выполняется, только если в хранилище есть ожидающая заявка с указанным
	 * номером транзакции. Номер заявки устанавливается для заявки, затем заявка
	 * удаляется из списка ожидающих и помещается в список зарегистрированных.
	 * Событие о появлении новой заявки <b>не генерируется</b>. 
	 * <p>
	 * @param transId номер транзакции ожидающей заявки
	 * @param orderId номер заявки, который следует использовать для регистрации
	 * @return перемещенная заявка или null, если заявки с указанным номером
	 * транзакции нет в списке ожидания
	 */
	public EditableOrder
			makePendingOrderAsRegisteredIfExists(long transId, long orderId)
	 				throws OrderException;

}
