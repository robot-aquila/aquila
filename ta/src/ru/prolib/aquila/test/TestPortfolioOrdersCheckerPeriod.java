package ru.prolib.aquila.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.ChaosTheory.Order;
import ru.prolib.aquila.ChaosTheory.OrderException;
import ru.prolib.aquila.ta.ValueException;
import ru.prolib.aquila.ta.ds.MarketData;

/**
 * Проверка условия исполнения заявки в текущем периоде. 
 * Данная реализация предназначена для проверки заявок, выставленных до начала
 * текущего периода (текущего бара). Использовать данный класс для проверки
 * только что поступивших заявок нельзя!
 */
public class TestPortfolioOrdersCheckerPeriod implements
	TestPortfolioOrdersChecker
{
	static private final Logger logger = LoggerFactory.getLogger(TestPortfolioOrdersCheckerPeriod.class);
	
	public TestPortfolioOrdersCheckerPeriod() {
		super();
	}

	@Override
	public boolean canFill(Order order, MarketData data) {
		if ( order.isMarketOrder() ) {
			return true;
		}
		try { 
			if ( data.getHigh().getLength() == 0 ) {
				return false;
			}
			if ( order.isStopOrder() ) {
				return checkStopOrder(order, data);
			} else if ( order.isLimitOrder() ) {
				return checkLimitOrder(order, data);
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
		double hi = data.getHigh().get();
		double lo = data.getLow().get();
		double price = order.getPrice();
		if ( order.isBuy() ) {
			msg = "LIMIT BUY. Expects: lo <= price, actual: ";
			if ( lo <= price ) {
				debug(msg, "{} <= {} -> satisfied", lo, price);
				return true;
			} else {
				debug(msg, "{} > {} -> not satisfied", lo, price);
				return false;
			}
			
		} else {
			msg = "LIMIT SELL. Expects: hi >= price, actual: ";
			if ( hi >= price ) {
				debug(msg, "{} >= {} -> satisfied", hi, price);
				return true;
			} else {
				debug(msg, "{} < {} -> not satisfied", hi, price);
				return false;
			}
		}
	}
	
	private boolean checkStopOrder(Order order, MarketData data)
		throws OrderException, ValueException
	{
		String msg = null;
		double hi = data.getHigh().get();
		double lo = data.getLow().get();
		double price = order.getStopPrice();
		if ( order.isBuy() ) {
			msg = "STOP LIMIT BUY. Expects: hi >= stop-price, actual: ";
			if ( hi >= price ) {
				debug(msg, "{} >= {} -> satisfied", hi, price);
				return true;
			} else {
				debug(msg, "{} < {} -> not satisfied", hi, price);
				return false;
			}
			
		} else {
			msg = "STOP LIMIT SELL. Expects: lo <= stop-price, actual: ";
			if ( lo <= price ) {
				debug(msg, "{} <= {} -> satisfied", lo, price);
				return true;
			} else {
				debug(msg, "{} > {} -> not satisfied", lo, price);
				return false;
			}
			
		}
	}
	
	private void debug(String msg1, String msg2, double p1, double p2) {
		logger.debug(msg1 + msg2, p1, p2);
	}

}
