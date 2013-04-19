package ru.prolib.aquila.ui.plugin.getters;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.data.GString;
import ru.prolib.aquila.core.data.ValueException;

/**
 * $Id$
 */
public class GSecurityCurrency extends GString {

	@Override
	public String get(Object obj) throws ValueException {
		Security o = (Security) obj;
		return super.get(o.getDescriptor().getCurrency());
	}
}
