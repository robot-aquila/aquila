package ru.prolib.aquila.qforts.impl;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;

public class QFValidator {
	private static final CDecimal ZERO = CDecimalBD.of("0", "RUB");
	
	public int canChangePositon(QFPortfolioChangeUpdate update) {
		if ( update.getFinalFreeMargin().compareTo(ZERO) < 0 ) {
			return QFResult.INSUFFICIENT_FUNDS;
		}
		return QFResult.OK;
	}

}
