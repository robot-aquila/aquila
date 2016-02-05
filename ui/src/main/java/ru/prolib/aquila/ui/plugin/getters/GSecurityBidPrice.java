package ru.prolib.aquila.ui.plugin.getters;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.data.GDouble;
import ru.prolib.aquila.core.data.ValueException;

/**
 * $Id$
 */
public class GSecurityBidPrice extends GDouble {

	@Override
	public Double get(Object obj) throws ValueException {
		Security o = (Security) obj;
		Tick tick = o.getBestBid();
		return tick == null ? null : tick.getPrice();
	}
}
