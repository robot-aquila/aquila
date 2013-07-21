package ru.prolib.aquila.core.BusinessEntities.utils;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Фабрика портфелей.
 */
public class PortfolioFactory {
	
	public PortfolioFactory() {
		super();
	}
	
	public EditablePortfolio
		createInstance(EditableTerminal terminal, Account account)
	{
		String id = "Portfolio[" + account + "]";
		EventSystem es = terminal.getEventSystem();
		EventDispatcher d = es.createEventDispatcher(id);
		PortfolioImpl p = new PortfolioImpl(terminal, account, d,
				d.createType("OnChanged"));
		EventDispatcher pd = es.createEventDispatcher(id);
		p.setPositionsInstance(new PositionsImpl(p, pd,
				pd.createType("OnPosAvailable"),
				pd.createType("OnPosChanged")));
		return p;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		return other != null && other.getClass() == PortfolioFactory.class;
	}

}
