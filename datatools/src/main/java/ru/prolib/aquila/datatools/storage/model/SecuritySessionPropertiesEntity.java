package ru.prolib.aquila.datatools.storage.model;

import java.time.LocalDateTime;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.datatools.storage.SecuritySessionProperties;

public class SecuritySessionPropertiesEntity implements SecuritySessionProperties {
	private Long id;
	private SymbolEntity symbol;
	private Integer scale, lotSize;
	private Double tickCost,initialMarginCost, initialPrice;
	private Double lowerPriceLimit, upperPriceLimit, tickSize;
	private LocalDateTime snapshotTime, clearingTime;
	
	public SecuritySessionPropertiesEntity() {
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

	// TODO: SymbolEntity needs refactoring
	@Override
	public Symbol getSymbolInfo() {
		SymbolEntity x = getSymbol(); 
		return x == null ? null : x.getSymbol();
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
	public LocalDateTime getSnapshotTime() {
		return snapshotTime;
	}
	
	public void setSnapshotTime(LocalDateTime time) {
		this.snapshotTime = time;
	}

	@Override
	public LocalDateTime getClearingTime() {
		return clearingTime;
	}
	
	public void setClearingTime(LocalDateTime time) {
		this.clearingTime = time;
	}

}
