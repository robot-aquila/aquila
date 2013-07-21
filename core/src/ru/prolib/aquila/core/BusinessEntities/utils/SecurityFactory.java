package ru.prolib.aquila.core.BusinessEntities.utils;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventSystem;
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
		EventSystem es = terminal.getEventSystem();
		EventDispatcher dispatcher =
			es.createEventDispatcher("Security[" + descr + "]");
		EditableSecurity s = new SecurityImpl(terminal, descr, dispatcher,
				es.createGenericType(dispatcher, "OnChanged"),
				es.createGenericType(dispatcher, "OnTrade"));
		return s;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		return other != null && other.getClass() == SecurityFactory.class;
	}

}
