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
	 * Создать экземпляр заявки.
	 * <p>
	 * @param terminal терминал
	 * @return экземпляр заявки
	 */
	public EditableOrder createOrder(EditableTerminal terminal);

}
