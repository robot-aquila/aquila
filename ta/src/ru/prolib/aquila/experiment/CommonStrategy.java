package ru.prolib.aquila.experiment;

import ru.prolib.aquila.ChaosTheory.Asset;
import ru.prolib.aquila.ChaosTheory.PortfolioDriver;
import ru.prolib.aquila.ChaosTheory.ServiceLocator;
import ru.prolib.aquila.ChaosTheory.TradingStrategy;

abstract public class CommonStrategy extends TradingStrategy {

	public CommonStrategy(ServiceLocator locator, PortfolioDriver driver) {
		super(locator, driver);
	}

	@Override
	public void nextPass() throws Exception {
		if ( driver.isLong() ) {
			inLongPosition();
		} else if ( driver.isShort() ) {
			inShortPosition();
		} else {
			inNeutralPosition();
		}
	}
	
	public Asset getAsset() {
		return driver.getAsset();
	}
	
	abstract public void inLongPosition() throws Exception;
	
	abstract public void inShortPosition() throws Exception;
	
	abstract public void inNeutralPosition() throws Exception;

}
