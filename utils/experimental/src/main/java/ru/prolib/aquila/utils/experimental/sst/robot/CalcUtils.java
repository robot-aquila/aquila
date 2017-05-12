package ru.prolib.aquila.utils.experimental.sst.robot;

import ru.prolib.aquila.core.BusinessEntities.FDecimal;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.Tick;

public class CalcUtils {
	
	public FDecimal getLastPrice(Security security) {
		Tick dummy = security.getLastTrade();
		return FDecimal.of(dummy.getPrice(), security.getScale());
	}
	
	public FDecimal getSafe(FDecimal x) {
		return x == null ? FDecimal.ZERO0 : x; 
	}
	
	public long getSafe(Long x) {
		return x == null ? 0L : x;
	}

}
