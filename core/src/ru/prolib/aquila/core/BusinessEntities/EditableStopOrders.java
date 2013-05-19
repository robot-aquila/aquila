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
	 * @param id номер стоп-заявки
	 * @return экземпляр заявки
	 * @throws OrderNotExistsException
	 */
	public EditableOrder getEditableStopOrder(long id)
			throws OrderNotExistsException;
	
	/**
	 * Зарегистрировать новую стоп-заявку.
	 * <p>
	 * @param id номер стоп-заявки (будет установлен для экземпляра)
	 * @param order экземпляр заявки
	 * @throws OrderAlreadyExistsException
	 */
	public void registerStopOrder(long id, EditableOrder order)
		throws OrderAlreadyExistsException;
	
	/**
	 * Удалить стоп-заявку из набора.
	 * <p>
	 * @param id номер стоп заявки
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
	 * Проверить наличие ожидающих стоп-заявок.
	 * <p>
	 * @return true - существуют ожидающие стоп-заявки
	 */
	public boolean hasPendingStopOrders();
	
	/**
	 * Зарегистрировать ожидающую стоп-заявку.
	 * <p>
	 * @param transId номер транзакции ожидающей стоп-заявки
	 * @param order заявка
	 * @throws OrderAlreadyExistsException
	 */
	public void registerPendingStopOrder(long transId, EditableOrder order)
			throws OrderAlreadyExistsException;
	
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
	 * @return стоп-заявка 
	 * @throws OrderNotExistsException
	 */
	public EditableOrder getPendingStopOrder(long transId)
		throws OrderNotExistsException;
	
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
	 * @return перемещенная стоп-заявка
	 * @throws OrderNotExistsException
	 * @throws OrderAlreadyExistsException
	 */
	public EditableOrder movePendingStopOrder(long transId, long orderId)
			throws OrderException;

	/**
	 * Создать экземпляр стоп-заявки.
	 * <p>
	 * @param terminal терминал
	 * @return экземпляр стоп-заявки
	 */
	public EditableOrder createStopOrder(EditableTerminal terminal);

}
