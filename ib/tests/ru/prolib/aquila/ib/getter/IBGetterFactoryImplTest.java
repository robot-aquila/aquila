package ru.prolib.aquila.ib.getter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.getter.GAccount;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.ib.subsys.IBServiceLocator;
import ru.prolib.aquila.ib.subsys.contract.IBContracts;

/**
 * 2012-12-15<br>
 * $Id: IBGetterFactoryImplTest.java 553 2013-03-01 13:37:31Z whirlwind $
 */
public class IBGetterFactoryImplTest {
	private static IMocksControl control;
	private static IBServiceLocator locator;
	private static IBGetterFactoryImpl factory;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		locator = control.createMock(IBServiceLocator.class);
		factory = new IBGetterFactoryImpl(locator);
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(locator, factory.getServiceLocator());
	}
	
	@Test
	public void testOpenOrderAccount() throws Exception {
		G<Account> expected = new GChain<Account>(
				new IBGetOpenOrder(),
				new GAccount(new IBGetOrderAccount()));
		assertEquals(expected, factory.openOrderAccount());
	}
	
	@Test
	public void testOpenOrderSecDescr() throws Exception {
		IBContracts contracts = control.createMock(IBContracts.class);
		expect(locator.getContracts()).andReturn(contracts);
		control.replay();
		G<SecurityDescriptor> expected = new GChain<SecurityDescriptor>(
				new IBGetOpenOrderContract(),
				new IBGetSecurityDescriptor(contracts));
		assertEquals(expected, factory.openOrderSecDescr());
		control.verify();
	}
	
	@Test
	public void testOpenOrderType() throws Exception {
		Map<String, OrderType> map = new HashMap<String, OrderType>();
		map.put("STP LMT", OrderType.STOP_LIMIT);
		map.put("MKT", OrderType.MARKET);
		map.put("LMT", OrderType.LIMIT);
		G<OrderType> expected = new GMapTR<OrderType>(
				new GChain<String>(new IBGetOpenOrder(),
								   new IBGetOrderType()), map);
		assertEquals(expected, factory.openOrderType());
	}

	@Test
	public void testOpenOrderDir() throws Exception {
		Map<String, OrderDirection> map = new HashMap<String, OrderDirection>();
		map.put("BUY", OrderDirection.BUY);
		map.put("SELL", OrderDirection.SELL);
		map.put("SSHORT", OrderDirection.SELL);
		G<OrderDirection> expected = new GMapTR<OrderDirection>(
				new GChain<String>(new IBGetOpenOrder(),
								   new IBGetOrderDir()), map);
		assertEquals(expected, factory.openOrderDir());
	}
	
	@Test
	public void testOpenOrderQty() throws Exception {
		G<Long> expected = new GChain<Long>(
				new IBGetOpenOrder(),
				new IBGetOrderQty());
		assertEquals(expected, factory.openOrderQty());
	}
	
	@Test
	public void testOpenOrderStatus() throws Exception {
		Map<String, OrderStatus> map = new HashMap<String, OrderStatus>();
		map.put("PendingSubmit", OrderStatus.PENDING);
		map.put("PreSubmitted", OrderStatus.PENDING);
		map.put("Submitted", OrderStatus.ACTIVE);
		map.put("Cancelled", OrderStatus.CANCELLED);
		map.put("Filled", OrderStatus.FILLED);
		G<OrderStatus> expected = new GMapTR<OrderStatus>(
				new GChain<String>(new IBGetOpenOrderState(),
								   new IBGetOrderStateStatus()), map);
		assertEquals(expected, factory.openOrderStatus());
	}
	
	@Test
	public void testEquals() throws Exception {
		IBServiceLocator locator2 = control.createMock(IBServiceLocator.class);
		assertTrue(factory.equals(factory));
		assertTrue(factory.equals(new IBGetterFactoryImpl(locator)));
		assertFalse(factory.equals(null));
		assertFalse(factory.equals(this));
		assertFalse(factory.equals(new IBGetterFactoryImpl(locator2)));
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder(20121217, 183657)
			.append(locator)
			.toHashCode(), factory.hashCode());
	}
	
	@Test
	public void testOrderStatusStatus() throws Exception {
		Map<String, OrderStatus> map = new HashMap<String, OrderStatus>();
		map.put("PendingSubmit", OrderStatus.PENDING);
		map.put("PreSubmitted", OrderStatus.PENDING);
		map.put("Submitted", OrderStatus.ACTIVE);
		map.put("Cancelled", OrderStatus.CANCELLED);
		map.put("Filled", OrderStatus.FILLED);
		G<OrderStatus> expected = new GMapTR<OrderStatus>(
				new IBGetOrderStatus(), map);
		assertEquals(expected, factory.orderStatusStatus());
	}
	
	@Test
	public void testOrderStatusRemaining() throws Exception {
		G<Long> expected = new IBGetOrderRestQty();
		assertEquals(expected, factory.orderStatusRemaining());
	}
	
	@Test
	public void testPortCash() throws Exception {
		assertEquals(new IBGetAccountDouble("TotalCashBalance", "BASE"),
					 factory.portCash());
	}
	
	@Test
	public void testPosCurrValue() throws Exception {
		G<Long> expected = new IBGetPositionCurrent();
		assertEquals(expected, factory.posCurrValue());
	}
	
	@Test
	public void testOrderStatusExecutedVolume() throws Exception {
		assertEquals(new IBGetOrderExecVolume(),
				factory.orderStatusExecutedVolume());
	}
	
	@Test
	public void testPortBalance() throws Exception {
		assertEquals(
				new IBGetAccountDouble("NetLiquidationByCurrency", "BASE"),
				factory.portBalance());
	}
	
	@Test
	public void testPosMarketValue() throws Exception {
		assertEquals(new IBGetPositionMktValue(), factory.posMarketValue());
	}

	@Test
	public void testPosBalanceCost() throws Exception {
		assertEquals(new IBGetPositionBalanceCost(), factory.posBalanceCost());
	}
	
	@Test
	public void testPosPL() throws Exception {
		assertEquals(new IBGetPositionVarMargin(), factory.posPL());
	}

}
