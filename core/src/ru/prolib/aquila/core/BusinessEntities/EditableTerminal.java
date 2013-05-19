package ru.prolib.aquila.core.BusinessEntities;

import ru.prolib.aquila.core.*;

/**
 * Интерфейс модифицируемого терминала.
 * <p>
 * 2013-01-05<br>
 * $Id: EditableTerminal.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public interface EditableTerminal extends Terminal, EditableOrders,
	EditableStopOrders, EditablePortfolios, EditableSecurities, FirePanicEvent
{
	
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
	public OrderProcessor getOrderProcessorInstance();
	
	/**
	 * Установить экземпляр процессора заявок.
	 * <p>
	 * @param processor процессор заявок
	 */
	public void setOrderProcessorInstance(OrderProcessor processor);
	
	/**
	 * Получить экземпляр хранилища заявок.
	 * <p>
	 * @return хранилище заявок
	 */
	public EditableSecurities getSecuritiesInstance();
	
	/**
	 * Получить экземпляр хранилища портфелей.
	 * <p>
	 * @return хранилище портфелей
	 */
	public EditablePortfolios getPortfoliosInstance();
	
	/**
	 * Получить экземпляр хранилища заявок.
	 * <p>
	 * @return хранилище заявок
	 */
	public EditableOrders getOrdersInstance();
	
	/**
	 * Получить экземпляр хранилища стоп-заявок.
	 * <p>
	 * @return хранилище стоп-заявок
	 */
	public EditableOrders getStopOrdersInstance();
	
	/**
	 * Получить стартер терминала.
	 * <p>
	 * @return стартер
	 */
	public Starter getStarter();

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
	 * Создать и зарегистрировать новый инструмент терминала.
	 * <p>
	 * @param descr дескриптор нового инструмента
	 * @return новый инструмент
	 * @throws SecurityAlreadyExistsException инструмент уже существует
	 */
	public EditableSecurity createSecurity(SecurityDescriptor descr)
		throws SecurityAlreadyExistsException;
	
	/**
	 * Создать и зарегистрировать новый портфель.
	 * <p>
	 * @param account счет
	 * @return новый портфель
	 * @throws PortfolioAlreadyExistsException портфель уже существует
	 */
	public EditablePortfolio createPortfolio(Account account)
		throws PortfolioException;
	
	/**
	 * Создать заявку.
	 * <p>
	 * @return новая заявка
	 */
	public EditableOrder createOrder();
	
	/**
	 * Создать стоп-заявку.
	 * <p>
	 * @return новая заявка
	 */
	public EditableOrder createStopOrder();

}
