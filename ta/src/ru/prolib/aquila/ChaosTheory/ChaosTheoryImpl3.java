package ru.prolib.aquila.ChaosTheory;

import ru.prolib.aquila.ta.Signal;

/**
 * Модификация стратегии #3.
 * Еще один вариант, аналогичен второму, только фракталы тоже учитываются.
 */
public class ChaosTheoryImpl3 extends ChaosTheoryImpl2 {

	public ChaosTheoryImpl3(ServiceLocator locator,
							PortfolioDriver driver) {
		super(locator, driver);
	}
	
	protected boolean isReversedSignalAreImportant(Signal signal) {
		return true;
	}

}
