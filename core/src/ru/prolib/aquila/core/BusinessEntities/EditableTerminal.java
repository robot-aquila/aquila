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
	 * Получить экземпляр конструктора заявок.
	 * <p>
	 * @return конструктор заявок
	 */
	public OrderBuilder getOrderBuilderInstance();
	
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

}
