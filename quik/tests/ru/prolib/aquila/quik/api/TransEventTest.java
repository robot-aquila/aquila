package ru.prolib.aquila.quik.api;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.t2q.T2QTransStatus;

public class TransEventTest {
	private IMocksControl control;
	private EventType type, type2;
	private TransEvent event;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		type = control.createMock(EventType.class);
		type2 = control.createMock(EventType.class);
		event = new TransEvent(type, T2QTransStatus.ERR_LIMIT, 1L, 50L, "test");
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
		Variant<T2QTransStatus> vStat = new Variant<T2QTransStatus>(vType)
			.add(T2QTransStatus.DONE)
			.add(T2QTransStatus.ERR_LIMIT);
		Variant<Long> vTrnId = new Variant<Long>(vStat)
			.add(500L)
			.add(1L);
		Variant<Long> vOrdId = new Variant<Long>(vTrnId)
			.add(null)
			.add(50L);
		Variant<String> vMsg = new Variant<String>(vOrdId)
			.add("test")
			.add("bar");
		Variant<?> iterator = vMsg;
		int foundCnt = 0;
		TransEvent x = null, found = null;
		do {
			x = new TransEvent(vType.get(), vStat.get(), vTrnId.get(),
					vOrdId.get(), vMsg.get());
			if ( event.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(type, found.getType());
		assertEquals(T2QTransStatus.ERR_LIMIT, found.getStatus());
		assertEquals(1L, found.getTransId());
		assertEquals(new Long(50), found.getOrderId());
		assertEquals("test", found.getMessage());
	}

}
