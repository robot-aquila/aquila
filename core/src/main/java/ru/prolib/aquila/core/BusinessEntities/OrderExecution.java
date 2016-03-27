package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;

public interface OrderExecution {
	
	public long getID();
	
	public String getExternalID();
	
	public long getOrderID();
	
	public Symbol getSymbol();
	
	public Instant getTime();
	
	public OrderAction getAction();
	
	public double getPricePerUnit();
	
	public long getVolume();
	
	public double getValue();
	
	public Terminal getTerminal();

}
