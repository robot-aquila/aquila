package ru.prolib.aquila.core.BusinessEntities.utils;

import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Фабрика инструментов.
 */
public class SecurityFactory {
	
	public SecurityFactory() {
		super();
	}
	
	public EditableSecurity
		createInstance(EditableTerminal terminal, Symbol symbol)
	{
		return new SecurityImpl(terminal, symbol,
				new SecurityEventDispatcher(terminal.getEventSystem(), symbol));
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		return other != null && other.getClass() == SecurityFactory.class;
	}

}
