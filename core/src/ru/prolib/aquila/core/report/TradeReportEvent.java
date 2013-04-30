package ru.prolib.aquila.core.report;

import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.EventType;

/**
 * $Id$
 */
public class TradeReportEvent extends EventImpl {

	private TradeReport report;
	/**
	 * @param type
	 * @param report
	 */
	public TradeReportEvent(EventType type, TradeReport report) {
		super(type);
		this.report = report;
	}
	
	public TradeReport getTradeReport() {
		return report;
	}
	
	public boolean equals(Object other) {
		if(other instanceof TradeReportEvent && 
				((TradeReportEvent) other).isType(getType())) {
			TradeReportEvent o = (TradeReportEvent) other;
			return report.equals(o.getTradeReport())? true : false;
			
		}
		return false;
	}

}
