package ru.prolib.aquila.core.BusinessEntities;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.utils.Counter;

/**
 * Интерфейс модифицируемого терминала.
 * <p>
 * 2013-01-05<br>
 * $Id: EditableTerminal.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public interface EditableTerminal extends Terminal, EditableOrders,
	EditablePortfolios, EditableSecurities, FirePanicEvent
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

}
