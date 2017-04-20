package ru.prolib.aquila.qforts.impl;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.FMoney;
import ru.prolib.aquila.core.BusinessEntities.Portfolio;

public class QFortsEnv {
	private final EditableTerminal terminal;
	private final QForts facade;
	
	public QFortsEnv(EditableTerminal terminal, QForts facade) {
		this.terminal = terminal;
		this.facade = facade;
	}
	
	public Portfolio createPortfolio(Account account, FMoney balance)
			throws QFTransactionException
	{
		EditablePortfolio p = terminal.getEditablePortfolio(account);
		facade.changeBalance(p, balance);
		return p;
	}

}
