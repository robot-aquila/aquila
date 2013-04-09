package ru.prolib.aquila.ui.plugin.getters;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.data.GLong;

/**
 * $Id$
 */
public class GSecurityAskSize extends GLong {

	@Override
	public Long get(Object obj) {
		Security o = (Security) obj;
		return super.get(o.getAskSize());
	}
}
