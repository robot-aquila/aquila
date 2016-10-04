package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;


public interface L1Update extends TStamped {
	
	public Symbol getSymbol();
	
	public Tick getTick();
	
	public L1Update withTime(Instant newTime);

}
