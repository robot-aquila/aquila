package ru.prolib.aquila.ui.plugin.getters;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.data.GDouble;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Геттер максимально-возможной цены инструмента.
 */
public class GSecurityMaxPrice extends GDouble {
	
	public GSecurityMaxPrice() {
		super();
	}

	@Override
	public Double get(Object obj) throws ValueException {
		Security o = (Security) obj;
		return super.get(o.getMaxPrice());
	}
	
}
