package ru.prolib.aquila.ta.ds.quik;

import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.ChaosTheory.AssetImpl;
import ru.prolib.aquila.ChaosTheory.Order;
import ru.prolib.aquila.ChaosTheory.OrderImpl;
import ru.prolib.aquila.ChaosTheory.PortfolioException;
import ru.prolib.aquila.ChaosTheory.PortfolioOrders;
import ru.prolib.aquila.ChaosTheory.PortfolioState;

public class PortfolioQuikTest {
	Tr2Quik tr;
	PortfolioState state;
	PortfolioOrders orders;
	PortfolioQuik port;
	IMocksControl control;
	AssetImpl asset;
	
	@BeforeClass
	static public void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		//Logger.getRootLogger().setLevel(Level.ALL);
		Logger.getRootLogger().setLevel(Level.INFO);
	}

	@Before
	public void setUp() throws Exception {
		asset = new AssetImpl("RIZ1", "SPBFUT", 1, 0);
		control = createStrictControl();
		tr = control.createMock(Tr2Quik.class);
		state = control.createMock(PortfolioState.class);
		orders = control.createMock(PortfolioOrders.class);
		port = new PortfolioQuik("KJZ00", asset, tr, state, orders);
	}
	
	@Test
	public void testGetAsset() {
		assertSame(asset, port.getAsset());
	}
	
	@Test
	public void testGetMoney() throws Exception {
		expect(state.getMoney()).andReturn(100.05d);
		control.replay();
		
		assertEquals(100.05d, port.getMoney(), 0.01d);
		
		control.verify();
	}
	
	@Test
	public void testGetPosition() throws Exception {
		expect(state.getPosition()).andReturn(100);
		control.replay();
		
		assertEquals(100, port.getPosition());
		
		control.verify();
	}
	
	@Test
	public void testKill_SkipIfKilled() throws Exception {
		Order order = new OrderImpl(100L, Order.BUY, 1, 25.00d);
		order.activate();
		order.kill();
		control.replay();
		
		port.kill(order);
		
		control.verify();
	}
	
	@Test
	public void testKill_SkipIfFilled() throws Exception {
		Order order = new OrderImpl(200L, Order.SELL, 1, 100.25d);
		order.activate();
		order.fill();
		control.replay();
		
		port.kill(order);
		
		control.verify();
	}
	
	@Test
	public void testKill_MarketOrLimitOrder() throws Exception {
		Order order = new OrderImpl(12345L, Order.BUY, 1, 100.05d);
		expect(tr.transaction("ACTION=KILL_ORDER;ORDER_KEY=12345;" +
				"ACCOUNT=KJZ00;CLASSCODE=SPBFUT;SECCODE=RIZ1", 0))
		    .andReturn(null);
		control.replay();
		
		port.kill(order);
		
		control.verify();
	}
	
	@Test
	public void testKill_StopOrder() throws Exception {
		Order order = new OrderImpl(54321L, Order.SELL, 20, 98.13d, 96.80d);
		expect(tr.transaction("ACTION=KILL_STOP_ORDER;ORDER_KEY=54321;" +
				"ACCOUNT=KJZ00;CLASSCODE=SPBFUT;SECCODE=RIZ1", 0))
			.andReturn(null);
		control.replay();
		
		port.kill(order);
		
		control.verify();
	}
	
	@Test
	public void testKillAll1() throws Exception {
		expect(tr.transaction("ACTION=KILL_ALL_STOP_ORDERS;ACCOUNT=KJZ00;" +
				"CLASSCODE=SPBFUT;SECCODE=RIZ1", 0))
			.andReturn(null);
		expect(tr.transaction("ACTION=KILL_ALL_ORDERS;ACCOUNT=KJZ00;" +
				"CLASSCODE=SPBFUT;SECCODE=RIZ1", 0))
            .andReturn(null);
		control.replay();
		
		port.killAll();
		
		control.verify();
	}

	@Test
	public void testKillAll2() throws Exception {
		expect(tr.transaction("ACTION=KILL_ALL_STOP_ORDERS;OPERATION=B;" +
				"ACCOUNT=KJZ00;CLASSCODE=SPBFUT;SECCODE=RIZ1", 0))
			.andReturn(null);
		expect(tr.transaction("ACTION=KILL_ALL_ORDERS;OPERATION=B;" + "" +
				"ACCOUNT=KJZ00;CLASSCODE=SPBFUT;SECCODE=RIZ1", 0))
			.andReturn(null);
		
		expect(tr.transaction("ACTION=KILL_ALL_STOP_ORDERS;OPERATION=S;" +
				"ACCOUNT=KJZ00;CLASSCODE=SPBFUT;SECCODE=RIZ1", 0))
			.andReturn(null);
		expect(tr.transaction("ACTION=KILL_ALL_ORDERS;OPERATION=S;" +
				"ACCOUNT=KJZ00;CLASSCODE=SPBFUT;SECCODE=RIZ1", 0))
			.andReturn(null);

		control.replay();
		
		port.killAll(Order.BUY);
		port.killAll(Order.SELL);
		
		control.verify();
	}

	@Test
	public void testLimitBuy2_Ok() throws Exception {
		OrderImpl order = new OrderImpl(888L, Order.BUY, 1000, 250.00d);
		expect(tr.transaction("ACTION=NEW_ORDER;OPERATION=B;PRICE=250;" +
			"QUANTITY=1000;ACCOUNT=KJZ00;CLASSCODE=SPBFUT;SECCODE=RIZ1", 3))
			.andReturn(new Tr2QuikResult(0, 3, "", 888L));
		orders.startWatch(eq(order));
		control.replay();
		
		Order resultOrder = port.limitBuy(1000, 250.00d);
		
		control.verify();
		assertNotNull(resultOrder);
		assertEquals(order, resultOrder);
	}
	
	@Test
	public void testLimitBuy2_ThrowsIfNoOrderNumber() throws Exception {
		expect(tr.transaction("ACTION=NEW_ORDER;OPERATION=B;PRICE=250;" +
			"QUANTITY=1000;ACCOUNT=KJZ00;CLASSCODE=SPBFUT;SECCODE=RIZ1", 3))
			.andReturn(new Tr2QuikResult(1, 3, "", 0L));
		control.replay();
		
		try {
			port.limitBuy(1000, 250.00d);
			fail("Expected exception: " + PortfolioException.class);
		} catch ( PortfolioException e ) {
			assertEquals("Order number expected for trans: 1", e.getMessage());
		}
		
		control.verify();
	}
	
	@Test
	public void testLimitBuy3_Ok() throws Exception {
		OrderImpl order = new OrderImpl(987L, Order.BUY, 10, 210.00d, "foobar");
		expect(tr.transaction("ACTION=NEW_ORDER;OPERATION=B;PRICE=210;" +
			"QUANTITY=10;ACCOUNT=KJZ00;CLASSCODE=SPBFUT;SECCODE=RIZ1", 3))
			.andReturn(new Tr2QuikResult(0, 3, "", 987));
		orders.startWatch(eq(order));
		control.replay();
		
		Order resultOrder = port.limitBuy(10, 210d, "foobar");
		
		control.verify();
		assertNotNull(resultOrder);
		assertEquals(order, resultOrder);
	}
	
	@Test
	public void testLimitSell2_Ok() throws Exception {
		OrderImpl order = new OrderImpl(999L, Order.SELL, 100, 180.05d);
		expect(tr.transaction("ACTION=NEW_ORDER;OPERATION=S;PRICE=180;" +
				"QUANTITY=100;ACCOUNT=KJZ00;CLASSCODE=SPBFUT;SECCODE=RIZ1", 3))
			.andReturn(new Tr2QuikResult(1, 3, "", 999L));
		orders.startWatch(eq(order));
		control.replay();
		
		Order resultOrder = port.limitSell(100, 180.05d);
		
		control.verify();
		assertNotNull(resultOrder);
		assertEquals(order, resultOrder);
	}
	
	@Test
	public void testLimitSell3_Ok() throws Exception {
		OrderImpl order = new OrderImpl(123L, Order.SELL, 1, 120.05d, "zulu4");
		expect(tr.transaction("ACTION=NEW_ORDER;OPERATION=S;PRICE=120;" +
				"QUANTITY=1;ACCOUNT=KJZ00;CLASSCODE=SPBFUT;SECCODE=RIZ1", 3))
			.andReturn(new Tr2QuikResult(0, 3, "", 123L));
		orders.startWatch(eq(order));
		control.replay();
		
		Order resultOrder = port.limitSell(1, 120.05d, "zulu4");
		
		control.verify();
		assertNotNull(resultOrder);
		assertEquals(order, resultOrder);
	}
	
	@Test
	public void testStopBuy1() throws Exception {
		OrderImpl order = new OrderImpl(123L, Order.BUY, 1, 25.00d, 26.00d);
		expect(tr.transaction("ACTION=NEW_STOP_ORDER;OPERATION=B;" +
				"STOPPRICE=25;PRICE=26;QUANTITY=1;ACCOUNT=KJZ00;" +
				"CLASSCODE=SPBFUT;SECCODE=RIZ1", 3))
			.andReturn(new Tr2QuikResult(1, 3, "", 123L));
		orders.startWatch(eq(order));
		control.replay();
		
		Order resultOrder = port.stopBuy(1, 25.00d, 26.00d);
		
		control.verify();
		assertNotNull(resultOrder);
		assertEquals(order, resultOrder);
	}
	
	@Test
	public void testStopBuy2() throws Exception {
		OrderImpl order = new OrderImpl(123L, Order.BUY, 1, 25d, 26d, "foobar");
		expect(tr.transaction("ACTION=NEW_STOP_ORDER;OPERATION=B;" +
				"STOPPRICE=25;PRICE=26;QUANTITY=1;ACCOUNT=KJZ00;" +
				"CLASSCODE=SPBFUT;SECCODE=RIZ1", 3))
			.andReturn(new Tr2QuikResult(1, 3, "", 123L));
		orders.startWatch(eq(order));
		control.replay();
		
		Order resultOrder = port.stopBuy(1, 25.00d, 26.00d, "foobar");
		
		control.verify();
		assertNotNull(resultOrder);
		assertEquals(order, resultOrder);
	}
	
	@Test
	public void testStopSell1() throws Exception {
		OrderImpl order = new OrderImpl(876L, Order.SELL, 5, 18.0d, 17.0d);
		expect(tr.transaction("ACTION=NEW_STOP_ORDER;OPERATION=S;" +
				"STOPPRICE=18;PRICE=17;QUANTITY=5;ACCOUNT=KJZ00;" +
				"CLASSCODE=SPBFUT;SECCODE=RIZ1", 3))
			.andReturn(new Tr2QuikResult(1, 3, "", 876L));
		orders.startWatch(eq(order));
		control.replay();
		
		Order resultOrder = port.stopSell(5, 18.0d, 17.0d);
		
		control.verify();
		assertNotNull(resultOrder);
		assertEquals(order, resultOrder);
	}
	
	@Test
	public void testStopSell2() throws Exception {
		OrderImpl order = new OrderImpl(876L, Order.SELL, 5, 18.0d, 17.0d, "foobar");
		expect(tr.transaction("ACTION=NEW_STOP_ORDER;OPERATION=S;" +
				"STOPPRICE=18;PRICE=17;QUANTITY=5;ACCOUNT=KJZ00;" +
				"CLASSCODE=SPBFUT;SECCODE=RIZ1", 3))
			.andReturn(new Tr2QuikResult(1, 3, "", 876L));
		orders.startWatch(eq(order));
		control.replay();
		
		Order resultOrder = port.stopSell(5, 18.0d, 17.0d, "foobar");
		
		control.verify();
		assertNotNull(resultOrder);
		assertEquals(order, resultOrder);
	}

	@Test
	public void testWaitForComplete2_Ok() throws Exception {
		Order order = new OrderImpl(123L, Order.BUY, 1, 100.00d);
		orders.waitForComplete(order, 700);
		control.replay();
		
		port.waitForComplete(order, 700);
		
		control.verify();
	}
	
	@Test
	public void testWaitForNeutralPosition2_Ok() throws Exception {
		state.waitForNeutralPosition(1000);
		control.replay();
		
		port.waitForNeutralPosition(1000);
		
		control.verify();
	}

}
