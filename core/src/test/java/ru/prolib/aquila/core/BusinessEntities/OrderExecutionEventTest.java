package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.utils.Variant;

public class OrderExecutionEventTest {
	private IMocksControl control;
	private Order o1, o2;
	private OrderExecution e1, e2;
	private EventType type1, type2;
	private OrderExecutionEvent event;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		o1 = control.createMock(Order.class);
		o2 = control.createMock(Order.class);
		e1 = control.createMock(OrderExecution.class);
		e2 = control.createMock(OrderExecution.class);
		type1 = new EventTypeImpl("type1");
		type2 = new EventTypeImpl("type2");
		event = new OrderExecutionEvent(type1, o1, e1);
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(event.equals(event));
		assertFalse(event.equals(null));
		assertFalse(event.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<EventType> vType = new Variant<EventType>(type1, type2);
		Variant<Order> vOrder = new Variant<Order>(vType, o1, o2);
		Variant<OrderExecution> vExec = new Variant<OrderExecution>(vOrder, e1, e2);
		Variant<?> iterator = vExec;
		int foundCnt = 0;
		OrderExecutionEvent x = null, found = null;
		do {
			x = new OrderExecutionEvent(vType.get(), vOrder.get(), vExec.get());
			if ( event.equals(x) ) {
				found = x;
				foundCnt ++;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(type1, found.getType());
		assertSame(o1, found.getOrder());
		assertSame(e1, found.getExecution());
	}

}
