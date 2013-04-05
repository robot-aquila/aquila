package ru.prolib.aquila.ui.plugin.getters;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.data.GLong;

/**
 * $Id$
 */
public class GSecurityAskSize extends GLong {

	public Long get(Security obj) {
		return super.get(obj.getAskSize());
	}
}
