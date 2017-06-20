package ru.prolib.aquila.core.BusinessEntities.osc.impl;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.Portfolio;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParams;

public interface PositionParams extends OSCParams {

	Terminal getTerminal();
	
	Account getAccount();

	Symbol getSymbol();

	Security getSecurity();
	
	Portfolio getPortfolio();
	
}
