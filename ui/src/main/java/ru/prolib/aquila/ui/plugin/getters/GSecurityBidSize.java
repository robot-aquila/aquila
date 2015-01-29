package ru.prolib.aquila.ui.plugin.getters;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.data.GLong;
import ru.prolib.aquila.core.data.ValueException;

/**
 * $Id$
 */
public class GSecurityBidSize extends GLong {

	@Override
	public Long get(Object obj) throws ValueException {
		Security o = (Security) obj;
		return super.get(o.getBidSize());
	}
}
