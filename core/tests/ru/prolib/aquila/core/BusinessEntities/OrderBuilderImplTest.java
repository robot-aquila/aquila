package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.List;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.log4j.BasicConfigurator;
import org.easymock.*;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.utils.OrderFactory;
import ru.prolib.aquila.core.BusinessEntities.utils.OrderFactoryImpl;
import ru.prolib.aquila.core.utils.Counter;
import ru.prolib.aquila.core.utils.SimpleCounter;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-12-10<br>
 * $Id: OrderBuilderImplTest.java 552 2013-03-01 13:35:35Z whirlwind $
 */
public class OrderBuilderImplTest {
	private static IMocksControl control;
	private static OrderFactory factory;
	private static EditableTerminal terminal;
	private static Counter transId;
	private static OrderBuilderImpl builder;
	private static Account account = new Account("test");
	private static SecurityDescriptor descr =
			new SecurityDescriptor("GAZP", "EQBR", "RUR", SecurityType.STK);
	private static Security security;
	private EditableOrder order;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		control = createStrictControl();
		factory = control.createMock(OrderFactory.class);
		terminal = control.createMock(EditableTerminal.class);
		security = control.createMock(Security.class);
		transId = control.createMock(Counter.class);
		builder = new OrderBuilderImpl(factory, terminal, transId);
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
		expect(security.getDescriptor()).andStubReturn(descr);
		order = new OrderFactoryImpl(new EventSystemImpl(),
				control.createMock(Terminal.class)).createOrder();
	}
	
	@Test
	public void testConstruct3() throws Exception {
		OrderBuilder expected = new OrderBuilderImpl(factory, 
				terminal, new SimpleCounter(0));
		assertEquals(expected, new OrderBuilderImpl(factory, terminal));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<OrderFactory> vof = new Variant<OrderFactory>()
			.add(factory)
			.add(control.createMock(OrderFactory.class));
		Variant<EditableTerminal> vt = new Variant<EditableTerminal>(vof)
			.add(terminal)
			.add(control.createMock(EditableTerminal.class));
		Variant<Counter> vc = new Variant<Counter>(vt)
			.add(transId)
			.add(control.createMock(Counter.class));
		Variant<?> iterator = vc;
		int foundCnt = 0;
		OrderBuilderImpl found = null, x = null;
		do {
			x = new OrderBuilderImpl(vof.get(), vt.get(), vc.get());
			if ( builder.equals(x) ) {
				found = x;
				foundCnt ++;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(factory, found.getOrderFactory());
		assertSame(terminal, found.getTerminal());
		assertSame(transId, found.getTransIdCounter());
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121211, 43719)
			.append(factory)
			.append(terminal)
			.append(transId)
			.toHashCode();
		assertEquals(hashCode, builder.hashCode());
	}
	
	@Test
	public void testCreateMarketOrderB() throws Exception {
		expect(factory.createOrder()).andReturn(order);
		expect(transId.incrementAndGet()).andReturn(900);
		terminal.registerPendingOrder(same(order));
		expectLastCall().andDelegateTo(new OrdersStub(){
			@Override public void registerPendingOrder(EditableOrder o) {
				assertSame(order, o);
				assertEquals(account, o.getAccount());
				assertEquals(OrderDirection.BUY, o.getDirection());
				assertNull(o.getId());
				assertNull(o.getLinkedOrderId());
				assertNull(o.getOffset());
				assertNull(o.getPrice());
				assertEquals(new Long(10), o.getQty());
				assertEquals(new Long(10), o.getQtyRest());
				assertEquals(descr, o.getSecurityDescriptor());
				assertNull(o.getSpread());
				assertEquals(OrderStatus.PENDING, o.getStatus());
				assertNull(o.getStopLimitPrice());
				assertNull(o.getTakeProfitPrice());
				assertEquals(new Long(900), o.getTransactionId());
				assertEquals(OrderType.MARKET, o.getType());
			}
		});
		control.replay();
		assertSame(order, builder.createMarketOrderB(account, security, 10));
		control.verify();
	}
	
	@Test
	public void testCreateMarketOrderS() throws Exception {
		expect(factory.createOrder()).andReturn(order);
		expect(transId.incrementAndGet()).andReturn(170);
		terminal.registerPendingOrder(same(order));
		expectLastCall().andDelegateTo(new OrdersStub(){
			@Override public void registerPendingOrder(EditableOrder o) {
				assertSame(order, o);
				assertEquals(account, o.getAccount());
				assertEquals(OrderDirection.SELL, o.getDirection());
				assertNull(o.getId());
				assertNull(o.getLinkedOrderId());
				assertNull(o.getOffset());
				assertNull(o.getPrice());
				assertEquals(new Long(5), o.getQty());
				assertEquals(new Long(5), o.getQtyRest());
				assertEquals(descr, o.getSecurityDescriptor());
				assertNull(o.getSpread());
				assertEquals(OrderStatus.PENDING, o.getStatus());
				assertNull(o.getStopLimitPrice());
				assertNull(o.getTakeProfitPrice());
				assertEquals(new Long(170), o.getTransactionId());
				assertEquals(OrderType.MARKET, o.getType());
			}
		});
		control.replay();
		assertSame(order, builder.createMarketOrderS(account, security, 5));
		control.verify();
	}
	
	@Test
	public void testCreateLimitOrderB() throws Exception {
		expect(factory.createOrder()).andReturn(order);
		expect(transId.incrementAndGet()).andReturn(4827);
		terminal.registerPendingOrder(same(order));
		expectLastCall().andDelegateTo(new OrdersStub(){
			@Override public void registerPendingOrder(EditableOrder o) {
				assertSame(order, o);
				assertEquals(account, o.getAccount());
				assertEquals(OrderDirection.BUY, o.getDirection());
				assertNull(o.getId());
				assertNull(o.getLinkedOrderId());
				assertNull(o.getOffset());
				assertEquals(23.5d, o.getPrice(), 0.01d);
				assertEquals(new Long(50), o.getQty());
				assertEquals(new Long(50), o.getQtyRest());
				assertEquals(descr, o.getSecurityDescriptor());
				assertNull(o.getSpread());
				assertEquals(OrderStatus.PENDING, o.getStatus());
				assertNull(o.getStopLimitPrice());
				assertNull(o.getTakeProfitPrice());
				assertEquals(new Long(4827), o.getTransactionId());
				assertEquals(OrderType.LIMIT, o.getType());
			}
		});
		control.replay();
		assertSame(order, builder.createLimitOrderB(account,security,50,23.5d));
		control.verify();
	}
	
	@Test
	public void testCreateLimitOrderS() throws Exception {
		expect(factory.createOrder()).andReturn(order);
		expect(transId.incrementAndGet()).andReturn(8274);
		terminal.registerPendingOrder(same(order));
		expectLastCall().andDelegateTo(new OrdersStub(){
			@Override public void registerPendingOrder(EditableOrder o) {
				assertSame(order, o);
				assertEquals(account, o.getAccount());
				assertEquals(OrderDirection.SELL, o.getDirection());
				assertNull(o.getId());
				assertNull(o.getLinkedOrderId());
				assertNull(o.getOffset());
				assertEquals(83.5d, o.getPrice(), 0.01d);
				assertEquals(new Long(10), o.getQty());
				assertEquals(new Long(10), o.getQtyRest());
				assertEquals(descr, o.getSecurityDescriptor());
				assertNull(o.getSpread());
				assertEquals(OrderStatus.PENDING, o.getStatus());
				assertNull(o.getStopLimitPrice());
				assertNull(o.getTakeProfitPrice());
				assertEquals(new Long(8274), o.getTransactionId());
				assertEquals(OrderType.LIMIT, o.getType());
			}
		});
		control.replay();
		assertSame(order, builder.createLimitOrderS(account,security,10,83.5d));
		control.verify();
	}
	
	@Test
	public void testCreateStopLimitB() throws Exception {
		expect(factory.createOrder()).andReturn(order);
		expect(transId.incrementAndGet()).andReturn(14827);
		terminal.registerPendingStopOrder(same(order));
		expectLastCall().andDelegateTo(new StopOrdersStub(){
			@Override public void registerPendingStopOrder(EditableOrder o) {
				assertSame(order, o);
				assertEquals(account, o.getAccount());
				assertEquals(OrderDirection.BUY, o.getDirection());
				assertNull(o.getId());
				assertNull(o.getLinkedOrderId());
				assertNull(o.getOffset());
				assertEquals(24.0d, o.getPrice(), 0.01d);
				assertEquals(new Long(50), o.getQty());
				assertNull(o.getQtyRest());
				assertEquals(descr, o.getSecurityDescriptor());
				assertNull(o.getSpread());
				assertEquals(OrderStatus.PENDING, o.getStatus());
				assertEquals(23.5d, o.getStopLimitPrice(), 0.01d);
				assertNull(o.getTakeProfitPrice());
				assertEquals(new Long(14827), o.getTransactionId());
				assertEquals(OrderType.STOP_LIMIT, o.getType());
			}
		});
		control.replay();
		assertSame(order,
				builder.createStopLimitB(account, security, 50, 23.5d, 24.0d));
		control.verify();
	}
	
	@Test
	public void testCreateStopLimitS() throws Exception {
		expect(factory.createOrder()).andReturn(order);
		expect(transId.incrementAndGet()).andReturn(214827);
		terminal.registerPendingStopOrder(same(order));
		expectLastCall().andDelegateTo(new StopOrdersStub(){
			@Override public void registerPendingStopOrder(EditableOrder o) {
				assertSame(order, o);
				assertEquals(account, o.getAccount());
				assertEquals(OrderDirection.SELL, o.getDirection());
				assertNull(o.getId());
				assertNull(o.getLinkedOrderId());
				assertNull(o.getOffset());
				assertEquals(22.0d, o.getPrice(), 0.01d);
				assertEquals(new Long(80), o.getQty());
				assertNull(o.getQtyRest());
				assertEquals(descr, o.getSecurityDescriptor());
				assertNull(o.getSpread());
				assertEquals(OrderStatus.PENDING, o.getStatus());
				assertEquals(23.5d, o.getStopLimitPrice(), 0.01d);
				assertNull(o.getTakeProfitPrice());
				assertEquals(new Long(214827), o.getTransactionId());
				assertEquals(OrderType.STOP_LIMIT, o.getType());
			}
		});
		control.replay();
		assertSame(order,
				builder.createStopLimitS(account, security, 80, 23.5d, 22.0d));
		control.verify();
	}
	
	private static class OrdersStub implements EditableOrders {
		@Override public boolean isOrderExists(long id) { return false; }
		@Override public List<Order> getOrders() { return null; }
		@Override public Order getOrder(long id) { return null; }
		@Override public EventType OnOrderAvailable() { return null; }
		@Override public void fireOrderAvailableEvent(Order order) { }
		@Override public EditableOrder getEditableOrder(long id)
			{ return null; }
		@Override public void registerOrder(EditableOrder order) { }
		@Override public void purgeOrder(EditableOrder order) { }
		@Override public void purgeOrder(long id) { }
		@Override public boolean isPendingOrder(long transId) { return false; }
		@Override public void registerPendingOrder(EditableOrder order) { }
		@Override public void purgePendingOrder(EditableOrder order) { }
		@Override public void purgePendingOrder(long transId) { }
		@Override public EditableOrder getPendingOrder(long transId)
			{ return null; }
		@Override public int getOrdersCount() { return 0; }
		@Override public EventType OnOrderCancelFailed() { return null; }
		@Override public EventType OnOrderCancelled() { return null; }
		@Override public EventType OnOrderChanged() { return null; }
		@Override public EventType OnOrderDone() { return null; }
		@Override public EventType OnOrderFailed() { return null; }
		@Override public EventType OnOrderFilled() { return null; }
		@Override public EventType OnOrderPartiallyFilled() { return null; }
		@Override public EventType OnOrderRegistered() { return null; }
		@Override public EventType OnOrderRegisterFailed() { return null; }
		@Override
		public EditableOrder
			makePendingOrderAsRegisteredIfExists(long transId, long orderId)
				{ return null; }
	}
	
	private static class StopOrdersStub implements EditableStopOrders {
		@Override public boolean isStopOrderExists(long id) { return false; }
		@Override public List<Order> getStopOrders() { return null; }
		@Override public Order getStopOrder(long id) { return null; }
		@Override public EventType OnStopOrderAvailable() { return null; }
		@Override public void fireStopOrderAvailableEvent(Order order) { }
		@Override public EditableOrder getEditableStopOrder(long id)
			{ return null; }
		@Override public void registerStopOrder(EditableOrder order) { }
		@Override public void purgeStopOrder(EditableOrder order) { }
		@Override public void purgeStopOrder(long id) { }
		@Override public boolean isPendingStopOrder(long transId)
			{ return false; }
		@Override public void registerPendingStopOrder(EditableOrder order) { }
		@Override public void purgePendingStopOrder(EditableOrder order) { }
		@Override public void purgePendingStopOrder(long transId) { }
		@Override public EditableOrder getPendingStopOrder(long transId)
			{ return null; }
		@Override public int getStopOrdersCount() { return 0; }
		@Override public EventType OnStopOrderCancelFailed() { return null; }
		@Override public EventType OnStopOrderCancelled() { return null; }
		@Override public EventType OnStopOrderChanged() { return null; }
		@Override public EventType OnStopOrderDone() { return null; }
		@Override public EventType OnStopOrderFailed() { return null; }
		@Override public EventType OnStopOrderFilled() { return null; }
		@Override public EventType OnStopOrderRegistered() { return null; }
		@Override public EventType OnStopOrderRegisterFailed() { return null; }
		@Override
		public EditableOrder
			makePendingStopOrderAsRegisteredIfExists(long transId, long orderId)
				{ return null; }
	}

}
