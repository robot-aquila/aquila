package ru.prolib.aquila.core.report.trades;

import java.util.*;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.report.RTrade;
import ru.prolib.aquila.core.report.TradeReportEvent;

/**
 * Отчет по активным трейдам.
 * <p>
 * Данный отчет оперирует только открытыми на текущий момент трейдами и не
 * сохраняет никакой истории.
 */
public class ActiveTrades {
	private Map<SecurityDescriptor, ERTrade> reports;
	private final EventDispatcher dispatcher;
	private final EventType onEnter;
	private final EventType onExit;
	private final EventType onChanged;
	
	/**
	 * Публичный конструктор.
	 */
	public ActiveTrades() {
		this(new EventDispatcherImpl(new SimpleEventQueue(), "ActiveTrades"));
	}
	
	/**
	 * Вспомогательный служебный конструктор.
	 * <p>
	 * @param dispatcher диспетчер событий
	 */
	private ActiveTrades(EventDispatcher dispatcher) {
		this(dispatcher, dispatcher.createType("Enter"),
			dispatcher.createType("Exit"), dispatcher.createType("Changed"));
	}
	
	/**
	 * Конструктор (для тестов).
	 * <p>
	 * @param dispatcher диспетчер событий
	 * @param onEnter тип события: при входе в трейд
	 * @param onExit тип события: при выходе из трейда
	 * @param onChanged тип события: при изменении трейда
	 */
	ActiveTrades(EventDispatcher dispatcher, EventType onEnter, 
			EventType onExit, EventType onChanged) 
	{
		this.dispatcher = dispatcher;
		this.onEnter = onEnter;
		this.onExit = onExit;
		this.onChanged = onChanged;
		reports = new HashMap<SecurityDescriptor, ERTrade>();
	}
	
	/**
	 * Получить диспетчер событий.
	 * <p>
	 * @return диспетчер событий
	 */
	public EventDispatcher getEventDispatcher() {
		return dispatcher;
	}
	
	/**
	 * Получить тип события: при открытии нового трейда.
	 * <p>
	 * @return тип события
	 */
	public EventType OnEnter() {
		return onEnter;
	}
	
	/**
	 * Получить тип события: при закрытии трейда.
	 * <p>
	 * @return тип события
	 */
	public EventType OnExit() {
		return onExit;
	}
	
	/**
	 * Получить тип события: при изменении трейда.
	 * <p>
	 * @return тип события
	 */
	public EventType OnChanged() {
		return onChanged;
	}
	
	/**
	 * Добавить сделку в отчет.
	 * <p>
	 * @param trade сделка
	 */
	public synchronized void addTrade(Trade trade) {
		SecurityDescriptor descr = trade.getSecurityDescriptor();
		ERTrade report = reports.get(descr);
		if ( report == null ) {
			report = new RTradeImpl(trade);
			reports.put(descr, report);
			dispatcher.dispatch(new TradeReportEvent(onEnter, report));
		} else {
			ERTrade next = report.addTrade(trade);
			if ( ! report.isOpen() ) {
				dispatcher.dispatch(new TradeReportEvent(onExit, report));
				if ( next != null ) {
					dispatcher.dispatch(new TradeReportEvent(onEnter, next));
					reports.put(descr, next);
				} else {				
					reports.remove(descr);
				}
			} else {
				dispatcher.dispatch(new TradeReportEvent(onChanged, report));
			}
		}
	}
	
	/**
	 * Получить текущий трейд по сделке.
	 * <p>
	 * @param descr дескриптор инструмента
	 * @return трейд или null, если трейд по инструменту не открыт
	 */
	public synchronized RTrade getReport(SecurityDescriptor descr) {
		return reports.get(descr);
	}
	
	/**
	 * Получить список текущих трейдов.
	 * <p>
	 * @return список трейдов отсортированных в порядке открытия
	 */
	public synchronized List<RTrade> getReports() {
		List<RTrade> list = new Vector<RTrade>(reports.values());
		Collections.sort(list);
		return list;
	}
	
	/**
	 * Установить отчет.
	 * <p>
	 * Только для тестов.
	 * <p>
	 * @param descr дескриптор инструмента
	 * @param report отчет
	 */
	protected synchronized
		void setReport(SecurityDescriptor descr, ERTrade report)
	{
		reports.put(descr, report);
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != ActiveTrades.class ) {
			return false;
		}
		ActiveTrades o = (ActiveTrades) other;
		return new EqualsBuilder()
			.append(o.dispatcher, dispatcher)
			.append(o.onChanged, onChanged)
			.append(o.onEnter, onEnter)
			.append(o.onExit, onExit)
			.append(o.reports, reports)
			.isEquals();
	}
	
	/**
	 * Удалить информацию о текущих трейдах.
	 * <p>
	 * Удаляет информацию о текущих открытых трейдах.
	 * Никакие события не генерируются.
	 */
	public synchronized void clear() {
		reports.clear();
	}

}
