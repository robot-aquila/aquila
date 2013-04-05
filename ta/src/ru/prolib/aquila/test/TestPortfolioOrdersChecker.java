package ru.prolib.aquila.test;

import ru.prolib.aquila.ChaosTheory.Order;
import ru.prolib.aquila.ta.ds.MarketData;

public interface TestPortfolioOrdersChecker {
	
	public boolean canFill(Order order, MarketData data);

}
