package ru.prolib.aquila.ui.plugin.getters;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.SecurityStatus;
import ru.prolib.aquila.core.data.G;

/**
 * $Id$
 */
public class GSecurityStatus implements G<SecurityStatus> {

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.data.G#get(java.lang.Object)
	 */
	@Override
	public SecurityStatus get(Object obj) {
		Security sc = (Security) obj;
		return sc.getStatus();
	}

}
