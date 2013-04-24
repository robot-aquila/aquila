package ru.prolib.aquila.core.report;

import java.util.Date;

import ru.prolib.aquila.core.BusinessEntities.OrderDirection;
import ru.prolib.aquila.core.BusinessEntities.PositionType;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.Trade;

/**
 * $Id$
 */
public class TradeReport {

	private Date openTime;
	private Date closeTime;
	private SecurityDescriptor descr;
	private Long openQty = 0L;
	private Long closeQty = 0L;
	private Double openPrice = 0.0d;
	private Double closePrice = 0.0d;
	private Double openVolume = 0.0d;
	private Double closeVolume = 0.0d;
	private PositionType type;
	
	public TradeReport(PositionType type, SecurityDescriptor descr) {
		this.type = type;		
		this.descr = descr;
	}	
	
	public PositionType getType() {
		return type;
	}
	
	public Double getCloseVolume() {
		return closeVolume;
	}
	
	public Double getOpenVolume() {
		return openVolume;
	}
	
	public Double getAverageClosePrice() {
		return (closeQty == 0L)? 0.0d : closePrice/closeQty;
	}
	
	public Double getAverageOpenPrice() {		
		return (openQty == 0L)? 0.0d : openPrice/openQty;
	}
	
	public Long getQty() {
		return openQty - closeQty;
	}
	
	public SecurityDescriptor getSecurity() {
		return descr;
	}
	
	public Date getCloseTime() {
		return closeTime;
	}
	
	public Date getOpenTime() {
		return openTime;
	}
	
	public void addTrade(Trade trade) throws TradeReportException {
		if(!canAppendToReport(trade)) {
			throw new TradeReportException("Trade Qty is too big!");
		}
		if(type == PositionType.LONG) {
			if(trade.getDirection() == OrderDirection.BUY) {
				addToOpen(trade);
			} else {
				addToClose(trade);
			}
		}else {
			if(trade.getDirection() == OrderDirection.SELL) {
				addToOpen(trade);
			}else {
				addToClose(trade);
			}
		}
	}
	
	public boolean isOpen() {
		return (openQty > closeQty);
	}
	
	public boolean canAppendToReport(Trade trade) {
		boolean valid = true;
		if(type == PositionType.LONG) {
			if(trade.getDirection() == OrderDirection.SELL) {
				valid = (trade.getQty() < getUncoveredQty());
			}
		} else {
			if(trade.getDirection() == OrderDirection.BUY) {
				valid = (trade.getQty() < getUncoveredQty());
			}
		}
		return valid;
	}
	
	public Long getUncoveredQty() {
		return openQty - closeQty;
	}
	
	private void addToOpen(Trade trade) {
		openQty += trade.getQty();
		openPrice += trade.getPrice() * trade.getQty();
		openVolume += trade.getVolume();
	}
	
	private void addToClose(Trade trade) {
		closeQty += trade.getQty();
		closePrice += trade.getPrice() * trade.getQty();
		closeVolume += trade.getVolume();
	}
}
