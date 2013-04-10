package ru.prolib.aquila.quik.api;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.t2q.T2QConnStatus;

public class ConnEventTest {
	private IMocksControl control;
	private EventType type, type2;
	private ConnEvent event;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		type = control.createMock(EventType.class);
		type2 = control.createMock(EventType.class);
		event = new ConnEvent(type, T2QConnStatus.QUIK_CONN);
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
		Variant<T2QConnStatus> vStat = new Variant<T2QConnStatus>(vType)
			.add(T2QConnStatus.QUIK_CONN)
			.add(T2QConnStatus.DLL_DISC);
		Variant<?> iterator = vStat;
		int foundCnt = 0;
		ConnEvent found = null, x = null;
		do {
			x = new ConnEvent(vType.get(), vStat.get());
			if ( event.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(type, found.getType());
		assertEquals(T2QConnStatus.QUIK_CONN, found.getStatus());
	}

}
