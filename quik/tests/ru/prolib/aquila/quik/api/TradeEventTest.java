package ru.prolib.aquila.quik.api;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.t2q.*;

public class TradeEventTest {
	private IMocksControl control;
	private EventType type, type2;
	private T2QTrade trade, trade2;
	private TradeEvent event;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		type = control.createMock(EventType.class);
		type2 = control.createMock(EventType.class);
		trade = control.createMock(T2QTrade.class);
		trade2 = control.createMock(T2QTrade.class);
		event = new TradeEvent(type, trade);
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
		Variant<T2QTrade> vTrd = new Variant<T2QTrade>(vType)
			.add(trade)
			.add(trade2);
		Variant<?> iterator = vTrd;
		int foundCnt = 0;
		TradeEvent found = null, x = null;
		do {
			x = new TradeEvent(vType.get(), vTrd.get());
			if ( event.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(type, found.getType());
		assertEquals(trade, found.getTrade());
	}

}
