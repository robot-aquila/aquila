package ru.prolib.aquila.ui.plugin.getters;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.data.GString;

/**
 * $Id$
 */
public class GSecurityCurrency extends GString {

	public String get(Security obj) {
		return super.get(obj.getDescriptor().getCurrency());
	}
}
