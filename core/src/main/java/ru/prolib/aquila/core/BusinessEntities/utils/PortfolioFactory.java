package ru.prolib.aquila.core.BusinessEntities.utils;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.CommonModel.Positions;

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
		EventSystem es = terminal.getEventSystem();
		PortfolioImpl p = new PortfolioImpl(terminal, account,
				new PortfolioEventDispatcher(es, account));
		p.setPositionsInstance(new Positions(p,
				new PositionsEventDispatcher(es, account)));
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
