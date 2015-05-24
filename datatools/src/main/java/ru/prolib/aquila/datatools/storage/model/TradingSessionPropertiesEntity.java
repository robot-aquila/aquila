package ru.prolib.aquila.datatools.storage.model;

import org.joda.time.DateTime;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.datatools.storage.TradingSessionProperties;

public class TradingSessionPropertiesEntity implements TradingSessionProperties {
	private Long id;
	private SymbolEntity symbol;
	private Integer scale, lotSize;
	private Double tickCost,initialMarginCost, initialPrice;
	private Double lowerPriceLimit, upperPriceLimit, tickSize;
	private DateTime snapshotTime, clearingTime;
	
	public TradingSessionPropertiesEntity() {
		super();
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public SymbolEntity getSymbol() {
		return symbol;
	}
	
	public void setSymbol(SymbolEntity symbol) {
		this.symbol = symbol;
	}

	@Override
	public SecurityDescriptor getSecurityDescriptor() {
		SymbolEntity x = getSymbol(); 
		return x == null ? null : x.getDescriptor();
	}

	@Override
	public Integer getScale() {
		return scale;
	}
	
	public void setScale(Integer scale) {
		this.scale = scale;
	}

	@Override
	public Double getTickCost() {
		return tickCost;
	}
	
	public void setTickCost(Double value) {
		this.tickCost = value;
	}

	@Override
	public Double getInitialMarginCost() {
		return initialMarginCost;
	}
	
	public void setInitialMarginCost(Double value) {
		this.initialMarginCost = value;
	}

	@Override
	public Double getInitialPrice() {
		return initialPrice;
	}
	
	public void setInitialPrice(Double value) {
		this.initialPrice = value;
	}

	@Override
	public Double getLowerPriceLimit() {
		return lowerPriceLimit;
	}
	
	public void setLowerPriceLimit(Double value) {
		this.lowerPriceLimit = value;
	}

	@Override
	public Double getUpperPriceLimit() {
		return upperPriceLimit;
	}
	
	public void setUpperPriceLimit(Double value) {
		this.upperPriceLimit = value;
	}

	@Override
	public Integer getLotSize() {
		return lotSize;
	}
	
	public void setLotSize(Integer value) {
		this.lotSize = value;
	}

	@Override
	public Double getTickSize() {
		return tickSize;
	}
	
	public void setTickSize(Double value) {
		this.tickSize = value;
	}

	@Override
	public DateTime getSnapshotTime() {
		return snapshotTime;
	}
	
	public void setSnapshotTime(DateTime time) {
		this.snapshotTime = time;
	}

	@Override
	public DateTime getClearingTime() {
		return clearingTime;
	}
	
	public void setClearingTime(DateTime time) {
		this.clearingTime = time;
	}

}
