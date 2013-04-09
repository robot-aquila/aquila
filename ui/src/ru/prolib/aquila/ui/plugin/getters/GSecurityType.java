package ru.prolib.aquila.ui.plugin.getters;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.data.GString;

/**
 * $Id$
 */
public class GSecurityType extends GString {

	@Override
	public String get(Object obj) {
		Security o = (Security) obj;
		return super.get(o.getDescriptor().getType().getName());
	}
}
