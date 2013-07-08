package ru.prolib.aquila.core.BusinessEntities.utils;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Конструктор терминала.
 * <p>
 * Класс реализует конструирование терминала с базовой структурой.
 * Создает все необходимые подчиненные объекты по-умолчанию.
 */
public class TerminalBuilder {
	
	/**
	 * Конструктор.
	 * <p>
	 */
	public TerminalBuilder() {
		super();
	}
	
	/**
	 * Создать терминал с базовой структурой.
	 * <p>
	 * Создает фасад событийной подсистемы класса
	 * {@link ru.prolib.aquila.core.EventSystemImpl EventSystemImpl} с очередью
	 * событий с указанным идентификатором. В качестве пускача использует
	 * экземпляр класса {@link ru.prolib.aquila.core.StarterQueue StarterQueue},
	 * в начало которого добавляет ранее созданную очередь событий. 
	 * Все созданные объекты доступны через служебный интерфейс терминала.
	 * <b>Внимание</b>: созданный терминал не содержит определенного процессора
	 * заявок. Специфический процессор заявок должен быть установлен явно.
	 * <p>
	 * @param queueId идентификатор очереди событий
	 * @return экземпляр терминала
	 */
	public EditableTerminal createTerminal(String queueId) {
		EventSystem es = new EventSystemImpl(new EventQueueImpl(queueId));
		StarterQueue starter = new StarterQueue();
		starter.add(es.getEventQueue());
		EventDispatcher d = es.createEventDispatcher("Terminal");
		return createTerminalInstance(es, starter,
				createSecurities(es), createPortfolios(es), createOrders(es), 
				d, d.createType("OnConnected"), d.createType("OnDisconnected"),
				d.createType("OnStarted"), d.createType("OnStopped"),
				d.createType("OnPanic"),
				d.createType("OnRequestSecurityError"));
	}
	
	protected EditableSecurities createSecurities(EventSystem es) {
		EventDispatcher d = es.createEventDispatcher("Securities");
		return new SecuritiesImpl(d,
				d.createType("OnAvailable"),
				d.createType("OnChanged"),
				d.createType("OnTrade"));
	}
	
	protected EditablePortfolios createPortfolios(EventSystem es) {
		EventDispatcher d = es.createEventDispatcher("Portfolios");
		return new PortfoliosImpl(d,
				d.createType("OnAvailable"),
				d.createType("OnChanged"),
				d.createType("OnPositionAvailable"),
				d.createType("OnPositionChanged"));
	}
	
	protected EditableOrders createOrders(EventSystem es) {
		EventDispatcher d = es.createEventDispatcher("Orders");
		return new OrdersImpl(d,
				d.createType("OnAvailable"),
				d.createType("OnCancelFailed"),
				d.createType("OnCancelled"),
				d.createType("OnChanged"),
				d.createType("OnDone"),
				d.createType("OnFailed"),
				d.createType("OnFilled"),
				d.createType("OnPartiallyFilled"),
				d.createType("OnRegistered"),
				d.createType("OnRegisterFailed"),
				d.createType("OnTrade"));
	}
	
	/**
	 * Создать экземпляр терминала.
	 * <p>
	 * Метод предназначен для переопределения в наследниках. Сигнатура метода
	 * повторяет сигнатуру конструктора базового терминала {@link TerminalImpl}.
	 * <p>
	 * @param es фасад событийной системы
	 * @param starter последовательность процедур запуска 
	 * @param securities набор инструментов
	 * @param portfolios набор портфелей
	 * @param orders набор заявок
	 * @param dispatcher диспетчер событий
	 * @param onConnected тип события: при подключении терминала
	 * @param onDisconnected тип события: при отключении терминала
	 * @param onStarted тип события: при запуске терминала
	 * @param onStopped тип события: при останове терминала
	 * @param onPanic тип события: критическое состояние
	 * @param onReqSecurityError тип события: на запрос инструмента
	 * @return экземпляр терминала
	 */
	protected EditableTerminal createTerminalInstance(EventSystem es,
			StarterQueue starter, EditableSecurities securities,
			EditablePortfolios portfolios, EditableOrders orders,
			EventDispatcher dispatcher,
			EventType onConnected, EventType onDisconnected,
			EventType onStarted, EventType onStopped, EventType onPanic,
			EventType onReqSecurityError)
	{
		return new TerminalImpl(es, starter, securities, portfolios,
				orders, dispatcher, onConnected, onDisconnected,
				onStarted, onStopped, onPanic, onReqSecurityError);
	}

}
