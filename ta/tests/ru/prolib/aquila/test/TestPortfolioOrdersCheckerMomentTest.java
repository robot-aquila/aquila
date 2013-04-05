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

public class TestPortfolioOrdersCheckerMomentTest {
	IMocksControl control;
	MarketDataImpl data;
	MarketDataReader dataReader;
	TestValue<Double> close;
	TestPortfolioOrdersCheckerMoment checker;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.ALL);
	}
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		close = new TestValue<Double>(MarketData.CLOSE);
		dataReader = control.createMock(MarketDataReader.class);
		data = new MarketDataImpl(dataReader);
		data.addValue(close);
		checker = new TestPortfolioOrdersCheckerMoment();
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
	 * - стоп заявка на покупку исполняется, когда close >= стоп-цене
	 * - стоп заявка на продажу исполняется, когда close <= стоп-цене
	 * 
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
			{ Order.SELL, 100.00d,    false },
			{ Order.SELL, 105.00d,    false },
			{ Order.SELL, 110.00d,    true  },
			{ Order.SELL, 115.00d,    true  },
		};
		close.addToStackAndUpdate(110.00d);
		for ( int i = 0; i < fix.length; i ++ ) {
			Order order = new OrderImpl(1L, (Integer)fix[i][0], 1,
					(Double)fix[i][1], 0.0d);
			String msg = "At #" + i;
			assertEquals(msg, (Boolean)fix[i][2], checker.canFill(order, data));
		}
	}
	
	/**
	 * Тестируются ситуации:
	 * - лимитная заявка на покупку исполняется, когда close <= цены
	 * - лимитная заявка на продажу исполняется, когда close >= цены
	 * @throws Exception
	 */
	@Test
	public void testCanFill_LimitOrder() throws Exception {
		Object fix[][] = {
			// type,      price, 	expected result 
			{ Order.BUY,  115.00d,  true  },
			{ Order.BUY,  110.00d,	true  },
			{ Order.BUY,  105.00d,	false },
			{ Order.BUY,  100.00d,	false },
			{ Order.BUY,   95.00d,	false },
			{ Order.SELL,  95.00d,  true  },
			{ Order.SELL, 100.00d,  true  },
			{ Order.SELL, 105.00d,  true  },
			{ Order.SELL, 110.00d,  true  },
			{ Order.SELL, 115.00d,  false },
		};
		close.addToStackAndUpdate(110.00d);
		for ( int i = 0; i < fix.length; i ++ ) {
			Order order = new OrderImpl(1L, (Integer)fix[i][0], 1,
					(Double)fix[i][1]);
			String msg = "At #" + i;
			assertEquals(msg, (Boolean)fix[i][2], checker.canFill(order, data));
		}
	}

}
