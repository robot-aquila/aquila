package ru.prolib.aquila.qforts.impl;

public class QFValidator {
	
	public int canChangePositon(QFPortfolioChangeUpdate update) {
		if ( update.getFinalFreeMargin().doubleValue() < 0d ) {
			return QFResult.INSUFFICIENT_FUNDS;
		}
		return QFResult.OK;
	}

}
