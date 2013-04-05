package ru.prolib.aquila.tasks;

import java.util.Observer;

import ru.prolib.aquila.ChaosTheory.ServiceLocator;

/**
 * Задача открытия длинной позиции с конкретной ценой реализации и
 * фиксированным стоп-лоссом.
 * 
 * 2012-02-14
 * $Id: TargetedLongWithFixedStopLoss.java 201 2012-04-03 14:45:43Z whirlwind $
 */
public class TargetedLongWithFixedStopLoss extends TargetedWithFixedStopLoss
	implements Observer
{
	
	public TargetedLongWithFixedStopLoss(ServiceLocator locator) {
		super(locator);
		open = new LimitBuy(locator);
		close = new LimitSell(locator);
		stop = new StopLossLong(locator);
	}

}
