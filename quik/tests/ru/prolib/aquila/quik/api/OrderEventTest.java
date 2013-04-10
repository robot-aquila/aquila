package ru.prolib.aquila.quik.api;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.t2q.T2QOrder;

public class OrderEventTest {
	private IMocksControl control;
	private EventType type, type2;
	private T2QOrder order, order2;
	private OrderEvent event;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		type = control.createMock(EventType.class);
		type2 = control.createMock(EventType.class);
		order = control.createMock(T2QOrder.class);
		order2 = control.createMock(T2QOrder.class);
		event = new OrderEvent(type, order);
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(event.equals(event));
		assertFalse(event.equals(null));
		assertFalse(event.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<EventType> vType = new Variant<EventType>()
			.add(type)
			.add(type2);
		Variant<T2QOrder> vOrd = new Variant<T2QOrder>(vType)
			.add(order)
			.add(order2);
		Variant<?> iterator = vOrd;
		int foundCnt = 0;
		OrderEvent found = null, x = null;
		do {
			x = new OrderEvent(vType.get(), vOrd.get());
			if ( event.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(type, found.getType());
		assertEquals(order, found.getOrderState());
	}

}
