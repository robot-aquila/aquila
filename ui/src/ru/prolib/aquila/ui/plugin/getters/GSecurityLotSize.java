package ru.prolib.aquila.ui.plugin.getters;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.data.GInteger;

/**
 * $Id$
 */
public class GSecurityLotSize extends GInteger {

	public Integer get(Security obj) {
		return super.get(obj.getLotSize());
	}
}
