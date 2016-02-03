package ru.prolib.aquila.core.BusinessEntities;

public interface ObjectFactory {
	
	public EditableSecurity
		createSecurity(EditableTerminal terminal, Symbol symbol);
	
	public EditablePortfolio
		createPortfolio(EditableTerminal terminal, Account account);
	
	public EditableOrder createOrder(EditableTerminal terminal,
			Account account, Symbol symbol, long id);

}
