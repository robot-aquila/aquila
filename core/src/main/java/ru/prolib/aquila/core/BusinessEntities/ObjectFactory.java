package ru.prolib.aquila.core.BusinessEntities;

public interface ObjectFactory {
	EditableSecurity createSecurity(EditableTerminal terminal, Symbol symbol);
	EditablePortfolio createPortfolio(EditableTerminal terminal, Account account);
	EditableOrder createOrder(EditableTerminal terminal, Account account, Symbol symbol, long id);
	EditablePosition createPosition(EditableTerminal terminal, Account account, Symbol symbol);
}
