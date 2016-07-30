package ru.prolib.aquila.core.BusinessEntities;


public interface L1Update extends Timestamped {
	
	public Symbol getSymbol();
	
	public Tick getTick();

}
