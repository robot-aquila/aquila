package ru.prolib.aquila.test;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.ChaosTheory.Order;
import ru.prolib.aquila.ChaosTheory.OrderImpl;
import ru.prolib.aquila.ta.TestValue;
import ru.prolib.aquila.ta.ds.MarketData;
import ru.prolib.aquila.ta.ds.MarketDataImpl;
import ru.prolib.aquila.ta.ds.MarketDataReader;

public class TestPortfolioOrdersCheckerPeriodTest {
	IMocksControl control;
	MarketDataImpl data;
	MarketDataReader dataReader;
	TestValue<Double> hi,lo;
	TestPortfolioOrdersCheckerPeriod checker;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.ALL);
	}
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		hi = new TestValue<Double>(MarketData.HIGH);
		lo = new TestValue<Double>(MarketData.LOW);
		dataReader = control.createMock(MarketDataReader.class);
		data = new MarketDataImpl(dataReader);
		data.addValue(hi);
		data.addValue(lo);
		checker = new TestPortfolioOrdersCheckerPeriod();
	}
	
	@Test
	public void testCanFill_AlwaysYesForMarketOrder() throws Exception {
		Order order = new OrderImpl(1L, Order.BUY, 1);
		
		assertTrue(checker.canFill(order, data));
	}
	
	@Test
	public void testCanFill_NoDataForStopOrder() throws Exception {
		Order order = new OrderImpl(1L, Order.SELL, 1, 100.00d, 200.00d);
		
		assertFalse(checker.canFill(order, data));
	}
	
	@Test
	public void testCanFill_NoDataForLimitOrder() throws Exception {
		Order order = new OrderImpl(1L, Order.BUY, 1, 100.00d);
		
		assertFalse(checker.canFill(order, data));
	}
	
	/**
	 * Тестируются ситуации:
	 * - стоп-заявка на покупку исполняется, когда high >= стоп-цене
	 * - стоп-заявка на продажу исполняется, когда low <= стоп-цене
	 * @throws Exception
	 */
	@Test
	public void testCanFill_StopOrder() throws Exception {
		Object fix[][] = {
			// type,      stop-price, expected result 
			{ Order.BUY,  115.00d,    false },
			{ Order.BUY,  110.00d,	  true  },
			{ Order.BUY,  105.00d,	  true  },
			{ Order.BUY,  100.00d,	  true  },
			{ Order.BUY,   95.00d,	  true  },
			{ Order.SELL,  95.00d,    false },
			{ Order.SELL, 100.00d,    true  },
			{ Order.SELL, 105.00d,    true  },
			{ Order.SELL, 110.00d,    true  },
			{ Order.SELL, 115.00d,    true  },
		};
		hi.addToStackAndUpdate(110.00d);
		lo.addToStackAndUpdate(100.00d);
		for ( int i = 0; i < fix.length; i ++ ) {
			Order order = new OrderImpl(1L, (Integer)fix[i][0], 1,
					(Double)fix[i][1], 0.0d);
			String msg = "At #" + i;
			assertEquals(msg, (Boolean)fix[i][2], checker.canFill(order, data));
		}
	}
	
	/**
	 * Тестируются ситуации:
	 * - лимитная заявка на покупку исполняется, когда low <= цены
	 * - лимитная заявка на продажу исполняется, когда high >= цены
	 * @throws Exception
	 */
	@Test
	public void testCanFill_LimitOrder() throws Exception {
		Object fix[][] = {
			// type,      price, 	expected result 
			{ Order.BUY,  115.00d,  true  },
			{ Order.BUY,  110.00d,	true  },
			{ Order.BUY,  105.00d,	true  },
			{ Order.BUY,  100.00d,	true  },
			{ Order.BUY,   95.00d,	false },
			{ Order.SELL,  95.00d,  true  },
			{ Order.SELL, 100.00d,  true  },
			{ Order.SELL, 105.00d,  true  },
			{ Order.SELL, 110.00d,  true  },
			{ Order.SELL, 115.00d,  false },
		};
		hi.addToStackAndUpdate(110.00d);
		lo.addToStackAndUpdate(100.00d);
		for ( int i = 0; i < fix.length; i ++ ) {
			Order order = new OrderImpl(1L, (Integer)fix[i][0], 1,
					(Double)fix[i][1]);
			String msg = "At #" + i;
			assertEquals(msg, (Boolean)fix[i][2], checker.canFill(order, data));
		}
	}

}
