package ru.prolib.aquila.tasks;

import ru.prolib.aquila.ChaosTheory.Order;
import ru.prolib.aquila.ChaosTheory.PortfolioException;
import ru.prolib.aquila.ChaosTheory.ServiceLocator;

public class LimitSell extends LimitOrder {

	public LimitSell(ServiceLocator locator) {
		super(locator);
	}

	@Override
	protected Order createOrder()
		throws PortfolioException, InterruptedException
	{
		return portfolio.limitSell(qty, price, comment);
	}

}
