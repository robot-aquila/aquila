package ru.prolib.aquila.experiment;

import ru.prolib.aquila.ChaosTheory.PortfolioDriver;
import ru.prolib.aquila.ChaosTheory.ServiceLocator;
import ru.prolib.aquila.ta.Value;

/**
 * Базовая стратегия, использующая в качестве стоп-линии челюсти аллигатора.
 */
public class CT0_GatorJaws extends CT0 {

	public CT0_GatorJaws(ServiceLocator locator, PortfolioDriver driver) {
		super(locator, driver);
	}
	
	@Override
	protected Value<Double> getSignalLine() {
		return gator.jaw;
	}

}
