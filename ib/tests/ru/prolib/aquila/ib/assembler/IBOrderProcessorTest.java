package ru.prolib.aquila.ib.assembler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;

import com.ib.client.Contract;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.OrderDirection;
import ru.prolib.aquila.core.BusinessEntities.OrderType;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.ib.IBEditableTerminal;
import ru.prolib.aquila.ib.IBTerminalBuilder;
import ru.prolib.aquila.ib.api.IBClient;
import ru.prolib.aquila.ib.assembler.cache.*;

public class IBOrderProcessorTest {
	private static SecurityDescriptor descr;
	private IMocksControl control;
	private IBEditableTerminal terminal;
	private Cache cache;
	private IBClient client;
	private EditableOrder order;
	private IBOrderProcessor processor;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		descr = new SecurityDescriptor("AAPL", "NSD", "USD", SecurityType.STK);
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(IBEditableTerminal.class);
		cache = control.createMock(Cache.class);
		client = control.createMock(IBClient.class);
		order = control.createMock(EditableOrder.class);
		processor = new IBOrderProcessor(terminal);
		
		expect(terminal.getCache()).andStubReturn(cache);
		expect(terminal.getClient()).andReturn(client);
	}
	
	@Test
	public void testCancelOrder() throws Exception {
		expect(order.getId()).andStubReturn(213L);
		client.cancelOrder(eq(213));
		control.replay();
		
		processor.cancelOrder(order);
		
		control.verify();
	}
	
	@Test
	public void testPlaceOrder_Market() throws Exception {
		expect(order.getType()).andStubReturn(OrderType.MARKET);
		expect(order.getDirection()).andStubReturn(OrderDirection.BUY);
		expect(order.getQty()).andStubReturn(10L);
		expect(order.getSecurityDescriptor()).andStubReturn(descr);
		
		expect(client.nextReqId()).andReturn(824);
		terminal.registerPendingOrder(eq(824L), same(order));
		
		Contract contract = new Contract();
		ContractEntry conEntry = control.createMock(ContractEntry.class);
		expect(cache.getContract(same(descr))).andReturn(conEntry);
		expect(conEntry.getDefaultContract()).andReturn(contract);
		
		com.ib.client.Order expected = new com.ib.client.Order();
		expected.m_orderType = "MKT";
		expected.m_action = "BUY";
		expected.m_totalQuantity = 10;
		
		client.placeOrder(eq(824), same(contract), eq(expected));
		control.replay();
		
		processor.placeOrder(order);
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(processor.equals(processor));
		assertFalse(processor.equals(null));
		assertFalse(processor.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		IBTerminalBuilder tb = new IBTerminalBuilder();
		IBEditableTerminal t1 = tb.createTerminal("foo"),
			t2 = tb.createTerminal("foo");
		processor = new IBOrderProcessor(t1);
		Variant<IBEditableTerminal> vTerm = new Variant<IBEditableTerminal>()
			.add(t1)
			.add(t2);
		Variant<?> iterator = vTerm;
		int foundCnt = 0;
		IBOrderProcessor x, found = null;
		do {
			x = new IBOrderProcessor(vTerm.get());
			if ( processor.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(t1, found.getTerminal());
	}

}
