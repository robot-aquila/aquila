package ru.prolib.aquila.core.BusinessEntities;

import ru.prolib.aquila.core.BusinessEntities.osc.impl.OrderParamsBuilder;
import ru.prolib.aquila.core.BusinessEntities.osc.impl.PortfolioParamsBuilder;
import ru.prolib.aquila.core.BusinessEntities.osc.impl.PositionParamsBuilder;
import ru.prolib.aquila.core.BusinessEntities.osc.impl.SecurityParamsBuilder;

/**
 * Object factory of standard model entities.
 * <p>
 * <b>This class is not thread-safe!</b>
 * <p>
 * This implementation works considering that the:
 * <ul>
 * <li>All objects excepting terminal were built by this factory;</li>
 * <li>This thread owns an exclusive lock on the terminal during the call;</li>
 * </ul>
 * Failure to comply with these conditions may result in a deadlock or unexpected exceptions.
 */
public class ObjectFactoryImpl implements ObjectFactory {
	
	public ObjectFactoryImpl() {
		super();
	}

	@Override
	public EditableSecurity
		createSecurity(EditableTerminal terminal, Symbol symbol)
	{
		return new SecurityImpl(new SecurityParamsBuilder(terminal.getEventQueue())
				.withTerminal(terminal)
				.withSymbol(symbol)
				.buildParams());
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
		return new OrderImpl(new OrderParamsBuilder(terminal.getEventQueue())
				.withTerminal(terminal)
				.withAccount(account)
				.withSymbol(symbol)
				.withOrderID(id)
				.buildParams());
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
