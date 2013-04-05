package ru.prolib.aquila.tasks;

import ru.prolib.aquila.ChaosTheory.Order;
import ru.prolib.aquila.ChaosTheory.PortfolioException;
import ru.prolib.aquila.ChaosTheory.ServiceLocator;

public class LimitBuy extends LimitOrder {
	
	public LimitBuy(ServiceLocator locator) {
		super(locator);
	}
	
	@Override
	protected Order createOrder()
		throws PortfolioException, InterruptedException
	{
		return portfolio.limitBuy(qty, price, comment);
	}

}
