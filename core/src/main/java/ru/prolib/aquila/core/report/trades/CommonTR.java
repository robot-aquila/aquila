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
import ru.prolib.aquila.core.report.*;

/**
 * Базовый отчет по трейдам.
 * <p>
 * Данный класс предназначен для отслеживания трейдов - последовательностей
 * сделок, приводящих к открытию и последующему закрытию позиции.
 */
public class CommonTR implements EditableTradeReport, EventListener {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(CommonTR.class);
	}
	
	private final List<RTrade> trades;
	private final Map<RTrade, Integer> indices;
	private final ActiveTrades activeTrades;
	private final CommonTREventDispatcher dispatcher;
	
	/**
	 * Служебный конструктор.
	 * <p>
	 * @param dispatcher диспетчер событий
	 * @param activeTrades набор активный трейдов
	 */
	CommonTR(CommonTREventDispatcher dispatcher, ActiveTrades activeTrades) {
		super();
		this.activeTrades = activeTrades;
		this.dispatcher = dispatcher;
		this.trades = new Vector<RTrade>();
		this.indices = new Hashtable<RTrade, Integer>();
	}

	public CommonTR(EventSystem es, CommonTREventDispatcher dispatcher) {
		this(dispatcher, new ActiveTrades(es));
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
	public CommonTREventDispatcher getEventDispatcher() {
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
			dispatcher.fireEnter(report, index);
			
		} else if ( event.isType(activeTrades.OnExit()) ) {
			TradeReportEvent e = (TradeReportEvent) event;
			RTrade report = e.getReport();
			Integer index = indices.get(report);
			report = report.clone();
			trades.set(index, report);
			indices.remove(index);
			dispatcher.fireExit(report, index);
			
		} else if ( event.isType(activeTrades.OnChanged()) ) {
			TradeReportEvent e = (TradeReportEvent) event;
			RTrade report = e.getReport();
			Integer index = indices.get(report);
			report = report.clone();
			trades.set(index, report);
			dispatcher.fireChanged(report, index);
			
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
	public synchronized int size() {
		return trades.size();
	}

	@Override
	public synchronized RTrade getRecord(int index) {
		return trades.get(index);
	}

	@Override
	public EventType OnEnter() {
		return dispatcher.OnEnter();
	}

	@Override
	public EventType OnExit() {
		return dispatcher.OnExit();
	}

	@Override
	public EventType OnChanged() {
		return dispatcher.OnChanged();
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
		if ( other == null || other.getClass() != CommonTR.class ) {
			return false;
		}
		CommonTR o = (CommonTR) other;
		return new EqualsBuilder()
			.append(o.activeTrades, activeTrades) 
			.append(o.trades, trades)
			.isEquals();
	}

	@Override
	public RTrade getCurrent(SecurityDescriptor descr) {
		return activeTrades.getReport(descr);
	}

	@Override
	public RTrade getCurrent(Security security) {
		return getCurrent(security.getDescriptor());
	}

	@Override
	public long getPosition(SecurityDescriptor descr) {
		RTrade record = getCurrent(descr);
		if ( record == null ) return 0;
		if ( record.getType() == PositionType.LONG ) {
			return record.getUncoveredQty();
		} else {
			return - record.getUncoveredQty();
		}
	}

	@Override
	public long getPosition(Security security) {
		return getPosition(security.getDescriptor());
	}

}
