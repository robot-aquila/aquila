package ru.prolib.aquila.ib.assembler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;

import com.ib.client.Contract;
import com.ib.client.OrderState;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.ib.IBEditableTerminal;
import ru.prolib.aquila.ib.api.IBClient;
import ru.prolib.aquila.ib.assembler.cache.*;

public class IBOrderHandlerTest {
	private IMocksControl control;
	private EditableOrder order;
	private IBEditableTerminal terminal;
	private IBClient client;
	private Cache cache;
	private OrderSystemInfo sysInfo;
	private PlaceOrderRequest request;
	private IBOrderHandler handler;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		order = control.createMock(EditableOrder.class);
		terminal = control.createMock(IBEditableTerminal.class);
		client = control.createMock(IBClient.class);
		cache = control.createMock(Cache.class);
		sysInfo = new OrderSystemInfo();
		request = new PlaceOrderRequest(new Contract());
		request.getOrder().m_orderId = 824;
		request.getContract().m_conId = 1024;
		handler = new IBOrderHandler(order, request);
		
		expect(order.getTerminal()).andStubReturn(terminal);
		expect(order.getSystemInfo()).andStubReturn(sysInfo);
		expect(order.getId()).andStubReturn(824);
		expect(terminal.getCache()).andStubReturn(cache);
		expect(terminal.getClient()).andStubReturn(client);
	}
	
	/**
	 * Создать экземпляр состояния заявки.
	 * <p>
	 * Конструктор класса статуса защищенный. Данный метод создает экземпляр
	 * используя рефлекшн API.
	 * <p>
	 * @return новый экземпляр состояния
	 * @throws Exception
	 */
	private OrderState createOrderState() throws Exception {
		Constructor<OrderState> con = OrderState.class.getDeclaredConstructor();
		con.setAccessible(true);
		con.newInstance();
		return con.newInstance();		
	}
	
	@Test
	public void testPlaceOrder() throws Exception {
		order.setStatus(OrderStatus.SENT);
		order.fireChangedEvent();
		order.resetChanges();
		client.placeOrder(eq(824), same(request.getContract()),
				same(request.getOrder()));
		control.replay();
		
		handler.placeOrder();
		
		control.verify();
		assertTrue(sysInfo.getRegistration().isStarted());
		assertSame(request, sysInfo.getRegistration().getRequest());
	}
	
	@Test
	public void testCancelOrder() throws Exception {
		order.setStatus(OrderStatus.CANCEL_SENT);
		order.fireChangedEvent();
		order.resetChanges();
		client.cancelOrder(eq(824));
		control.replay();
		
		handler.cancelOrder();
		
		control.verify();
		assertTrue(sysInfo.getCancellation().isStarted());
		assertEquals(new Integer(824), sysInfo.getCancellation().getRequest());
	}
	
	@Test
	public void testError_SkipForActive() throws Exception {
		expect(order.getStatus()).andReturn(OrderStatus.ACTIVE);
		control.replay();
		
		handler.error(824, 400, "test error");
		
		control.verify();
	}

	@Test
	public void testError_ForSent() throws Exception {
		expect(order.getStatus()).andReturn(OrderStatus.SENT);
		order.setStatus(OrderStatus.REJECTED);
		order.fireChangedEvent();
		order.resetChanges();
		client.removeHandler(eq(824));
		control.replay();
		
		handler.error(824, 400, "test error");
		
		control.verify();
		assertTrue(sysInfo.getRegistration().isExecuted());
		assertEquals("test error", sysInfo.getRegistration().getResponse());
	}

	@Test
	public void testError_ForCancelSent() throws Exception {
		expect(order.getStatus()).andReturn(OrderStatus.CANCEL_SENT);
		order.setStatus(OrderStatus.CANCEL_FAILED);
		order.fireChangedEvent();
		order.resetChanges();
		client.removeHandler(eq(824));
		control.replay();
		
		handler.error(824, 400, "test error");
		
		control.verify();
		assertTrue(sysInfo.getCancellation().isExecuted());
		assertEquals("test error", sysInfo.getCancellation().getResponse());
	}
	
	@Test
	public void testOpenOrder_ForSent() throws Exception {
		OrderEntry entry = new OrderEntry(824, request.getContract(),
				request.getOrder(), createOrderState());
		cache.update(eq(entry));
		expect(order.getStatus()).andReturn(OrderStatus.SENT);
		order.setStatus(OrderStatus.ACTIVE);
		order.fireChangedEvent();
		order.resetChanges();
		control.replay();
		
		handler.openOrder(824, entry.getContract(), entry.getOrder(),
				entry.getOrderState());
		
		control.verify();
		assertTrue(sysInfo.getRegistration().isExecuted());
		assertEquals(entry, sysInfo.getRegistration().getResponse());
	}
	
	@Test
	public void testOpenOrder_SkipForOther() throws Exception {
		OrderStatus list[] = {
				OrderStatus.ACTIVE,
				OrderStatus.CANCEL_FAILED,
				OrderStatus.CANCEL_SENT,
				OrderStatus.CANCELLED,
				OrderStatus.CONDITION,
				OrderStatus.FILLED,
				OrderStatus.PENDING,
				OrderStatus.REJECTED,
		};
		OrderEntry entry = new OrderEntry(824, request.getContract(),
				request.getOrder(), createOrderState());
		for ( int i = 0; i < list.length; i ++ ) {
			setUp();
			cache.update(eq(entry));
			expect(order.getStatus()).andReturn(list[i]);
			control.replay();
			
			handler.openOrder(824, entry.getContract(), entry.getOrder(),
					entry.getOrderState());
			
			control.verify();
		}
	}
	
	@Test
	public void testOrderStatus_ForCancelled() throws Exception {
		OrderStatusEntry entry =
			new OrderStatusEntry(824, "Cancelled", 18, 128.15d);
		cache.update(eq(entry));
		order.setQtyRest(eq(18L));
		order.setAvgExecutedPrice(eq(128.15d));
		expect(order.getQty()).andReturn(20L);
		order.setExecutedVolume(eq(256.3));
		order.setStatus(OrderStatus.CANCELLED);
		order.fireChangedEvent();
		order.resetChanges();
		client.removeHandler(eq(824));
		control.replay();
		
		handler.orderStatus(824, "Cancelled", 0, 18, 128.15d, 0, 0, 0d, 0, "");
		
		control.verify();
		assertTrue(sysInfo.getCancellation().isExecuted());
		assertEquals(entry, sysInfo.getCancellation().getResponse());
	}
	
	@Test
	public void testOrderStatus_ForFilled() throws Exception {
		OrderStatusEntry entry =
			new OrderStatusEntry(824, "Filled", 5, 400d);
		cache.update(eq(entry));
		order.setQtyRest(eq(5L));
		order.setAvgExecutedPrice(eq(400d));
		expect(order.getQty()).andReturn(10L);
		order.setExecutedVolume(eq(2000d));
		order.setStatus(OrderStatus.FILLED);
		order.fireChangedEvent();
		order.resetChanges();
		client.removeHandler(eq(824));
		control.replay();
		
		handler.orderStatus(824, "Filled", 0, 5, 400d, 0, 0, 0d, 0, "");
		
		control.verify();
	}
	
	@Test
	public void testOrderStatus_ForOther() throws Exception {
		String list[] = {
				"PendingSubmit",
				"PendingCancel",
				"PreSubmitted",
				"Submitted",
				"Inactive",
		};
		OrderStatusEntry entry;
		for ( int i = 0; i < list.length; i ++ ) {
			setUp();
			entry = new OrderStatusEntry(824, list[i], 10, 20d);
			cache.update(eq(entry));
			order.setQtyRest(eq(10L));
			order.setAvgExecutedPrice(eq(20d));
			expect(order.getQty()).andReturn(15L);
			order.setExecutedVolume(eq(100d));
			expect(order.hasChanged()).andReturn(true);
			order.fireChangedEvent();
			order.resetChanges();
			control.replay();
			
			handler.orderStatus(824, list[i], 0, 10, 20d, 0, 0, 0d, 0, "");
			
			control.verify();
		}
	}

	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(handler.equals(handler));
		assertFalse(handler.equals(null));
		assertFalse(handler.equals(this));
	}

	@Test
	public void testEquals() throws Exception {
		Variant<Order> vOrd = new Variant<Order>()
			.add(order)
			.add(control.createMock(EditableOrder.class));
		Variant<PlaceOrderRequest> vReq = new Variant<PlaceOrderRequest>(vOrd)
			.add(request)
			.add(control.createMock(PlaceOrderRequest.class));
		Variant<?> iterator = vReq;
		int foundCnt = 0;
		IBOrderHandler x, found = null;
		do {
			x = new IBOrderHandler(vOrd.get(), vReq.get());
			if ( handler.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(order, found.getOrder());
		assertSame(request, found.getPlaceOrderRequest());
	}

}
