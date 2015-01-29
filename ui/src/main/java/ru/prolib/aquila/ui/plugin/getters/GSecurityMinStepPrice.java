package ru.prolib.aquila.ui.plugin.getters;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.*;

public class GSecurityMinStepPrice extends GDouble {

	@Override
	public Double get(Object obj) throws ValueException {
		Security o = (Security) obj;
		return super.get(o.getMinStepPrice());
	}
}
