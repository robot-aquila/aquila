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
		createInstance(EditableTerminal terminal, SecurityDescriptor descr)
	{
		return new SecurityImpl(terminal, descr,
				new SecurityEventDispatcher(terminal.getEventSystem(), descr));
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		return other != null && other.getClass() == SecurityFactory.class;
	}

}
