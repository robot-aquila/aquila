package ru.prolib.aquila.ui.plugin.getters;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.data.GDouble;

/**
 * $Id$
 */
public class GSecurityClosePrice extends GDouble {

	@Override
	public Double get(Object obj) {
		Security o = (Security) obj;
		return super.get(o.getClosePrice());
	}
}
