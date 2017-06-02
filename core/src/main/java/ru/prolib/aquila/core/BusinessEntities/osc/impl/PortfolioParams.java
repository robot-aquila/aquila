package ru.prolib.aquila.core.BusinessEntities.osc.impl;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParams;

public interface PortfolioParams extends OSCParams {

	Terminal getTerminal();
	
	Account getAccount();

}
