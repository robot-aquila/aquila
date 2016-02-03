package ru.prolib.aquila.core.BusinessEntities;

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
		return new PortfolioImpl(terminal, account);
	}

	@Override
	public EditableOrder createOrder(EditableTerminal terminal, Account account,
			Symbol symbol, long id)
	{
		return new OrderImpl(terminal, account, symbol, id);
	}

}
