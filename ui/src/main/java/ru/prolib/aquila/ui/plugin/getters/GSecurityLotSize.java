package ru.prolib.aquila.ui.plugin.getters;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.data.GInteger;
import ru.prolib.aquila.core.data.ValueException;

/**
 * $Id$
 */
public class GSecurityLotSize extends GInteger {

	@Override
	public Integer get(Object obj) throws ValueException {
		Security o = (Security) obj;
		return super.get(o.getLotSize());
	}
}
