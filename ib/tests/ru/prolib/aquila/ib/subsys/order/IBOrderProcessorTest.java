package ru.prolib.aquila.ib.subsys.order;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import com.ib.client.Contract;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.*;
import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.utils.Counter;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.ib.getter.IBGetSecurityDescriptorContract;
import ru.prolib.aquila.ib.subsys.api.IBClient;

/**
 * 2012-12-11<br>
 * $Id: IBOrderProcessorTest.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public class IBOrderProcessorTest {
	private static IMocksControl control;
	private static IBClient client;
	private static Counter transId;
	private static IBOrderProcessor processor;
	private static BMFactory bf;
	private static EventType onNextValidId;
	private static G<Contract> gSec2Contr;
	private static SecurityDescriptor descr;
	private static EditableTerminal terminal;
	private EditableSecurity security;
	private EditableOrder order;

	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		client = control.createMock(IBClient.class);
		transId = control.createMock(Counter.class);
		gSec2Contr = control.createMock(G.class);
		processor = new IBOrderProcessor(client, transId, gSec2Contr);
		onNextValidId = control.createMock(EventType.class);
		terminal = control.createMock(EditableTerminal.class);
		bf = new BMFactoryImpl(new EventSystemImpl(), terminal);
		descr = new SecurityDescriptor("AAPL","SMART","USD",SecurityType.STK);
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
		security = bf.createSecurityFactory().createSecurity(descr);
		order = bf.createOrderFactory().createOrder();
		order.setSecurityDescriptor(descr);
		expect(client.OnNextValidId()).andStubReturn(onNextValidId);
		expect(terminal.getSecurity(eq(descr))).andStubReturn(security);
	}
	
	@Test
	public void testCancelOrder() throws Exception {
		order.setId(200L);
		client.cancelOrder(200);
		control.replay();
		processor.cancelOrder(order);
		control.verify();
	}
	
	@Test
	public void testPlaceOrder_MarketBuy() throws Exception {
		order.setQty(10L);
		order.setDirection(OrderDirection.BUY);
		order.setType(OrderType.MARKET);
		order.setTransactionId(150L);
		
		Contract contract = new Contract();
		expect(gSec2Contr.get(same(descr))).andReturn(contract);

		com.ib.client.Order ibo = new com.ib.client.Order();
		ibo.m_action = "BUY";
		ibo.m_totalQuantity = 10;
		ibo.m_orderType = "MKT";
		client.placeOrder(eq(150), same(contract), eq(ibo));
		control.replay();
		processor.placeOrder(order);
		control.verify();
	}
	
	@Test
	public void testPlaceOrder_MarketSell() throws Exception {
		order.setQty(100L);
		order.setDirection(OrderDirection.SELL);
		order.setType(OrderType.MARKET);
		order.setTransactionId(50L);
		
		Contract contract = new Contract();
		expect(gSec2Contr.get(same(descr))).andReturn(contract);

		com.ib.client.Order ibo = new com.ib.client.Order();
		ibo.m_action = "SELL";
		ibo.m_totalQuantity = 50;
		ibo.m_orderType = "MKT";
		client.placeOrder(eq(50), same(contract), eq(ibo));
		control.replay();
		processor.placeOrder(order);
		control.verify();
	}

	@Test (expected=OrderException.class)
	public void testPlaceOrder_LimitUnsupported() throws Exception {
		order.setType(OrderType.LIMIT);
		control.replay();
		processor.placeOrder(order);
	}

	@Test (expected=OrderException.class)
	public void testPlaceOrder_StopLimitUnsupported() throws Exception {
		order.setType(OrderType.STOP_LIMIT);
		control.replay();
		processor.placeOrder(order);
	}
	
	@Test (expected=OrderException.class)
	public void testPlaceOrder_TakeProfitAndStopLimitUnsupported()
		throws Exception
	{
		order.setType(OrderType.TAKE_PROFIT_AND_STOP_LIMIT);
		control.replay();
		processor.placeOrder(order);
	}
	
	@Test (expected=OrderException.class)
	public void testPlaceOrder_TakeProfitUnsupported() throws Exception {
		order.setType(OrderType.TAKE_PROFIT);
		control.replay();
		processor.placeOrder(order);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testEquals() throws Exception {
		Variant<IBClient> vClient = new Variant<IBClient>()
			.add(client)
			.add(control.createMock(IBClient.class));
		Variant<Counter> vTransId = new Variant<Counter>(vClient)
			.add(transId)
			.add(control.createMock(Counter.class));
		Variant<G<Contract>> vS2C = new Variant<G<Contract>>(vTransId)
			.add(gSec2Contr)
			.add(control.createMock(G.class));
		Variant<?> iterator = vS2C;
		int foundCnt = 0;
		IBOrderProcessor x = null, found = null;
		do {
			x = new IBOrderProcessor(vClient.get(), vTransId.get(), vS2C.get());
			if ( processor.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(client, found.getClient());
		assertSame(transId, found.getTransIdCounter());
		assertSame(gSec2Contr, found.getSecDescr2ContractConverter());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(processor.equals(processor));
		assertFalse(processor.equals(null));
		assertFalse(processor.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121215, 164921)
			.append(client)
			.append(transId)
			.append(gSec2Contr)
			.toHashCode();
		assertEquals(hashCode, processor.hashCode());
	}
	
	@Test
	public void testConstruct2() throws Exception {
		IBOrderProcessor expected = new IBOrderProcessor(client, transId,
				new IBGetSecurityDescriptorContract());
		assertEquals(expected, new IBOrderProcessor(client, transId));
	}

}
