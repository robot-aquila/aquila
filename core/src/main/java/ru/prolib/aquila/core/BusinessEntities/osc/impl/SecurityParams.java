package ru.prolib.aquila.core.BusinessEntities.osc.impl;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParams;

public interface SecurityParams extends OSCParams {
	
	Terminal getTerminal();
	
	Symbol getSymbol();

}
