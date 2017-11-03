package ru.prolib.aquila.utils.experimental.sst.robot;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.Tick;

public class CalcUtils {
	
	public CDecimal getLastPrice(Security security) {
		Tick dummy = security.getLastTrade();
		return CDecimalBD.of(Double.toString(dummy.getPrice())).withScale(security.getScale());
	}
	
	public CDecimal getSafe(CDecimal x) {
		return x == null ? CDecimalBD.ZERO : x; 
	}
	
}
