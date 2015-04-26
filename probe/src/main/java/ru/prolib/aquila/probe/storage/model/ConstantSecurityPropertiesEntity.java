package ru.prolib.aquila.probe.storage.model;

import java.util.*;

import org.joda.time.*;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.probe.storage.ConstantSecurityProperties;

public class ConstantSecurityPropertiesEntity
	implements ConstantSecurityProperties
{
	private Long id;
	private SymbolEntity symbol;
	private String displayName;
	private DateTime expirationTime;
	private Currency currencyOfCost;
	
	public ConstantSecurityPropertiesEntity() {
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
	
	public void setSymbol(SymbolEntity id) {
		this.symbol = id;
	}
	
	@Override
	public SecurityDescriptor getSecurityDescriptor() {
		return symbol.getDescriptor();
	}
	
	@Override
	public String getDisplayName() {
		return displayName;
	}
	
	public void setDisplayName(String name) {
		this.displayName = name;
	}
	
	@Override
	public DateTime getExpirationTime() {
		return expirationTime;
	}
	
	public void setExpirationTime(DateTime time) {
		this.expirationTime = time;
	}
	
	@Override
	public Currency getCurrencyOfCost() {
		return currencyOfCost;
	}
	
	public void setCurrencyOfCost(Currency currency) {
		this.currencyOfCost = currency;
	}

}
