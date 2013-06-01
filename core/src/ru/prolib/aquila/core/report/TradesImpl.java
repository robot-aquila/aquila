package ru.prolib.aquila.core.report;

import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
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
	
	public TradesImpl(ActiveTrades activeReports, EventDispatcher dispatcher,
			EventType onEnter, EventType onExit, EventType onChanged)
	{
		super();
		this.activeTrades = activeReports;
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
			trades.add(report);
			indices.put(report, index);
			dispatcher.dispatch(new TradeReportEvent(onEnter, report, index));
			
			System.err.println("enter trade report: " + report);
			System.err.println("enter report index: " + index);
			System.err.println(".");
			
		} else if ( event.isType(activeTrades.OnExit()) ) {
			TradeReportEvent e = (TradeReportEvent) event;
			TradeReport report = e.getReport();
			Integer index = indices.get(report);
			dispatcher.dispatch(new TradeReportEvent(onExit, report, index));
			indices.remove(index);
			
			System.err.println("exit trade report: " + report);
			System.err.println("exit report index: " + index);
			System.err.println(".");
			
		} else if ( event.isType(activeTrades.OnChanged()) ) {
			TradeReportEvent e = (TradeReportEvent) event;
			TradeReport report = e.getReport();
			Integer index = indices.get(report);
			dispatcher.dispatch(new TradeReportEvent(onChanged, report, index));
			
			System.err.println("change trade report: " + report);
			System.err.println("change report index: " + index);
			System.err.println(".");
			
		}
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
		System.err.println("add trade: " + trade);
		System.err.println(".");
		
		activeTrades.addTrade(trade);
	}

}
