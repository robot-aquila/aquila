package ru.prolib.aquila.ui.plugin.getters;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.SecurityEvent;
import ru.prolib.aquila.core.data.G;

/**
 * $Id$
 */
public class GSecurity implements G<Security> {

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.data.G#get(java.lang.Object)
	 */
	@Override
	public Security get(Object event) {
		SecurityEvent e = (SecurityEvent) event;
		return e.getSecurity();
	}

}
