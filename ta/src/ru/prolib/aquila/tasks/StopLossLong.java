package ru.prolib.aquila.tasks;

import ru.prolib.aquila.ChaosTheory.AssetException;
import ru.prolib.aquila.ChaosTheory.Order;
import ru.prolib.aquila.ChaosTheory.PortfolioException;
import ru.prolib.aquila.ChaosTheory.ServiceLocator;

public class StopLossLong extends StopLoss {

	public StopLossLong(ServiceLocator locator) {
		super(locator);
	}

	@Override
	protected Order createStopOrder()
		throws PortfolioException, InterruptedException
	{
		try {
			double stopPrice = price - (asset.getPriceStep() * slippage);
			return portfolio.stopSell(qty, stopPrice, price, comment);
		} catch ( AssetException e ) {
			throw new PortfolioException(e);
		}
	}

}
