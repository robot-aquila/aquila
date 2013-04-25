package ru.prolib.aquila.core.report;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.OrderDirection;
import ru.prolib.aquila.core.BusinessEntities.PositionType;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.Trade;

/**
 * $Id$
 */
public class ActiveTrades {

	private Map<SecurityDescriptor, TradeReport> trades = new HashMap<SecurityDescriptor, TradeReport>();
	private final EventDispatcher dispatcher;
	private final EventType onReportOpened;
	private final EventType onReportClosed;
	private final EventType onReportChanged;
	
	public ActiveTrades(EventDispatcher dispatcher, EventType onOpened, EventType onClosed, 
			EventType onChanged) 
	{
		this.dispatcher = dispatcher;
		onReportOpened = onOpened;
		onReportClosed = onClosed;
		onReportChanged = onChanged;
	}
	
	public EventDispatcher getDispatcher() {
		return dispatcher;
	}
	
	public EventType OnReportOpened() {
		return onReportOpened;
	}
	
	public EventType OnReportClosed() {
		return onReportClosed;
	}
	
	public EventType OnReportChanged() {
		return onReportChanged;
	}
	
	public void addTrade(Trade trade) throws TradeReportException {
		TradeReport report = trades.get(trade.getSecurityDescriptor());
		if(report == null) {
			report = openTradeReport(trade);
		}
		if(report.canAppendToReport(trade)) {
			report.addTrade(trade);
		}else {
			Trade trade1 = new Trade(trade.getTerminal());
			Trade trade2 = new Trade(trade.getTerminal());
			trade1.setDirection(trade.getDirection());
			trade2.setDirection(trade.getDirection());
			trade1.setSecurityDescriptor(trade.getSecurityDescriptor());
			trade2.setSecurityDescriptor(trade.getSecurityDescriptor());
			trade1.setTime(trade.getTime());
			trade2.setTime(trade.getTime());
			trade1.setPrice(trade.getPrice());
			trade2.setPrice(trade.getPrice());
			
			trade1.setQty(report.getQty());
			trade1.setVolume(trade.getVolume()/trade.getQty()*trade1.getQty());
			
			trade2.setQty(trade.getQty()-report.getQty());
			trade2.setVolume(trade.getVolume()/trade.getQty()*trade2.getQty());
			
			report.addTrade(trade1);
			closeTradeReport(report);
			
			report = openTradeReport(trade2);
			report.addTrade(trade2);
		}
		if(! trades.containsValue(report)) {
			trades.put(report.getSecurity(), report);
			fireReportOpened(report);
		}
		if(report.isOpen()) {
			fireReportChanged(report);
		}else {
			closeTradeReport(report);
		}
	}
	
	public TradeReport getReport(SecurityDescriptor descr) {
		return trades.get(descr);
	}
	
	public List<TradeReport> getReports() {
		List<TradeReport> reports = new Vector<TradeReport>();
		for(TradeReport report : trades.values()) {
			reports.add(report);
		}
		return reports;
	}
	
	protected void fireReportOpened(TradeReport report) {
		dispatcher.dispatch(new TradeReportEvent(onReportOpened, report));
	}
	
	protected void fireReportClosed(TradeReport report) {
		dispatcher.dispatch(new TradeReportEvent(onReportClosed, report));
	}
	
	protected void fireReportChanged(TradeReport report) {
		dispatcher.dispatch(new TradeReportEvent(onReportChanged, report));
	}
	
	private TradeReport openTradeReport(Trade trade) {
		PositionType type = trade.getDirection() == OrderDirection.BUY? 
				PositionType.LONG : PositionType.SHORT;
		return new TradeReport(type, trade.getSecurityDescriptor());		
	}
	
	private void closeTradeReport(TradeReport report) {
		trades.remove(report.getSecurity());
		fireReportClosed(report);
	}
}
