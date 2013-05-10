package ru.prolib.aquila.core.BusinessEntities.utils;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Фабрика инструментов биржевой торговли.
 * <p>
 * 2012-07-05<br>
 * $Id: SecurityFactoryImpl.java 522 2013-02-12 12:07:35Z whirlwind $
 */
public class SecurityFactoryImpl implements SecurityFactory {
	
	/**
	 * Конструктор.
	 */
	public SecurityFactoryImpl() {
		super();
	}

	@Override
	public EditableSecurity
		createSecurity(EditableTerminal terminal, SecurityDescriptor descr)
	{
		EventSystem es = terminal.getEventSystem();
		EventDispatcher dispatcher =
			es.createEventDispatcher("Security[" + descr + "]");
		return new SecurityImpl(terminal, descr, dispatcher,
				es.createGenericType(dispatcher, "OnChanged"),
				es.createGenericType(dispatcher, "OnTrade"));
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != SecurityFactoryImpl.class ) {
			return false;
		}
		return true;
	}

}
