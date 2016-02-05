package ru.prolib.aquila.ui.plugin.getters;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.data.GLong;
import ru.prolib.aquila.core.data.ValueException;

/**
 * $Id$
 */
public class GSecurityBidSize extends GLong {

	@Override
	public Long get(Object obj) throws ValueException {
		Security o = (Security) obj;
		Tick tick = o.getBestBid();
		return tick == null ? null : tick.getSize();
	}
}
