package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-09-25<br>
 * $Id: OrderEventTest.java 283 2012-09-26 17:01:17Z whirlwind $
 */
public class OrderEventTest {
	private IMocksControl control;
	private EventType type1,type2;
	private Order order1,order2;
	private OrderEvent event;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		type1 = control.createMock(EventType.class);
		type2 = control.createMock(EventType.class);
		order1 = control.createMock(Order.class);
		order2 = control.createMock(Order.class);
		event = new OrderEvent(type1, order1);
	}
	
	@Test
	public void testAccessors() throws Exception {
		assertSame(type1, event.getType());
		assertSame(order1, event.getOrder());
	}
	
	@Test
	public void testEquals_MixedVars() throws Exception {
		Variant<EventType> vType = new Variant<EventType>()
			.add(type1)
			.add(type2);
		Variant<Order> vOrder = new Variant<Order>(vType)
			.add(null)
			.add(order1)
			.add(order2);
		int foundCount = 0;
		OrderEvent foundEvent = null;
		do {
			OrderEvent actual = new OrderEvent(vType.get(), vOrder.get());
			if ( event.equals(actual) ) {
				foundCount ++;
				foundEvent = actual;
			}
		} while ( vOrder.next() );
		assertEquals(1, foundCount);
		assertNotNull(foundEvent);
		assertSame(type1, foundEvent.getType());
		assertSame(order1, foundEvent.getOrder());
	}
	
	@Test
	public void testEquals_TrueIfSameInstance() throws Exception {
		assertTrue(event.equals(event));
	}
	
	@Test
	public void testEquals_FalseIfNull() throws Exception {
		assertFalse(event.equals(null));
	}
	
	@Test
	public void testEquals_FalseIfDifferentClass() throws Exception {
		assertFalse(event.equals(this));
	}

}
