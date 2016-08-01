package ru.prolib.aquila.core.BusinessEntities;


public interface L1Update extends TStamped {
	
	public Symbol getSymbol();
	
	public Tick getTick();

}
