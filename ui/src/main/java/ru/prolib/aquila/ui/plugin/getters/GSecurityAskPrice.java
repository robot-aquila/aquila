package ru.prolib.aquila.ui.plugin.getters;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.data.GDouble;
import ru.prolib.aquila.core.data.ValueException;

/**
 * $Id$
 */
public class GSecurityAskPrice extends GDouble {

	@Override
	public Double get(Object obj) throws ValueException {
		Security o = (Security) obj;
		Tick tick = o.getBestAsk();
		if ( tick == null ) {
			return null;
		}
		return super.get(tick.getPrice());
	}
}
