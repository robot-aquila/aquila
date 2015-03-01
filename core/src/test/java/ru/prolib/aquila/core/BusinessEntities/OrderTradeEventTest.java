package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.utils.Variant;

public class OrderTradeEventTest {
	private IMocksControl control;
	private Order o1, o2;
	private Trade t1, t2;
	private EventTypeSI type1, type2;
	private OrderTradeEvent event;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		o1 = control.createMock(Order.class);
		o2 = control.createMock(Order.class);
		t1 = control.createMock(Trade.class);
		t2 = control.createMock(Trade.class);
		type1 = new EventTypeImpl("type1");
		type2 = new EventTypeImpl("type2");
		event = new OrderTradeEvent(type1, o1, t1);
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(event.equals(event));
		assertFalse(event.equals(null));
		assertFalse(event.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<EventTypeSI> vType = new Variant<EventTypeSI>()
			.add(type1)
			.add(type2);
		Variant<Order> vOrder = new Variant<Order>(vType)
			.add(o1)
			.add(o2);
		Variant<Trade> vTrade = new Variant<Trade>(vOrder)
			.add(t1)
			.add(t2);
		Variant<?> iterator = vTrade;
		int foundCnt = 0;
		OrderTradeEvent x = null, found = null;
		do {
			x = new OrderTradeEvent(vType.get(), vOrder.get(), vTrade.get());
			if ( event.equals(x) ) {
				found = x;
				foundCnt ++;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(type1, found.getType());
		assertSame(o1, found.getOrder());
		assertSame(t1, found.getTrade());
	}

}
