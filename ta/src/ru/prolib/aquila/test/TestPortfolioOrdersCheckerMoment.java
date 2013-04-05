package ru.prolib.aquila.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.ChaosTheory.Order;
import ru.prolib.aquila.ChaosTheory.OrderException;
import ru.prolib.aquila.ta.ValueException;
import ru.prolib.aquila.ta.ds.MarketData;

public class TestPortfolioOrdersCheckerMoment implements
		TestPortfolioOrdersChecker
{
	private static final Logger logger = LoggerFactory.getLogger(TestPortfolioOrdersCheckerMoment.class); 
	
	public TestPortfolioOrdersCheckerMoment() {
		super();
	}

	@Override
	public boolean canFill(Order order, MarketData data) {
		if ( order.isMarketOrder() ) {
			return true;
		}
		try {
			if ( data.getClose().getLength() == 0 ) {
				return false;
			}
			if ( order.isLimitOrder() ) {
				return checkLimitOrder(order, data);
			} else if ( order.isStopOrder() ) {
				return checkStopOrder(order, data);
			} else {
				logger.warn("Unknown order type: {}", order);
			}
		} catch ( Exception e ) {
			logger.error("Error check order: {}", e.getMessage(), e);
		}
		return false;
	}
	
	private boolean checkLimitOrder(Order order, MarketData data)
		throws OrderException, ValueException
	{
		String msg = null;
		double close = data.getClose().get();
		double price = order.getPrice();
		if ( order.isBuy() ) {
			msg = "LIMIT BUY. Expects: close <= price, actual: ";
			if ( close <= price ) {
				debug(msg, "{} <= {} -> satisfied", close, price);
				return true;
			} else {
				debug(msg, "{} > {} -> not satisfied", close, price);
				return false;
			}
			
		} else {
			msg = "LIMIT SELL. Expects: close >= price, actual: ";
			if ( close >= price ) {
				debug(msg, "{} >= {} -> satisfied", close, price);
				return true;
			} else {
				debug(msg, "{} < {} -> not satisfied", close, price);
				return false;
			}
		}
	}
	
	private boolean checkStopOrder(Order order, MarketData data)
		throws OrderException, ValueException
	{
		String msg = null;
		double close = data.getClose().get();
		double price = order.getStopPrice();
		if ( order.isBuy() ) {
			msg = "STOP LIMIT BUY. Expects: close >= stop-price, actual: ";
			if ( close >= price ) {
				debug(msg, "{} >= {} -> satisfied", close, price);
				return true;
			} else {
				debug(msg, "{} < {} -> not satisfied", close, price);
				return false;
			}
			
		} else {
			msg = "STOP LIMIT SELL. Expects: close <= stop-price, actual: ";
			if ( close <= price ) {
				debug(msg, "{} <= {} -> satisfied", close, price);
				return true;
			} else {
				debug(msg, "{} > {} -> not satisfied", close, price);
				return false;
			}
			
		}
	}
	
	private void debug(String msg1, String msg2, double p1, double p2) {
		logger.debug(msg1 + msg2, p1, p2);
	}

}
