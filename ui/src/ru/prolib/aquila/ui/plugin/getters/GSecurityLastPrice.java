package ru.prolib.aquila.ui.plugin.getters;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.data.GDouble;

/**
 * $Id$
 */
public class GSecurityLastPrice extends GDouble {

	public Double get(Security obj) {
		return super.get(obj.getLastPrice());
	}
}
