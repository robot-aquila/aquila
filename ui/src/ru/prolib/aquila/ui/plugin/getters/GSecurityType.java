package ru.prolib.aquila.ui.plugin.getters;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.data.GString;

/**
 * $Id$
 */
public class GSecurityType extends GString {

	public String get(Security obj) {
		return super.get(obj.getDescriptor().getType().getName());
	}
}
