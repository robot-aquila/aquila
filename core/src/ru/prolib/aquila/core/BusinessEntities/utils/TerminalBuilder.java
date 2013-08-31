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
		starter.add(new EventQueueStarter(es.getEventQueue(), 30000));
		return createTerminalInstance(es, starter,
				createSecurities(es), createPortfolios(es), createOrders(es), 
				new TerminalEventDispatcher(es));
	}
	
	protected EditableSecurities createSecurities(EventSystem es) {
		return new SecuritiesImpl(new SecuritiesEventDispatcher(es));
	}
	
	protected EditablePortfolios createPortfolios(EventSystem es) {
		return new PortfoliosImpl(new PortfoliosEventDispatcher(es));
	}
	
	protected EditableOrders createOrders(EventSystem es) {
		return new OrdersImpl(new OrdersEventDispatcher(es));
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
	 * @return экземпляр терминала
	 */
	protected EditableTerminal createTerminalInstance(EventSystem es,
			StarterQueue starter, EditableSecurities securities,
			EditablePortfolios portfolios, EditableOrders orders,
			TerminalEventDispatcher dispatcher)
	{
		return new TerminalImpl(es, starter, securities, portfolios,
				orders, dispatcher);
	}

}
