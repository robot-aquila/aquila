package ru.prolib.aquila.tasks;

import ru.prolib.aquila.ChaosTheory.Order;
import ru.prolib.aquila.ChaosTheory.PortfolioException;
import ru.prolib.aquila.ChaosTheory.ServiceLocator;

public class TrailingStopLossLong extends TrailingStopLoss {
	private double local;

	public TrailingStopLossLong(ServiceLocator locator) {
		super(locator);
	}

	@Override
	protected Order createOrder(int qty, double price,
								double slippage, String comment)
		throws PortfolioException, InterruptedException
	{
		return portfolio.stopSell(qty, price + slippage, price, comment);
	}

	@Override
	protected boolean updateLocalPrice(double current) {
		if ( current > local ) {
			getClassLogger()
				.debug("Local maximum updated from {} to {}", local, current);
			local = current;
		}
		if ( local > spreadPoints * 1.5 + price ) {
			price = local - spreadPoints;
			getClassLogger().debug("Move order up to: {}", price);
			return true;
		}
		return false;
	}

	@Override
	protected void initialLocalPrice(double price) {
		local = price;
	}

}
