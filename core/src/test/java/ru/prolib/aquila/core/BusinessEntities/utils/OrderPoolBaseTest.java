package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.Variant;

public class OrderPoolBaseTest {
	private IMocksControl control;
	private Terminal terminal;
	private Order order1, order2, order3, order4, order5;
	private Set<Order> pending, active, done;
	private EventType onDone;
	private OrderPoolBase base;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(Terminal.class);
		onDone = control.createMock(EventType.class);
		order1 = control.createMock(Order.class);
		order2 = control.createMock(Order.class);
		order3 = control.createMock(Order.class);
		order4 = control.createMock(Order.class);
		order5 = control.createMock(Order.class);
		pending = new LinkedHashSet<Order>();
		active = new LinkedHashSet<Order>();
		done = new LinkedHashSet<Order>();
		base = new OrderPoolBase(pending, active, done);
		
		expect(order1.getTerminal()).andStubReturn(terminal);
		expect(order2.getTerminal()).andStubReturn(terminal);
		expect(order3.getTerminal()).andStubReturn(terminal);
		expect(order4.getTerminal()).andStubReturn(terminal);
		expect(order5.getTerminal()).andStubReturn(terminal);
	}
	
	@Test
	public void testAdd_Pending() throws Exception {
		expect(order1.OnDone()).andStubReturn(onDone);
		expect(order1.getStatus()).andReturn(OrderStatus.PENDING);
		onDone.addListener(base);
		control.replay();
		
		assertSame(order1, base.add(order1));
		
		control.verify();
		assertTrue(pending.contains(order1));
		assertTrue(base.isPooled(order1));
		assertTrue(base.isPending(order1));
		assertFalse(base.isActive(order1));
		assertFalse(base.isDone(order1));
	}
	
	@Test
	public void testAdd_Activated() throws Exception {
		OrderStatus expected[] = {
				OrderStatus.SENT,
				OrderStatus.ACTIVE,
				OrderStatus.CONDITION,
		};
		for ( int i = 0; i < expected.length; i ++ ) {
			setUp();
			expect(order1.OnDone()).andStubReturn(onDone);
			expect(order1.getStatus()).andReturn(expected[i]);
			onDone.addListener(base);
			control.replay();
			
			assertSame(order1, base.add(order1));
			
			control.verify();
			String msg = "At #" + i;
			assertTrue(msg, active.contains(order1));
			assertTrue(msg, base.isPooled(order1));
			assertFalse(msg, base.isPending(order1));
			assertTrue(msg, base.isActive(order1));
			assertFalse(msg, base.isDone(order1));
		}
	}
	
	@Test
	public void testAdd_Finished() throws Exception {
		OrderStatus expected[] = {
				OrderStatus.CANCEL_FAILED,
				OrderStatus.CANCEL_SENT,
				OrderStatus.CANCELLED,
				OrderStatus.FILLED,
				OrderStatus.REJECTED,
		};
		for ( int i = 0; i < expected.length; i ++ ) {
			setUp();
			expect(order1.getStatus()).andReturn(expected[i]);
			control.replay();
			
			assertSame(order1, base.add(order1));
			
			control.verify();
			String msg = "At #" + i;
			assertTrue(msg, done.contains(order1));
			assertTrue(msg, base.isPooled(order1));
			assertFalse(msg, base.isPending(order1));
			assertFalse(msg, base.isActive(order1));
			assertTrue(msg, base.isDone(order1));
		}
	}
	
	@Test
	public void testIsPooled() throws Exception {
		pending.add(order1);
		pending.add(order2);
		active.add(order3);
		done.add(order4);
		
		assertTrue(base.isPooled(order1));
		assertTrue(base.isPooled(order2));
		assertTrue(base.isPooled(order3));
		assertTrue(base.isPooled(order4));
		assertFalse(base.isPooled(order5));
	}
	
	@Test
	public void testIsPending() throws Exception {
		pending.add(order1);
		pending.add(order2);
		active.add(order3);
		done.add(order4);
		
		assertTrue(base.isPending(order1));
		assertTrue(base.isPending(order2));
		assertFalse(base.isPending(order3));
		assertFalse(base.isPending(order4));
		assertFalse(base.isPending(order5));
	}
	
	@Test
	public void testIsActive() throws Exception {
		pending.add(order1);
		pending.add(order2);
		active.add(order3);
		done.add(order4);
		
		assertFalse(base.isActive(order1));
		assertFalse(base.isActive(order2));
		assertTrue(base.isActive(order3));
		assertFalse(base.isActive(order4));
		assertFalse(base.isActive(order5));
	}

	@Test
	public void testIsDone() throws Exception {
		pending.add(order1);
		pending.add(order2);
		active.add(order3);
		done.add(order4);
		
		assertFalse(base.isDone(order1));
		assertFalse(base.isDone(order2));
		assertFalse(base.isDone(order3));
		assertTrue(base.isDone(order4));
		assertFalse(base.isDone(order5));
	}
	
	@Test
	public void testOnEvent() throws Exception {
		pending.add(order1);
		active.add(order1); // Это по идее не нормально, но возможно например
							// в случае последовательного добавления одного
							// экземпляра когда его статус изменяется.
							// Для работы это особой роли не играет, если
							// подобные дубликаты своевременно удаляются.
							// Это удаление и проверим.
		expect(order1.OnDone()).andStubReturn(onDone);
		onDone.removeListener(same(base));
		control.replay();
		
		base.onEvent(new OrderEvent(onDone, order1));
		
		control.verify();
		assertFalse(pending.contains(order1));
		assertFalse(active.contains(order1));
		assertTrue(done.contains(order1));
	}
	
	@Test
	public void testPlaceOrders() throws Exception {
		expect(order1.getStatus()).andStubReturn(OrderStatus.PENDING);
		expect(order2.getStatus()).andStubReturn(OrderStatus.CONDITION);
		expect(order3.getStatus()).andStubReturn(OrderStatus.ACTIVE);
		expect(order4.getStatus()).andStubReturn(OrderStatus.FILLED);
		pending.add(order1);
		pending.add(order2);
		pending.add(order3);
		pending.add(order4);
		terminal.placeOrder(same(order1));
		control.replay();
		
		base.placeOrders();
		
		control.verify();
		assertFalse(pending.contains(order1));
		assertFalse(pending.contains(order2));
		assertFalse(pending.contains(order3));
		assertFalse(pending.contains(order4)); // все переходят в active
		assertTrue(active.contains(order1));
		assertTrue(active.contains(order2));
		assertTrue(active.contains(order3));
		assertTrue(active.contains(order4));
		assertFalse(done.contains(order1));
		assertFalse(done.contains(order2));
		assertFalse(done.contains(order3));
		assertFalse(done.contains(order4));
	}
	
	@Test
	public void testCancelOrders() throws Exception {
		expect(order1.getStatus()).andStubReturn(OrderStatus.CANCEL_SENT);
		expect(order2.getStatus()).andStubReturn(OrderStatus.CONDITION);
		expect(order3.getStatus()).andStubReturn(OrderStatus.ACTIVE);
		expect(order4.getStatus()).andStubReturn(OrderStatus.FILLED);
		active.add(order1);
		active.add(order2);
		active.add(order3);
		active.add(order4);
		terminal.cancelOrder(same(order2));
		terminal.cancelOrder(same(order3));
		control.replay();
		
		base.cancelOrders();
		
		control.verify();
		assertFalse(pending.contains(order1));
		assertFalse(pending.contains(order2));
		assertFalse(pending.contains(order3));
		assertFalse(pending.contains(order4));
		// Заявки никуда не переходят.
		// Должны переносится при обработке финализации
		assertTrue(active.contains(order1));
		assertTrue(active.contains(order2));
		assertTrue(active.contains(order3));
		assertTrue(active.contains(order4));
		assertFalse(done.contains(order1));
		assertFalse(done.contains(order2));
		assertFalse(done.contains(order3));
		assertFalse(done.contains(order4));
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(base.equals(base));
		assertFalse(base.equals(null));
		assertFalse(base.equals(this));
		assertFalse(base.equals(new OrderPoolBase()));
	}
	
	@Test
	public void testCompareOrders() throws Exception {
		pending.add(order1);
		active.add(order2);
		active.add(order3);
		done.add(order4);
		done.add(order5);
		Set<Order> pend1 = new LinkedHashSet<Order>();
		pend1.add(order1);
		Set<Order> pend2 = new LinkedHashSet<Order>();
		pend2.add(order2);
		pend2.add(order4);
		Set<Order> act1 = new LinkedHashSet<Order>();
		act1.add(order2);
		act1.add(order3);
		Set<Order> act2 = new LinkedHashSet<Order>();
		act2.add(order5);
		act2.add(order4);
		Set<Order> done1 = new LinkedHashSet<Order>();
		done1.add(order4);
		done1.add(order5);
		Set<Order> done2 = new LinkedHashSet<Order>();
		done2.add(order3);
		
		Variant<Set<Order>> vPend = new Variant<Set<Order>>()
			.add(pend1)
			.add(pend2);
		Variant<Set<Order>> vAct = new Variant<Set<Order>>(vPend)
			.add(act1)
			.add(act2);
		Variant<Set<Order>> vDone = new Variant<Set<Order>>(vAct)
			.add(done1)
			.add(done2);
		Variant<?> iterator = vDone;
		int foundCnt = 0;
		OrderPoolBase x, found = null;
		do {
			x = new OrderPoolBase(vPend.get(), vAct.get(), vDone.get());
			if ( base.compareOrders(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(pend1, found.getPendingOrders());
		assertEquals(act1, found.getActiveOrders());
		assertEquals(done1, found.getDoneOrders());
	}
	
	@Test
	public void testCompareOrders_SpecialCases() throws Exception {
		assertTrue(base.compareOrders(base));
		assertFalse(base.compareOrders(null));
	}
	
	@Test
	public void testConstruct0() throws Exception {
		OrderPoolBase expected = new OrderPoolBase(new LinkedHashSet<Order>(),
				new LinkedHashSet<Order>(), new LinkedHashSet<Order>());
		assertTrue(expected.compareOrders(new OrderPoolBase()));
	}
	
	@Test
	public void testListenerSpecialCase() throws Exception {
		// Тест проверяет, что разные экземпляры пула с одинаковым содержимым
		// являются отличными друг от друга наблюдателями.
		EventType type = new EventTypeImpl("foo");
		OrderPoolBase pool1 = new OrderPoolBase(), pool2 = new OrderPoolBase();
		type.addListener(pool1);
		type.addListener(pool2);
		
		List<EventListener> actual = type.getAsyncListeners();
		assertEquals(2, actual.size());
		assertSame(pool1, actual.get(0));
		assertSame(pool2, actual.get(1));
	}
	
}
