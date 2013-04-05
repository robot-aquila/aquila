package ru.prolib.aquila.ChaosTheory;

import org.junit.*;
import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class PortfolioDriverTest {
	private AssetImpl asset;
	private IMocksControl control;
	private Portfolio port;
	private RiskManager rm;
	private PortfolioDriver drv;
	private PortfolioDriverEmergClosePosition closeShort,closeLong;
	
	@BeforeClass
	static public void setUpBeforeClass() throws Exception {
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		asset = new AssetImpl("RTS", "SPBFUT", 5, 0);
		control = createStrictControl();
		port = control.createMock(Portfolio.class);
		rm = control.createMock(RiskManager.class);
		closeShort = control.createMock(PortfolioDriverEmergClosePosition.class);
		closeLong = control.createMock(PortfolioDriverEmergClosePosition.class);
		drv = new PortfolioDriver(port, asset, rm, closeLong, closeShort);
	}
	
	@Test
	public void testAccessors() {
		assertSame(port, drv.getPortfolio());
		assertSame(asset, drv.getAsset());
		assertSame(rm, drv.getRiskManagement());
	}
	
	/**
	 * Тестируются ситуации:
	 * - нет заявки -> null
	 * - возврат ожидающей заявки
	 * - возврат активной заявки
	 * - заявка исполнена, возврат связаной активной заявки
	 * - заявка исполнена, связаная заявка исполнена -> null
	 * - заявка исполнена, связаная заявка снята -> null
	 * - заявка снята -> null
	 * - заявка исполнена -> null
	 * @throws Exception
	 */
	@Test
	public void testGetBuy() throws Exception {
		drv.buy = null;
		assertNull(drv.getBuy());
		
		drv.buy = new OrderImpl(123, Order.BUY, 10, 25.90d, 25.95d);
		assertSame(drv.buy, drv.getBuy());
	
		drv.buy.activate();
		assertSame(drv.buy, drv.getBuy());
		
		drv.buy.fill(555L);
		assertSame(drv.buy.getRelatedOrder(), drv.getBuy());
		
		drv.buy.getRelatedOrder().fill();
		assertNull(drv.getBuy());
		
		drv.buy = new OrderImpl(123, Order.BUY, 10, 25.90d, 25.95d);
		drv.buy.activate();
		drv.buy.fill(555L);
		drv.buy.getRelatedOrder().kill();
		assertNull(drv.getBuy());
		
		drv.buy = new OrderImpl(123, Order.BUY, 10, 25.90d);
		drv.buy.activate();
		drv.buy.fill();
		assertNull(drv.getBuy());
		
		drv.buy = new OrderImpl(123, Order.BUY, 10, 25.90d);
		drv.buy.activate();
		drv.buy.kill();
		assertNull(drv.getBuy());
	}

	/**
	 * Тестируются ситуации:
	 * - нет заявки -> null
	 * - возврат ожидающей заявки
	 * - возврат активной заявки
	 * - заявка исполнена, возврат связаной активной заявки
	 * - заявка исполнена, связаная заявка исполнена -> null
	 * - заявка исполнена, связаная заявка снята -> null
	 * - заявка снята -> null
	 * - заявка исполнена -> null
	 * @throws Exception
	 */
	@Test
	public void testGetSell() throws Exception {
		drv.sell = null;
		assertNull(drv.getSell());
		
		drv.sell = new OrderImpl(123, Order.SELL, 10, 26.00d, 25.00d);
		assertSame(drv.sell, drv.getSell());
		
		drv.sell.activate();
		assertSame(drv.sell, drv.getSell());
		
		drv.sell.fill(321L);
		assertSame(drv.sell.getRelatedOrder(), drv.getSell());
		
		drv.sell.getRelatedOrder().fill();
		assertNull(drv.getSell());
		
		drv.sell = new OrderImpl(123, Order.SELL, 10, 26.00d, 25.00d);
		drv.sell.activate();
		drv.sell.fill(322L);
		drv.sell.getRelatedOrder().kill();
		assertNull(drv.getSell());
		
		drv.sell = new OrderImpl(123, Order.SELL, 1, 26.00d);
		drv.sell.activate();
		drv.sell.kill();
		assertNull(drv.getSell());
		
		drv.sell = new OrderImpl(123, Order.SELL, 1, 26.00d);
		drv.sell.activate();
		drv.sell.fill();
		assertNull(drv.getSell());		
	}
	
	/**
	 * Тестируются ситуации:
	 * - подача команды на снятие всех заявок
	 * - активные заявки обнуляются
	 * @throws Exception
	 */
	@Test
	public void testKillAll_Ok() throws Exception {
		drv.buy = new OrderImpl(1L, Order.BUY, 1, 100.00d);
		drv.sell = new OrderImpl(2L, Order.SELL, 1, 100.00d);
		port.killAll();
		control.replay();
		
		drv.killAll();
		
		control.verify();
		assertNull(drv.buy);
		assertNull(drv.sell);
	}
	
	/**
	 * Тестируются ситуации:
	 * - проброс исключения портфеля
	 * @throws Exception
	 */
	@Test
	public void testKillAll_ThrowsIfPortfolioThrows() throws Exception {
		PortfolioException e = new PortfolioException("Test exception");
		port.killAll();
		expectLastCall().andThrow(e);
		control.replay();
		
		try {
			drv.killAll();
			fail("Expected exception: " + PortfolioDriverException.class.getName());
		} catch ( PortfolioDriverException e1 ) {
			assertSame(e, e1.getCause());
		}
		
		control.verify();
	}
	
	/**
	 * Тестируются ситуации:
	 * - подача команды на святие всех заявок на покупку
	 * - активная заявка на покупку обнуляется, а на продажу остается
	 * @throws Exception
	 */
	@Test
	public void testKillBuy_Ok() throws Exception {
		drv.buy = new OrderImpl(1L, Order.BUY, 1, 100.00d);
		Order order = drv.sell = new OrderImpl(2L, Order.SELL, 1, 100.00d);
		port.killAll(Order.BUY);
		control.replay();
		
		drv.killBuy();
		
		control.verify();
		assertNull(drv.buy);
		assertSame(order, drv.sell);
	}
	
	/**
	 * Тестируются ситуации:
	 * - проброс исключения портфеля
	 * @throws Exception
	 */
	@Test
	public void testKillBuy_ThrowsIfPortfolioThrows() throws Exception {
		PortfolioException e = new PortfolioException("Test exception");
		port.killAll(Order.BUY);
		expectLastCall().andThrow(e);
		control.replay();
		
		try {
			drv.killBuy();
			fail("Expected exception: " + PortfolioDriverException.class.getName());
		} catch ( PortfolioDriverException e1 ) {
			assertSame(e, e1.getCause());
		}
		
		control.verify();
	}
	
	/**
	 * Тестируются ситуации:
	 * - подача команды на снятие всех заявок на продажу
	 * - активная заявка на продажу обнуляется, а на покупку остается
	 * @throws Exception
	 */
	@Test
	public void testKillSell_Ok() throws Exception {
		drv.sell = new OrderImpl(1L, Order.SELL, 1, 100.00d);
		Order order = drv.buy = new OrderImpl(2L, Order.BUY, 1, 100.00d); 
		port.killAll(Order.SELL);
		control.replay();
		
		drv.killSell();
		
		control.verify();
		assertNull(drv.sell);
		assertSame(order, drv.buy);
	}
	
	/**
	 * Тестируются ситуации:
	 * - проброс исключения портфеля
	 * @throws Exception
	 */
	@Test
	public void testKillSell_ThrowsIfPortfolioThrows() throws Exception {
		PortfolioException e = new PortfolioException("Test error");
		port.killAll(Order.SELL);
		expectLastCall().andThrow(e);
		control.replay();

		try {
			drv.killSell();
			fail("Expected exception: " + PortfolioDriverException.class.getName());
		} catch ( PortfolioDriverException e1 ) {
			assertSame(e, e1.getCause());
		}
		
		control.verify();
	}
	
	/**
	 * Тестируются ситуации:
	 * - все заявки на покупку снимаются
	 * - размер заявки запрашивается у риск-менеджера
	 * - выставляется стоп-заявка на покупку
	 * - активнаяя заявка сохраняется
	 * @throws Exception
	 */
	@Test
	public void testAddLong_Ok() throws Exception {
		OrderImpl order = new OrderImpl(123L, Order.BUY, 10, 25.90d, 25.95d, "foobar");
		expect(rm.getLongSize(eq(12.34d))).andReturn(10);
		port.killAll(Order.BUY);
		expect(port.stopBuy(eq(10), eq(7.34d), eq(12.34d), eq("foobar")))
			.andReturn(order);
		control.replay();
		
		drv.addLong(12.34d, "foobar");

		control.verify();
		assertSame(order, drv.buy);
	}

	/**
	 * Тестируются ситуации:
	 * - при рассчете нулевой позиции заявка не выставляется
	 * @throws Exception
	 */
	@Test
	public void testAddLong_SkipIfZeroSize() throws Exception {
		expect(rm.getLongSize(eq(12.34d))).andReturn(0);
		port.killAll(Order.BUY);
		control.replay();
		
		drv.addLong(12.34d, "foobar");
		
		control.verify();
	}
	
	/**
	 * Тестируются ситуации:
	 * - при исключении портфеля повторная команда на снятие заявок на покупку 
	 * @throws Exception
	 */
	@Test
	public void testAddLong_IgnoreException() throws Exception {
		expect(rm.getLongSize(eq(12.34d))).andReturn(1);
		port.killAll(Order.BUY);
		expect(port.stopBuy(eq(1), eq(7.34d), eq(12.34), eq("foobar")))
			.andThrow(new PortfolioException("Test error"));
		port.killAll(Order.BUY);
		control.replay();
		
		drv.addLong(12.34d, "foobar");
		
		control.verify();
	}
	
	/**
	 * Тестируются ситуации:
	 * - при наличии активной заявки с аналогичными параметрами, заявка
	 * не заменяется
	 * @throws Exception
	 */
	@Test
	public void testAddLong_SkipOrderIfEqualsOrderActive() throws Exception {
		// order prices rouned by assets price step
		drv.buy = new OrderImpl(123L, Order.BUY, 1, 5.00d, 10.00d, "zulu");
		drv.buy.activate();
		expect(rm.getLongSize(12.34d)).andReturn(1);
		control.replay();
		
		drv.addLong(12.34d, "zulu");
		
		control.verify();
	}

	
	/**
	 * Тестируются ситуации:
	 * - все заявки на продажу снимаются
	 * - выставляется стоп-заявка на продажу по текущему количеству
	 * @throws Exception
	 */
	@Test
	public void testCloseLong_Ok() throws Exception {
		OrderImpl order = new OrderImpl(12345L, Order.SELL, 10, 100.00d);
		expect(port.getPosition()).andReturn(10);
		port.killAll(Order.SELL);
		expect(port.stopSell(eq(10), eq(105.00d), eq(100.00d), eq("foobar")))
			.andReturn(order);
		control.replay();
		
		drv.closeLong(100.00d, "foobar");
		
		control.verify();
		assertSame(order, drv.sell);
	}
	
	/**
	 * Тестируются ситуации:
	 * - проброс исключения портфеля
	 * @throws Exception
	 */
	@Test
	public void testCloseLong_ThrowsIfPortfolioThrows() throws Exception {
		PortfolioException e = new PortfolioException("Error");
		expect(port.getPosition()).andReturn(10);
		port.killAll(Order.SELL);
		expect(port.stopSell(eq(10), eq(105.00d), eq(100.00d), eq("foobar")))
			.andThrow(e);
		control.replay();
		
		try {
			drv.closeLong(100.00d, "foobar");
			fail("Expected exception: " + PortfolioDriverException.class.getName());
		} catch ( PortfolioDriverException e1 ) {
			assertSame(e, e1.getCause());
		}

		control.verify();
	}

	/**
	 * Тестируются ситуации:
	 * - все заявки на продажу снимаются
	 * - размер заявки запрашивается у риск менеджера
	 * - выставляется стоп-заявка на продажу
	 * - активная заявка сохраняется
	 * @throws Exception
	 */
	@Test
	public void testAddShort_Ok() throws Exception {
		OrderImpl order = new OrderImpl(321L, Order.SELL, 5, 29.56d, 34.56d, "foobar");
		expect(rm.getShortSize(35.55d)).andReturn(333);
		port.killAll(Order.SELL);
		expect(port.stopSell(eq(333), eq(40.55d), eq(35.55d), eq("foobar")))
			.andReturn(order);
		control.replay();
		
		drv.addShort(35.55d, "foobar");
		
		control.verify();
		assertSame(order, drv.sell);
	}
	
	/**
	 * Тестируются ситуации:
	 * - при расчете нулевой позиции заявка не выставляется
	 * @throws Exception
	 */
	@Test
	public void testAddShort_SkipOrderIfZeroSize() throws Exception {
		expect(rm.getShortSize(35.55d)).andReturn(0);
		port.killAll(Order.SELL);
		control.replay();
		
		drv.addShort(35.55d, "foobar");

		control.verify();
		assertNull(drv.sell);
	}

	/**
	 * Тестируются ситуации:
	 * - при исключения портфеля повторная команда на снятие заявок на продажу
	 * @throws Exception
	 */
	@Test
	public void testAddShort_IgnoreException() throws Exception {
		expect(rm.getShortSize(35.55d)).andReturn(333);
		port.killAll(Order.SELL);
		expect(port.stopSell(eq(333), eq(40.55d), eq(35.55d), eq("foobar")))
			.andThrow(new PortfolioException("Test error"));
		port.killAll(Order.SELL);
		control.replay();
		
		drv.addShort(35.55d, "foobar");
		
		control.verify();
	}
	
	/**
	 * Тестируются ситуации:
	 * - при наличии активной заявки с аналогичными параметрами, заявка
	 * не заменяется
	 * @throws Exception
	 */
	@Test
	public void testAddShort_SkipOrderIfEqualsOrderActive() throws Exception {
		// order prices rouned by assets price step
		drv.sell = new OrderImpl(123L, Order.SELL, 5, 40.00d, 35.00d, "foo");
		expect(rm.getShortSize(35.55d)).andReturn(5);
		control.replay();
		
		drv.addShort(35.55d, "foo");
		
		control.verify();
	}
	
	/**
	 * Тестируются ситуации:
	 * - все заявки на покупку снимаются
	 * - выставляется стоп-заявка на покупку по текущей позиции
	 * @throws Exception
	 */
	@Test
	public void testCloseShort_Ok() throws Exception {
		OrderImpl order = new OrderImpl(321, Order.BUY, 10, 30.55d, 35.55d, "foobar");
		expect(port.getPosition()).andReturn(-15);
		port.killAll(Order.BUY);
		expect(port.stopBuy(eq(15), eq(31.22d), eq(36.22d), eq("close short")))
			.andReturn(order);
		control.replay();
		
		drv.closeShort(36.22d, "close short");
		
		control.verify();
		assertSame(order, drv.buy);
	}
	
	/**
	 * Тестируются ситуации:
	 * - проброс исключения портфеля
	 * @throws Exception
	 */
	@Test
	public void testCloseShort_ThrowsIfPortfolioThrows() throws Exception {
		PortfolioException e = new PortfolioException("Error");
		expect(port.getPosition()).andReturn(-15);
		port.killAll(Order.BUY);
		expect(port.stopBuy(eq(15), eq(31.22d), eq(36.22d), eq("close short")))
			.andThrow(e);
		control.replay();

		try {
			drv.closeShort(36.22d, "close short");
			fail("Expected exception: " + PortfolioDriverException.class.getName());
		} catch ( PortfolioDriverException e1 ) {
			assertSame(e, e1.getCause());
		}
		
		control.verify();
	}
	
	@Test
	public void testIsLong() throws Exception {
		Object fixture[][] = {
			// qty, expected result
			{  1, true },
			{ 10, true },
			{-10, false},
			{  0, false},
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			control = createStrictControl();
			port = control.createMock(Portfolio.class);
			expect(port.getPosition()).andReturn((Integer)fixture[i][0]);
			control.replay();
			
			drv = new PortfolioDriver(port, asset, null, null, null);
			assertEquals((Boolean)fixture[i][1], drv.isLong());
			
			control.verify();
		}
	}
	
	@Test
	public void testIsShort() throws Exception {
		Object fixture[][] = {
			// qty, expected result
			{  1, false},
			{ 10, false},
			{-10, true },
			{  0, false},
			{ -1, true },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			control = createStrictControl();
			port = control.createMock(Portfolio.class);
			expect(port.getPosition()).andReturn((Integer)fixture[i][0]);
			control.replay();
			
			drv = new PortfolioDriver(port, asset, null, null, null);
			assertEquals((Boolean)fixture[i][1], drv.isShort());
			
			control.verify();
		}
	}
	
	@Test
	public void testIsNeutral() throws Exception {
		Object fixture[][] = {
			// qty, expected result
			{  1, false},
			{ 10, false},
			{-10, false},
			{  0, true },
			{ -1, false},
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			control = createStrictControl();
			port = control.createMock(Portfolio.class);
			expect(port.getPosition()).andReturn((Integer)fixture[i][0]);
			control.replay();
			
			drv = new PortfolioDriver(port, asset, null, null, null);
			assertEquals((Boolean)fixture[i][1], drv.isNeutral());
			
			control.verify();
		}
	}
	
	/**
	 * Тестируются ситуации:
 	 * - выполняются попытки закрыть короткую, пока закрыватор не скажет ок
	 * @throws Exception
	 */
	@Test
	public void testCloseShortImmediately_Ok() throws Exception {
		for ( int i = 1; i < PortfolioDriver.CLOSE_IMMEDIATELY_RETRIES; i++ ) {
			expect(closeShort.tryClose(i, "foobar")).andReturn(false);
		}
		expect(closeShort.tryClose(PortfolioDriver.CLOSE_IMMEDIATELY_RETRIES,
				"foobar"))
			.andReturn(true);
		control.replay();
		
		drv.closeShortImmediately("foobar");
		
		control.verify();
	}
	
	/**
	 * Тестируются ситуации:
	 * - ошибки портфеля пробрасываемые через стратегию не игнорируются
	 * @throws Exception
	 */
	@Test
	public void testCloseShortImmediately_ThrowsExceptions() throws Exception {
		PortfolioException e = new PortfolioException("Unknown exception");
		expect(closeShort.tryClose(1, "foobar")).andThrow(e);
		control.replay();

		try {
			drv.closeShortImmediately("foobar");
			fail("Expected exception: " + PortfolioDriverException.class);
		} catch ( PortfolioDriverException e1 ) {
			assertSame(e, e1.getCause());
		}
		
		control.verify();
	}
	
	/**
	 * Тестируются ситуации:
	 * - исключение драйвера в случае максимального кол-ва попыток
	 * @throws Exception
	 */
	@Test
	public void testCloseShortImmediately_MaxRetries() throws Exception {
		for ( int i = 1; i <= PortfolioDriver.CLOSE_IMMEDIATELY_RETRIES; i++ ) {
			expect(closeShort.tryClose(i, "zulu charlie")).andReturn(false);
		}
		control.replay();
		
		try {
			drv.closeShortImmediately("zulu charlie");
			fail("Expected exception: " + PortfolioDriverException.class);
		} catch ( PortfolioDriverException e ) {
			assertEquals("Max retries", e.getMessage());
		}
		
		control.verify();
	}

	/**
	 * Тестируются ситуации:
	 * - выполняются попытки закрыть длинную, пока закрыватор не скажет ок
	 * @throws Exception
	 */
	@Test
	public void testCloseLongImmediately_Ok() throws Exception {
		for ( int i = 1; i < PortfolioDriver.CLOSE_IMMEDIATELY_RETRIES; i++ ) {
			expect(closeLong.tryClose(i, "foobar")).andReturn(false);
		}
		expect(closeLong.tryClose(PortfolioDriver.CLOSE_IMMEDIATELY_RETRIES,
				"foobar"))
			.andReturn(true);
		control.replay();
		
		drv.closeLongImmediately("foobar");
		
		control.verify();
	}
	
	/**
	 * Тестируются ситуации:
	 * - ошибки портфеля не игнорируются
	 * @throws Exception
	 */
	@Test
	public void testCloseLongImmediately_ThrowsException() throws Exception {
		PortfolioException e = new PortfolioException("Unknown exception");
		expect(closeLong.tryClose(1, "foobar")).andThrow(e);
		control.replay();

		try {
			drv.closeLongImmediately("foobar");
			fail("Expected exception: " + PortfolioDriverException.class);
		} catch ( PortfolioDriverException e1 ) {
			assertSame(e, e1.getCause());
		}
		
		control.verify();
	}

	/**
	 * Тестируются ситуации:
	 * - исключение драйвера в случае максимального кол-ва попыток
	 * @throws Exception
	 */
	@Test
	public void testCloseLongImmediately_MaxRetries() throws Exception {
		for ( int i = 1; i <= PortfolioDriver.CLOSE_IMMEDIATELY_RETRIES; i++ ) {
			expect(closeLong.tryClose(i, "zulu charlie")).andReturn(false);
		}
		control.replay();
		
		try {
			drv.closeLongImmediately("zulu charlie");
			fail("Expected exception: " + PortfolioDriverException.class);
		} catch ( PortfolioDriverException e ) {
			assertEquals("Max retries", e.getMessage());
		}
		
		control.verify();
	}

}
