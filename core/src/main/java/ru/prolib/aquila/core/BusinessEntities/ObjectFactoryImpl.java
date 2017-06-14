package ru.prolib.aquila.core.BusinessEntities;

import ru.prolib.aquila.core.BusinessEntities.osc.impl.PortfolioParamsBuilder;
import ru.prolib.aquila.core.BusinessEntities.osc.impl.PositionParamsBuilder;

public class ObjectFactoryImpl implements ObjectFactory {
	
	public ObjectFactoryImpl() {
		super();
	}

	@Override
	public EditableSecurity
		createSecurity(EditableTerminal terminal, Symbol symbol)
	{
		return new SecurityImpl(terminal, symbol);
	}

	@Override
	public EditablePortfolio
		createPortfolio(EditableTerminal terminal, Account account)
	{
		return new PortfolioImpl(new PortfolioParamsBuilder(terminal.getEventQueue())
				.withTerminal(terminal)
				.withAccount(account)
				.withObjectFactory(this)
				.buildParams());
	}

	@Override
	public EditableOrder createOrder(EditableTerminal terminal, Account account,
			Symbol symbol, long id)
	{
		return new OrderImpl(terminal, account, symbol, id);
	}

	@Override
	public EditablePosition createPosition(EditableTerminal terminal, Account account, Symbol symbol) {
		PortfolioImpl p = (PortfolioImpl) terminal.getEditablePortfolio(account);
		return new PositionImpl(new PositionParamsBuilder(terminal.getEventQueue())
				.withTerminal(terminal)
				.withAccount(account)
				.withSymbol(symbol)
				.withLock(p.lock)
				.buildParams());
	}

}
