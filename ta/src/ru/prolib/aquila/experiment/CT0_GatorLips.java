package ru.prolib.aquila.experiment;

import ru.prolib.aquila.ChaosTheory.PortfolioDriver;
import ru.prolib.aquila.ChaosTheory.ServiceLocator;
import ru.prolib.aquila.ta.Value;

/**
 * Базовая стратегия, использующая в качестве стоп-линии губы аллигатора. 
 */
public class CT0_GatorLips extends CT0 {

	public CT0_GatorLips(ServiceLocator locator, PortfolioDriver driver) {
		super(locator, driver);
	}
	
	@Override
	protected Value<Double> getSignalLine() {
		return gator.lips;
	}

}
