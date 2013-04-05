package ru.prolib.aquila.tasks;

import ru.prolib.aquila.ChaosTheory.ServiceLocator;

/**
 * Задача открытия короткой позиции с конкретной ценой реализации и
 * фиксированным стоп-лоссом.
 * 
 * 2012-02-14
 * $Id: TargetedShortWithFixedStopLoss.java 201 2012-04-03 14:45:43Z whirlwind $
 */
public class TargetedShortWithFixedStopLoss extends TargetedWithFixedStopLoss {

	public TargetedShortWithFixedStopLoss(ServiceLocator locator) {
		super(locator);
		open = new LimitSell(locator);
		close = new LimitBuy(locator);
		stop = new StopLossShort(locator);
	}

}
