package ru.prolib.aquila.core.BusinessEntities;

import ru.prolib.aquila.core.*;

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
	 * Перевести терминал в режим "Подключен".
	 * <p>
	 * Этот метод меняет статус терминала, только если терминал находится
	 * в состоянии {@link TerminalState#STARTED}. При этом генерируются
	 * соответствующие события о смене состояния терминала. Если на момент
	 * вызова терминал не находится в состоянии {@link TerminalState#STARTED},
	 * то вызов игнорируется, а в журнал направляется предупреждающее сообщение. 
	 */
	public void markTerminalConnected();
	
	/**
	 * Перевести терминал в режим "Отключен".
	 * <p>
	 * Этот метод меняют статус терминала и генерирует соответствующие события,
	 * только если терминал находится в состоянии
	 * {@link TerminalState#CONNECTED} или {@link TerminalState#STOPPING}. При
	 * этом, статус меняется на {@link TerminalState#STARTED}, только в случае,
	 * на момент вызова статус был {@link TerminalState#CONNECTED}. Если
	 * терминал находился в процессе останова, то ожидается, что статус будет
	 * сменен по завершении этой процедуры. Если на момент вызова терминал
	 * не находится в одном из подходящих состояний, то вызов игнорируется, а в
	 * журнал направляется соответствующее предупреждение.
	 */
	public void markTerminalDisconnected();
	
	/**
	 * Генерировать событие о подключении терминала.
	 * <p>
	 * Используй {@link #markTerminalConnected()} вместо вызова этого метода.
	 */
	@Deprecated
	public void fireTerminalConnectedEvent();
	
	/**
	 * Генерировать событие об отключении терминала.
	 * <p>
	 * Используй {@link #markTerminalDisconnected()} вместо вызова этого метода.
	 */
	@Deprecated
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
	@Deprecated // TODO: use injection by terminal builder
	public void setOrderProcessor(OrderProcessor processor);
	
	/**
	 * Установить стартер терминала.
	 * <p>
	 * @param starter стартер
	 */
	@Deprecated // TODO: use injection by terminal builder
	public void setStarter(StarterQueue starter);
	
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
	 * Генерировать событие о готовности терминала.
	 */
	public void fireTerminalReady();
	
	/**
	 * Генерировать событие о неготовности терминала.
	 */
	public void fireTerminalUnready();
	
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
	 * @throws OrderNotExistsException - TODO:
	 */
	public EditableOrder getEditableOrder(int id)
		throws OrderNotExistsException;
	
	/**
	 * Удалить заявку из набора.
	 * <p>
	 * Если заявки с указанным номером нет в реестре, то ничего не происходит.
	 * <p>
	 * @param id идентификатор заявки
	 */
	public void purgeOrder(int id);
	
	/**
	 * Генерировать события портфеля.
	 * <p>
	 * @param portfolio портфель
	 */
	public void fireEvents(EditablePortfolio portfolio);
	

	/**
	 * Установить портфель по-умолчанию.
	 * <p>
	 * @param portfolio экземпляр портфеля
	 */
	public void setDefaultPortfolio(EditablePortfolio portfolio);
	
}
