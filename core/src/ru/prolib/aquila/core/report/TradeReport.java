package ru.prolib.aquila.core.report;

import java.util.Date;

import org.apache.commons.lang3.builder.HashCodeBuilder;

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
	
	protected TradeReport(PositionType type, SecurityDescriptor descr, 
			Date openTime, Date closeTime, Long openQty, Long closeQty,
			Double openPrice, Double closePrice, 
			Double openVolume, Double closeVolume)
	{
		this.type = type;
		this.descr = descr;
		this.openTime = openTime;
		this.closeTime = closeTime;
		this.openPrice = openPrice;
		this.closePrice = closePrice;
		this.openQty = openQty;
		this.closeQty = closeQty;
		this.openVolume = openVolume;
		this.closeVolume = closeVolume;
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
		return openQty;
	}
	
	public Long getUncoveredQty() {
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
			throw new TradeReportException("Can't append trade to report!");
		}
		if(trade.getSecurityDescriptor() != descr) {
			throw new TradeReportException("Invalid SecurityDescriptor!");
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
		boolean valid = false;		
		if(type == PositionType.LONG) {
			if(trade.getDirection() == OrderDirection.SELL) {
				if(trade.getQty() <= getUncoveredQty()) {
					valid = true;
				}
			}else {
				valid = true;
			}
		} else {
			if(trade.getDirection() == OrderDirection.BUY) {
				if(trade.getQty() <= getUncoveredQty()) {
					valid = true;
				}
			}else {
				valid = true;
			}
		}
		return valid;
	}
	
	public boolean equals(Object other) {			
		if(other instanceof TradeReport) {
			TradeReport o = (TradeReport) other;
			if(descr != o.descr) return false;
			if(type != o.type) return false;
			if(openTime != o.openTime) return false;
			if(closeTime != o.closeTime) return false;
			if(!openQty.equals(o.openQty)) return false;
			if(! closeQty.equals(o.closeQty)) return false;
			if(Double.compare(openPrice, o.openPrice) != 0) return false;
			if(Double.compare(closePrice, o.closePrice) != 0) return false;
			if(Double.compare(openVolume, o.openVolume) != 0) return false;
			if(Double.compare(closeVolume, o.closeVolume) != 0) return false;
		}else {
			return false;
		}
		return true;
	}
	
	public int hashCode() {
		return new HashCodeBuilder(23748293, 574037)
			.toHashCode();
	}
	
	private void addToOpen(Trade trade) {
		if(openTime == null) {
			openTime = trade.getTime();
		}
		openQty += trade.getQty();
		openPrice += trade.getPrice() * trade.getQty();
		openVolume += trade.getVolume();
	}
	
	private void addToClose(Trade trade) {
		closeQty += trade.getQty();
		closePrice += trade.getPrice() * trade.getQty();
		closeVolume += trade.getVolume();
		if(! isOpen()) {
			closeTime = trade.getTime();
		}
	}
}
