package ru.prolib.aquila.core.report;

import java.util.*;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Отчет по активным трейдам.
 */
public class ActiveTradeReports {
	private Map<SecurityDescriptor, EditableTradeReport> reports;
	private final EventDispatcher dispatcher;
	private final EventType onEnter;
	private final EventType onExit;
	private final EventType onChanged;
	
	public ActiveTradeReports(EventDispatcher dispatcher, EventType onEnter, 
			EventType onExit, EventType onChanged) 
	{
		this.dispatcher = dispatcher;
		this.onEnter = onEnter;
		this.onExit = onExit;
		this.onChanged = onChanged;
		reports = new HashMap<SecurityDescriptor, EditableTradeReport>();
	}
	
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
		EditableTradeReport report = reports.get(descr);
		if ( report == null ) {
			report = new TradeReportImpl(trade);
			reports.put(descr, report);
			dispatcher.dispatch(new TradeReportEvent(onEnter, report));
		} else {
			EditableTradeReport next = report.addTrade(trade);
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
	public synchronized TradeReport getReport(SecurityDescriptor descr) {
		return reports.get(descr);
	}
	
	/**
	 * Получить список текущих трейдов.
	 * <p>
	 * @return список трейдов отсортированных в порядке открытия
	 */
	public synchronized List<TradeReport> getReports() {
		List<TradeReport> list = new Vector<TradeReport>(reports.values());
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
		void setReport(SecurityDescriptor descr, EditableTradeReport report)
	{
		reports.put(descr, report);
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != ActiveTradeReports.class ) {
			return false;
		}
		ActiveTradeReports o = (ActiveTradeReports) other;
		return new EqualsBuilder()
			.append(o.dispatcher, dispatcher)
			.append(o.onChanged, onChanged)
			.append(o.onEnter, onEnter)
			.append(o.onExit, onExit)
			.append(o.reports, reports)
			.isEquals();
	}

}
