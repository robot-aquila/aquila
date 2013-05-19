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
	 * @return заявка
	 * @throws OrderNotExistsException
	 */
	public EditableOrder getEditableOrder(long id)
		throws OrderNotExistsException;
	
	/**
	 * Зарегистрировать новую заявку.
	 * <p>
	 * @param id номер заявки (будет установлен для экземпляра)
	 * @param order заявка
	 * @throws OrderAlreadyExistsException
	 */
	public void registerOrder(long id, EditableOrder order)
		throws OrderAlreadyExistsException;
	
	/**
	 * Удалить заявку из набора.
	 * <p>
	 * Если заявки с указанным номером нет в реестре, то ничего не происходит.
	 * <p>
	 * @param id идентификатор заявки
	 */
	public void purgeOrder(long id);
	
	/**
	 * Проверить ожидающую заявку.
	 * <p>
	 * @param transId идентификатор транзакции
	 * @return true - если заявка с указанным номером транзакции в ожидании,
	 * false - если нет соответствующей заявки
	 */
	public boolean isPendingOrder(long transId);
	
	/**
	 * Проверить наличие ожидающих заявок.
	 * <p>
	 * @return true - если есть ожидающие заявки, false - нет ожидающих заявок
	 */
	public boolean hasPendingOrders();
	
	/**
	 * Зарегистрировать ожидающую заявку.
	 * <p>
	 * @param transId номер транзакции
	 * @param order заявка
	 * @throws OrderAlreadyExistsException
	 */
	public void registerPendingOrder(long transId, EditableOrder order)
		throws OrderAlreadyExistsException;
	
	/**
	 * Удалить заявку из очереди ожидания.
	 * <p>
	 * Если заявки с указанным номером транзакции нет в реестре, то ничего
	 * не происходит.
	 * <p>
	 * @param transId идентификатор транзакции
	 */
	public void purgePendingOrder(long transId);
	
	/**
	 * Получить экземпляр ожидающей заявки.
	 * <p>
	 * @param transId идентификатор транзакции
	 * @return заявка
	 * @throws OrderNotExistsException
	 */
	public EditableOrder getPendingOrder(long transId)
		throws OrderNotExistsException;
	
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
	 * @return перемещенная заявка
	 * @throws OrderNotExistsException
	 * @throws OrderAlreadyExistsException
	 */
	public EditableOrder movePendingOrder(long transId, long orderId)
	 	throws OrderException;
	
	/**
	 * Создать экземпляр заявки.
	 * <p>
	 * @param terminal терминал
	 * @return экземпляр заявки
	 */
	public EditableOrder createOrder(EditableTerminal terminal);

}
