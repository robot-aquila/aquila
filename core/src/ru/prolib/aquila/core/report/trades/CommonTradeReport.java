package ru.prolib.aquila.core.report.trades;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.report.RTrade;
import ru.prolib.aquila.core.report.TradeReportEvent;

/**
 * Базовый отчет по трейдам.
 * <p>
 * Данный класс предназначен для отслеживания трейдов - последовательностей
 * сделок, приводящих к открытию и последующему закрытию позиции.
 */
public class CommonTradeReport implements EditableTradeReport, EventListener {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(CommonTradeReport.class);
	}
	
	private final List<RTrade> trades;
	private final Map<RTrade, Integer> indices;
	private final ActiveTrades activeTrades;
	private final EventDispatcher dispatcher;
	private EventType onEnter, onExit, onChanged;
	
	public CommonTradeReport(EventDispatcher dispatcher, EventType onEnter,
			EventType onExit, EventType onChanged)
	{
		super();
		activeTrades = new ActiveTrades();
		this.dispatcher = dispatcher;
		this.onEnter = onEnter;
		this.onExit = onExit;
		this.onChanged = onChanged;
		this.trades = new Vector<RTrade>();
		this.indices = new Hashtable<RTrade, Integer>();
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
	public synchronized List<RTrade> getRecords() {
		return new Vector<RTrade>(trades);
	}

	@Override
	public synchronized void onEvent(Event event) {
		if ( event.isType(activeTrades.OnEnter()) ) {
			TradeReportEvent e = (TradeReportEvent) event;
			RTrade report = e.getReport();
			Integer index = trades.size();
			indices.put(report, index);
			report = report.clone();
			trades.add(report);
			postEvent(onEnter, report, index);
			
		} else if ( event.isType(activeTrades.OnExit()) ) {
			TradeReportEvent e = (TradeReportEvent) event;
			RTrade report = e.getReport();
			Integer index = indices.get(report);
			report = report.clone();
			trades.set(index, report);
			indices.remove(index);
			postEvent(onExit, report, index);
			
		} else if ( event.isType(activeTrades.OnChanged()) ) {
			TradeReportEvent e = (TradeReportEvent) event;
			RTrade report = e.getReport();
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
	private void postEvent(EventType type, RTrade report, Integer index) {
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
	public synchronized int size() {
		return trades.size();
	}

	@Override
	public synchronized RTrade getRecord(int index) {
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
		if ( ! activeTrades.OnEnter().isListener(this) ) {
			logger.error("Cannot process trade cuz not started");
		} else {
			activeTrades.addTrade(trade);
		}
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != CommonTradeReport.class ) {
			return false;
		}
		CommonTradeReport o = (CommonTradeReport) other;
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
