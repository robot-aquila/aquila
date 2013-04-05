package ru.prolib.aquila.ui.plugin.getters;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.data.GInteger;

/**
 * $Id$
 */
public class GSecurityPrecision extends GInteger {

	public Integer get(Security obj) {
		return super.get(obj.getPrecision());
	}
}
