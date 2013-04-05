package ru.prolib.aquila.tasks;

import ru.prolib.aquila.ChaosTheory.ServiceLocator;

public class OpenLongWithTrailingStopLoss extends OpenWithTrailingStopLoss {

	public OpenLongWithTrailingStopLoss(ServiceLocator locator) {
		super(locator);
		open = new LimitBuy(locator);
		stop = new TrailingStopLossLong(locator);
	}

}
