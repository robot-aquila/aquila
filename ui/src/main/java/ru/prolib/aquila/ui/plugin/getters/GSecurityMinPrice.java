package ru.prolib.aquila.ui.plugin.getters;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.data.GDouble;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Геттер минимально-возможной цены инструмента.
 */
public class GSecurityMinPrice extends GDouble {
	
	public GSecurityMinPrice() {
		super();
	}

	@Override
	public Double get(Object obj) throws ValueException {
		Security o = (Security) obj;
		return super.get(o.getLowerPriceLimit().doubleValue());
	}
	
}
