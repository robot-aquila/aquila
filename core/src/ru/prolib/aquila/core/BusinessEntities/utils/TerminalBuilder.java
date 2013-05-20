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
	 * <b>Внимание</b>: созданный терминал не содержик определенного процессора
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
		EditableTerminal terminal = new TerminalImpl(es, starter,
				createSecurities(es), createPortfolios(es),
				createOrders(es, "Orders"), createOrders(es, "StopOrders"),
				d, d.createType("OnConnected"), d.createType("OnDisconnected"),
				d.createType("OnStarted"), d.createType("OnStopped"),
				d.createType("OnPanic"));
		return terminal;
	}
	
	private EditableSecurities createSecurities(EventSystem es) {
		EventDispatcher d = es.createEventDispatcher("Securities");
		return new SecuritiesImpl(d,
				d.createType("OnAvailable"),
				d.createType("OnChanged"),
				d.createType("OnTrade"));
	}
	
	private EditablePortfolios createPortfolios(EventSystem es) {
		EventDispatcher d = es.createEventDispatcher("Portfolios");
		return new PortfoliosImpl(d,
				d.createType("OnAvailable"),
				d.createType("OnChanged"),
				d.createType("OnPositionAvailable"),
				d.createType("OnPositionChanged"));
	}
	
	private EditableOrders createOrders(EventSystem es, String dispatcherId) {
		EventDispatcher d = es.createEventDispatcher(dispatcherId);
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
				d.createType("OnRegisterFailed"));
	}

}
