package ru.prolib.aquila.core.report;

import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Базовый отчет по трейдам.
 * <p>
 * Данный класс предназначен для отслеживания трейдов - последовательностей
 * сделок, приводящих к открытию и последующему закрытию позиции.
 */
public class TradesImpl implements EditableTrades, EventListener {
	private final List<TradeReport> trades;
	private final Map<TradeReport, Integer> indices;
	private final ActiveTrades activeTrades;
	private final EventDispatcher dispatcher;
	private EventType onEnter, onExit, onChanged;
	
	public TradesImpl(EventDispatcher dispatcher, EventType onEnter,
			EventType onExit, EventType onChanged)
	{
		super();
		activeTrades = new ActiveTrades();
		this.dispatcher = dispatcher;
		this.onEnter = onEnter;
		this.onExit = onExit;
		this.onChanged = onChanged;
		this.trades = new Vector<TradeReport>();
		this.indices = new Hashtable<TradeReport, Integer>();
	}
	
	/**
	 * Получить набор текущих активных трейдов.
	 * <p>
	 * @return активные трейды
	 */
	public ActiveTrades getActiveTrades() {
		return activeTrades;
	}
	
	/**
	 * Получить диспетчер событий.
	 * <p>
	 * @return диспетчер событий
	 */
	public EventDispatcher getEventDispatcher() {
		return dispatcher;
	}
	
	@Override
	public synchronized List<TradeReport> getTradeReports() {
		return Collections.unmodifiableList(trades);
	}

	@Override
	public synchronized void onEvent(Event event) {
		if ( event.isType(activeTrades.OnEnter()) ) {
			TradeReportEvent e = (TradeReportEvent) event;
			TradeReport report = e.getReport();
			Integer index = trades.size();
			indices.put(report, index);
			report = report.clone();
			trades.add(report);
			postEvent(onEnter, report, index);
			
		} else if ( event.isType(activeTrades.OnExit()) ) {
			TradeReportEvent e = (TradeReportEvent) event;
			TradeReport report = e.getReport();
			Integer index = indices.get(report);
			report = report.clone();
			trades.set(index, report);
			indices.remove(index);
			postEvent(onExit, report, index);
			
		} else if ( event.isType(activeTrades.OnChanged()) ) {
			TradeReportEvent e = (TradeReportEvent) event;
			TradeReport report = e.getReport();
			Integer index = indices.get(report);
			report = report.clone();
			trades.set(index, report);
			postEvent(onChanged, report, index);
			
		}
	}
	
	/**
	 * Генерировать событие указанного типа.
	 * <p>
	 * @param type тип события
	 * @param report отчет (при отправке используется копия)
	 * @param index индекс отчета
	 */
	private void postEvent(EventType type, TradeReport report, Integer index) {
		dispatcher.dispatch(new TradeReportEvent(type, report, index));
	}
	
	@Override
	public synchronized void start() throws StarterException {
		activeTrades.OnEnter().addListener(this);
		activeTrades.OnChanged().addListener(this);
		activeTrades.OnExit().addListener(this);
	}

	@Override
	public synchronized void stop() throws StarterException {
		indices.clear();
		trades.clear();
		activeTrades.clear();
		activeTrades.OnEnter().removeListener(this);
		activeTrades.OnChanged().removeListener(this);
		activeTrades.OnExit().removeListener(this);
	}

	@Override
	public synchronized int getTradeReportCount() {
		return trades.size();
	}

	@Override
	public synchronized TradeReport getTradeReport(int index) {
		return trades.get(index);
	}

	@Override
	public EventType OnEnter() {
		return onEnter;
	}

	@Override
	public EventType OnExit() {
		return onExit;
	}

	@Override
	public EventType OnChanged() {
		return onChanged;
	}

	@Override
	public synchronized void addTrade(Trade trade) {
		activeTrades.addTrade(trade);
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TradesImpl.class ) {
			return false;
		}
		TradesImpl o = (TradesImpl) other;
		return new EqualsBuilder()
			//.append(o.activeTrades, activeTrades) // don't cmp -> recursion 
			.append(o.dispatcher, dispatcher)
			//.append(o.indices, indices) // never equals by keys
			.append(o.onChanged, onChanged)
			.append(o.onEnter, onEnter)
			.append(o.onExit, onExit)
			.append(o.trades, trades)
			.isEquals();
	}

}
