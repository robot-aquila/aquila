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
	private static SecurityDescriptor descr;
	private IMocksControl control;
	private IBClient client;
	private Counter transNumerator;
	private IBOrderProcessor processor;
	private EventType onNextValidId;
	private G<Contract> gSec2Contr;
	private EditableTerminal terminal;
	private EditableSecurity security;
	private EditableOrder order;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		descr = new SecurityDescriptor("AAPL","SMART","USD",SecurityType.STK);
	}

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		client = control.createMock(IBClient.class);
		transNumerator = control.createMock(Counter.class);
		gSec2Contr = control.createMock(G.class);
		security = control.createMock(EditableSecurity.class);
		onNextValidId = control.createMock(EventType.class);
		terminal = control.createMock(EditableTerminal.class);		
		order = control.createMock(EditableOrder.class);
		processor = new IBOrderProcessor(terminal, client, transNumerator,
				gSec2Contr);
		
		expect(client.OnNextValidId()).andStubReturn(onNextValidId);
		expect(security.getDescriptor()).andStubReturn(descr);
		expect(order.getSecurity()).andStubReturn(security);
	}
	
	@Test
	public void testCancelOrder() throws Exception {
		expect(order.getId()).andStubReturn(200L);
		client.cancelOrder(200);
		control.replay();
		
		processor.cancelOrder(order);
		
		control.verify();
	}
	
	@Test
	public void testPlaceOrder_MarketBuy() throws Exception {
		expect(order.getQty()).andStubReturn(10L);
		expect(order.getDirection()).andStubReturn(OrderDirection.BUY);
		expect(order.getType()).andStubReturn(OrderType.MARKET);
		
		com.ib.client.Order ibo = new com.ib.client.Order();
		ibo.m_action = "BUY";
		ibo.m_totalQuantity = 10;
		ibo.m_orderType = "MKT";
		expect(transNumerator.incrementAndGet()).andReturn(150);
		terminal.registerPendingOrder(eq(150L), same(order));
		Contract contract = new Contract();
		expect(gSec2Contr.get(same(descr))).andReturn(contract);
		client.placeOrder(eq(150), same(contract), eq(ibo));
		control.replay();
		
		processor.placeOrder(order);
		
		control.verify();
	}
	
	@Test
	public void testPlaceOrder_MarketSell() throws Exception {
		expect(order.getQty()).andStubReturn(100L);
		expect(order.getDirection()).andStubReturn(OrderDirection.SELL);
		expect(order.getType()).andStubReturn(OrderType.MARKET);
		
		com.ib.client.Order ibo = new com.ib.client.Order();
		ibo.m_action = "SELL";
		ibo.m_totalQuantity = 50;
		ibo.m_orderType = "MKT";
		expect(transNumerator.incrementAndGet()).andReturn(50);
		terminal.registerPendingOrder(eq(50L), same(order));
		Contract contract = new Contract();
		expect(gSec2Contr.get(same(descr))).andReturn(contract);
		client.placeOrder(eq(50), same(contract), eq(ibo));
		control.replay();
		
		processor.placeOrder(order);
		
		control.verify();
	}

	@Test (expected=OrderException.class)
	public void testPlaceOrder_LimitUnsupported() throws Exception {
		expect(order.getType()).andStubReturn(OrderType.LIMIT);
		control.replay();
		
		processor.placeOrder(order);
	}

	@Test (expected=OrderException.class)
	public void testPlaceOrder_StopLimitUnsupported() throws Exception {
		expect(order.getType()).andStubReturn(OrderType.STOP_LIMIT);
		control.replay();
		
		processor.placeOrder(order);
	}
	
	@Test (expected=OrderException.class)
	public void testPlaceOrder_TakeProfitAndStopLimitUnsupported()
		throws Exception
	{
		expect(order.getType()).andStubReturn(OrderType.TPSL);
		control.replay();
		
		processor.placeOrder(order);
	}
	
	@Test (expected=OrderException.class)
	public void testPlaceOrder_TakeProfitUnsupported() throws Exception {
		expect(order.getType()).andStubReturn(OrderType.TAKE_PROFIT);
		control.replay();
		
		processor.placeOrder(order);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testEquals() throws Exception {
		TerminalBuilder tb = new TerminalBuilder();
		EditableTerminal t1 = tb.createTerminal("foo"),
			t2 = tb.createTerminal("foo");
		processor = new IBOrderProcessor(t1, client, transNumerator, gSec2Contr);
		Variant<EditableTerminal> vTerm = new Variant<EditableTerminal>()
			.add(t1)
			.add(t2);
		Variant<IBClient> vClient = new Variant<IBClient>(vTerm)
			.add(client)
			.add(control.createMock(IBClient.class));
		Variant<Counter> vTransId = new Variant<Counter>(vClient)
			.add(transNumerator)
			.add(control.createMock(Counter.class));
		Variant<G<Contract>> vS2C = new Variant<G<Contract>>(vTransId)
			.add(gSec2Contr)
			.add(control.createMock(G.class));
		Variant<?> iterator = vS2C;
		int foundCnt = 0;
		IBOrderProcessor x = null, found = null;
		do {
			x = new IBOrderProcessor(vTerm.get(), vClient.get(),
					vTransId.get(), vS2C.get());
			if ( processor.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(client, found.getClient());
		assertSame(transNumerator, found.getTransNumerator());
		assertSame(gSec2Contr, found.getSecDescr2ContractConverter());
		assertSame(t1, found.getTerminal());
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
			.append(transNumerator)
			.append(gSec2Contr)
			.append(terminal)
			.toHashCode();
		assertEquals(hashCode, processor.hashCode());
	}
	
	@Test
	public void testConstruct2() throws Exception {
		IBOrderProcessor expected = new IBOrderProcessor(terminal, client,
				transNumerator, new IBGetSecurityDescriptorContract());
		assertEquals(expected,
				new IBOrderProcessor(terminal, client, transNumerator));
	}

}
