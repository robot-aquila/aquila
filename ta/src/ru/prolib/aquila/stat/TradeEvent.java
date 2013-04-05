package ru.prolib.aquila.stat;

/**
 * 2012-02-02
 * $Id: TradeEvent.java 196 2012-02-02 20:24:38Z whirlwind $
 * 
 * Событие связанное со сделкой.
 */
public class TradeEvent {
	public static final Integer NEW_TRADE = 0x01;
	public static final Integer TRADE_CHANGE = 0x02;
	public static final Integer TRADE_CLOSED = 0x04;
	
	private final Integer event;
	private final TradeReport trade;
	
	public static TradeEvent newTrade(TradeReport trade) {
		return new TradeEvent(NEW_TRADE, trade);
	}
	
	public static TradeEvent tradeChange(TradeReport trade) {
		return new TradeEvent(TRADE_CHANGE, trade);
	}
	
	public static TradeEvent tradeClosed(TradeReport trade) {
		return new TradeEvent(TRADE_CLOSED, trade);
	}

	private TradeEvent(Integer eventId, TradeReport tradeReport) {
		super();
		this.event = eventId;
		this.trade = tradeReport;
	}
	
	public Integer getEventId() {
		return event;
	}
	
	public TradeReport getTradeReport() {
		return trade;
	}

}
