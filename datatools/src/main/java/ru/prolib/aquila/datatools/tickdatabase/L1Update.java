package ru.prolib.aquila.datatools.tickdatabase;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Tick;

public interface L1Update {
	
	public Symbol getSymbol();
	
	public Tick getTick();

}
