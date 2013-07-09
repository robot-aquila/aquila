package ru.prolib.aquila.ib.assembler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;

import com.ib.client.Contract;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.Direction;
import ru.prolib.aquila.core.BusinessEntities.OrderException;
import ru.prolib.aquila.core.BusinessEntities.OrderStatus;
import ru.prolib.aquila.core.BusinessEntities.OrderSystemInfo;
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
	private OrderSystemInfo sysInfo;

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
		sysInfo = new OrderSystemInfo();
	
		expect(order.getId()).andStubReturn(213);
		expect(order.getSystemInfo()).andStubReturn(sysInfo);
		expect(order.getTerminal()).andStubReturn(terminal);
		expect(terminal.getCache()).andStubReturn(cache);
		expect(terminal.getClient()).andStubReturn(client);
	}
	
	@Test
	public void testCancelOrder_SkipForFinalOrCancelSent() throws Exception {
		OrderStatus list[] = {
				OrderStatus.CANCEL_FAILED,
				OrderStatus.CANCEL_SENT,
				OrderStatus.CANCELLED,
				OrderStatus.FILLED,
				OrderStatus.REJECTED,
		};
		for ( int i = 0; i < list.length; i ++ ) {
			setUp();
			expect(order.getStatus()).andReturn(list[i]);
			control.replay();
			
			processor.cancelOrder(order);
			
			control.verify();
		}
	}
	
	@Test
	public void testCancelOrder_ThrowsForNotActivated() throws Exception {
		OrderStatus list[] = {
				OrderStatus.CONDITION,
				OrderStatus.PENDING,
				OrderStatus.SENT,
		};
		for ( int i = 0; i < list.length; i ++ ) {
			setUp();
			expect(order.getStatus()).andReturn(list[i]);
			control.replay();
			
			try {
				processor.cancelOrder(order);
				fail("Expected: " + OrderException.class.getSimpleName());
			} catch ( OrderException e ) { }
			control.verify();
		}
	}
	
	@Test
	public void testCancelOrder_ForActive() throws Exception {
		expect(order.getStatus()).andReturn(OrderStatus.ACTIVE);
		IBOrderHandler handler = control.createMock(IBOrderHandler.class);
		expect(client.getOrderHandler(eq(213))).andReturn(handler);
		handler.cancelOrder();
		control.replay();
		
		processor.cancelOrder(order);
		
		control.verify();
	}
	
	@Test
	public void testPlaceOrder_ThrowsForIllegalStatus() throws Exception {
		OrderStatus list[] = {
				OrderStatus.ACTIVE,
				OrderStatus.CANCEL_FAILED,
				OrderStatus.CANCEL_SENT,
				OrderStatus.CANCELLED,
				OrderStatus.FILLED,
				OrderStatus.REJECTED,
				OrderStatus.SENT,
		};
		for ( int i = 0; i < list.length; i ++ ) {
			setUp();
			expect(order.getStatus()).andReturn(list[i]);
			control.replay();
			
			try {
				processor.placeOrder(order);
				fail("Expected: " + OrderException.class.getSimpleName());
			} catch ( OrderException e ) { }
			control.verify();
		}
	}
	
	@Test
	public void testPlaceOrder_Market() throws Exception {
		expect(order.getStatus()).andStubReturn(OrderStatus.PENDING);
		expect(order.getType()).andStubReturn(OrderType.MARKET);
		expect(order.getDirection()).andStubReturn(Direction.BUY);
		expect(order.getQty()).andStubReturn(10L);
		expect(order.getSecurityDescriptor()).andStubReturn(descr);
		
		Contract ibc = new Contract();
		ContractEntry entry = control.createMock(ContractEntry.class);
		expect(cache.getContract(same(descr))).andReturn(entry);
		expect(entry.getDefaultContract()).andReturn(ibc);

		PlaceOrderRequest request = new PlaceOrderRequest(ibc);
		request.getOrder().m_orderId = 213;
		request.getOrder().m_orderType = "MKT";
		request.getOrder().m_action = "BUY";
		request.getOrder().m_totalQuantity = 10;

		IBOrderHandler handler = new IBOrderHandler(order, request);
		client.setOrderHandler(eq(213), eq(handler));
		// Мокнуть хендлер никак, так что это последовательность из хендлера
		order.setStatus(OrderStatus.SENT);
		order.fireChangedEvent();
		order.resetChanges();
		client.placeOrder(eq(213), same(ibc), eq(request.getOrder()));
		control.replay();
		
		processor.placeOrder(order);
		
		control.verify();
		assertTrue(sysInfo.getRegisteration().isStarted());
		assertEquals(request, sysInfo.getRegisteration().getRequest());
	}
	
	@Test
	public void testPlaceOrder_Limit() throws Exception {
		expect(order.getStatus()).andStubReturn(OrderStatus.CONDITION);
		expect(order.getType()).andStubReturn(OrderType.LIMIT);
		expect(order.getDirection()).andStubReturn(Direction.SELL);
		expect(order.getQty()).andStubReturn(100L);
		expect(order.getPrice()).andStubReturn(800d);
		expect(order.getSecurityDescriptor()).andStubReturn(descr);

		Contract ibc = new Contract();
		ContractEntry entry = control.createMock(ContractEntry.class);
		expect(cache.getContract(same(descr))).andReturn(entry);
		expect(entry.getDefaultContract()).andReturn(ibc);

		PlaceOrderRequest request = new PlaceOrderRequest(ibc);
		request.getOrder().m_orderId = 213;
		request.getOrder().m_orderType = "LMT";
		request.getOrder().m_action = "SELL";
		request.getOrder().m_totalQuantity = 100;
		request.getOrder().m_lmtPrice = 800d;

		IBOrderHandler handler = new IBOrderHandler(order, request);
		client.setOrderHandler(eq(213), eq(handler));
		// Мокнуть хендлер никак, так что это последовательность из хендлера
		order.setStatus(OrderStatus.SENT);
		order.fireChangedEvent();
		order.resetChanges();
		client.placeOrder(eq(213), same(ibc), eq(request.getOrder()));
		control.replay();
		
		processor.placeOrder(order);
		
		control.verify();
		assertTrue(sysInfo.getRegisteration().isStarted());
		assertEquals(request, sysInfo.getRegisteration().getRequest());
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
