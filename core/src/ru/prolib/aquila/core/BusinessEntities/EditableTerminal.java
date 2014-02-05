package ru.prolib.aquila.core.BusinessEntities;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.utils.Counter;

/**
 * Интерфейс модифицируемого терминала.
 * <p>
 * 2013-01-05<br>
 * $Id: EditableTerminal.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public interface EditableTerminal extends Terminal {
	
	/**
	 * Получить фасад событийной системы.
	 * <p>
	 * @return фасад системы событий
	 */
	public EventSystem getEventSystem();
	
	/**
	 * Генерировать событие о подключении терминала.
	 */
	public void fireTerminalConnectedEvent();
	
	/**
	 * Генерировать событие об отключении терминала.
	 */
	public void fireTerminalDisconnectedEvent();
	
	/**
	 * Получить экземпляр процессора заявок.
	 * <p>
	 * @return процессор заявок
	 */
	public OrderProcessor getOrderProcessor();
	
	/**
	 * Установить экземпляр процессора заявок.
	 * <p>
	 * @param processor процессор заявок
	 */
	public void setOrderProcessor(OrderProcessor processor);
	
	/**
	 * Получить стартер терминала.
	 * <p>
	 * @return стартер
	 */
	public StarterQueue getStarter();

	/**
	 * Генерировать событие о старте терминала.
	 */
	public void fireTerminalStartedEvent();

	/**
	 * Генерировать событие об останове терминала.
	 */
	public void fireTerminalStoppedEvent();
	
	/**
	 * Установить состояние терминала.
	 * <p>
	 * @param state новое состояние
	 */
	public void setTerminalState(TerminalState state);
	
	/**
	 * Получить инструмент.
	 * <p>
	 * Если инструмент не существует, то он будет создан.
	 * <p>
	 * @param descr дескриптор нового инструмента
	 * @return инструмент
	 */
	public EditableSecurity getEditableSecurity(SecurityDescriptor descr);
	
	/**
	 * Получить портфель.
	 * <p>
	 * Если портфель не существует, то он будет создан.
	 * <p>
	 * @param account счет
	 * @return портфель
	 */
	public EditablePortfolio getEditablePortfolio(Account account);
	
	/**
	 * Создать заявку.
	 * <p>
	 * @return новая заявка
	 */
	public EditableOrder createOrder();
	
	/**
	 * Получить нумератор заявок.
	 * <p>
	 * Номер заявки - это суррогатный ключ, однозначно идентифицирующий
	 * экземпляр заявки в рамках экземпляра терминала. При создании заявки
	 * с помощью {@link #createOrder(Account, Direction, Security, long)} или
	 * других аналогичных методов, ей автоматически присваивается очередной
	 * номер. Номер заявке <b>НЕ НАЗНАЧАЕТСЯ</b> при создании экземпляра через
	 * вызовы {@link #createOrder()} или {@link #createOrder(EditableTerminal)}.
 	 * <p>
	 * Непрерывность номеров заявок не имеет никакого практического значения для
	 * терминала. По этому данный нумератор может быть использован конкретной
	 * реализацией терминала для решения своих специфических задач. Например,
	 * для сквозной нумерации всех запросов в удаленную систему. Единственное
	 * требование - это увеличение значение счетчика в процессе работы, что бы
	 * избежать дублирования номеров для разных экземпляров заявок.
	 * <p>
	 * В любой момент времени этот счетчик указывает на последний использованный
	 * номер. Что бы получить следующий номер необходимо выполнить атомарный
	 * инкремент с возвратом через {@link Counter#incrementAndGet()}. Для
	 * установки последнего использованного значения можно использовать
	 * {@link Counter#set(int)}. В целях избежания дубликатов номеров важно
	 * гарантировать, что устанавливаемое значение больше текущего.
	 * <p>
	 * @return нумератор заявок
	 */
	public Counter getOrderNumerator();
	
	/**
	 * Генерировать событие о недоступности инструмента.
	 * <p>
	 * Служебный метод, генерирующий событие об отрицательном результате
	 * на запрос инструмента, который был выполнен посредством вызова метода
	 * {@link #requestSecurity(SecurityDescriptor)}.
	 * <p>
	 * @param descr дескриптор инструмента
	 * @param errorCode код ошибки
	 * @param errorMsg текст ошибки
	 */
	public void fireSecurityRequestError(SecurityDescriptor descr,
			int errorCode, String errorMsg);
	
	/**
	 * Получить экземпляр инструмента.
	 * <p>
	 * Если инструмент не существует, создает инструмент и добавляет его в
	 * набор. В результате вызова никаких событий не генерируется.
	 * <p>
	 * @param terminal терминал
	 * @param descr дескриптор инструмента
	 * @return инструмент
	 */
	public EditableSecurity getEditableSecurity(EditableTerminal terminal,
			SecurityDescriptor descr);
	
	/**
	 * Генерировать события инструмента.
	 * <p>
	 * @param security инструмент
	 */
	public void fireEvents(EditableSecurity security);
	

	/**
	 * Генерировать событие о паническом состоянии.
	 * <p>
	 * @param code код ситуации
	 * @param msgId идентификатор сообщения
	 */
	public void firePanicEvent(int code, String msgId);

	/**
	 * Генерировать событие о паническом состоянии.
	 * <p>
	 * Данный метод используется для описания состояний, характеризующемся
	 * дополнительными аргументами. Как правило, идентификатор сообщения
	 * указывает на строку с плейсхолдерами, а массив аргументов содержит
	 * значения для подстановки. 
	 * <p>
	 * @param code код ситуации
	 * @param msgId идентификатор сообщения
	 * @param args аргументы, описывающие ситуацию
	 */
	public void firePanicEvent(int code, String msgId, Object[] args);
	
	
	/**
	 * Генерировать события заявки.
	 * <p>
	 * @param order заявка
	 */
	public void fireEvents(EditableOrder order);
	
	/**
	 * Получить экземпляр редактируемой заявки.
	 * <p>
	 * @param id идентификатор заявки
	 * @return заявка
	 * @throws OrderNotExistsException
	 */
	public EditableOrder getEditableOrder(int id)
		throws OrderNotExistsException;
	
	/**
	 * Зарегистрировать новую заявку.
	 * <p>
	 * @param id номер заявки (будет установлен для экземпляра)
	 * @param order заявка
	 * @throws OrderAlreadyExistsException
	 */
	public void registerOrder(int id, EditableOrder order)
		throws OrderAlreadyExistsException;
	
	/**
	 * Удалить заявку из набора.
	 * <p>
	 * Если заявки с указанным номером нет в реестре, то ничего не происходит.
	 * <p>
	 * @param id идентификатор заявки
	 */
	public void purgeOrder(int id);
	
	/**
	 * Создать экземпляр заявки.
	 * <p>
	 * @param terminal терминал
	 * @return экземпляр заявки
	 */
	public EditableOrder createOrder(EditableTerminal terminal);

	
	/**
	 * Генерировать события портфеля.
	 * <p>
	 * @param portfolio портфель
	 */
	public void fireEvents(EditablePortfolio portfolio);
	
	/**
	 * Получить экземпляр редактируемого портфеля.
	 * <p>
	 * Если портфель не существует, то он будет создан.
	 * <p>
	 * @param terminal терминал
	 * @param account идентификатор счета
	 * @return портфель
	 */
	public EditablePortfolio
		getEditablePortfolio(EditableTerminal terminal, Account account);
	
	/**
	 * Установить портфель по-умолчанию.
	 * <p>
	 * @param portfolio экземпляр портфеля
	 */
	public void setDefaultPortfolio(EditablePortfolio portfolio);

}
