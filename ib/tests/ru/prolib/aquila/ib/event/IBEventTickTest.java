package ru.prolib.aquila.ib.event;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.ib.event.IBEventTick;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.*;
import org.junit.*;

import com.ib.client.TickType;

/**
 * 2012-12-23<br>
 * $Id: IBEventTickTest.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBEventTickTest {
	private static IMocksControl control;
	private static EventType type;
	private static IBEventTick event;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		type = control.createMock(EventType.class);
		event = new IBEventTick(type, 220, TickType.ASK_SIZE, 12.34d);
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testConstruct4() throws Exception {
		assertSame(type, event.getType());
		assertEquals(220, event.getReqId());
		assertEquals(TickType.ASK_SIZE, event.getTickType());
		assertEquals(12.34d, event.getValue(), 0.001d);
	}
	
	@Test
	public void testConstruct2() throws Exception {
		EventType type2 = control.createMock(EventType.class);
		IBEventTick event2 = new IBEventTick(type2, event);
		assertSame(type2, event2.getType());
		assertEquals(220, event2.getReqId());
		assertEquals(TickType.ASK_SIZE, event2.getTickType());
		assertEquals(12.34d, event2.getValue(), 0.001d);
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
			.add(control.createMock(EventType.class));
		Variant<Integer> vReqId = new Variant<Integer>(vType)
			.add(220)
			.add(130);
		Variant<Integer> vTickType = new Variant<Integer>(vReqId)
			.add(TickType.ASK_SIZE)
			.add(528);
		Variant<Double> vVal = new Variant<Double>(vTickType)
			.add(12.34d)
			.add(23.45d);
		Variant<?> iterator = vVal;
		int foundCnt = 0;
		IBEventTick x = null, found = null;
		do {
			x = new IBEventTick(vType.get(), vReqId.get(),
					vTickType.get(), vVal.get());
			if ( event.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(type, found.getType());
		assertEquals(220, found.getReqId());
		assertEquals(TickType.ASK_SIZE, found.getTickType());
		assertEquals(12.34d, found.getValue(), 0.001d);
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder(20121223, 41613)
			.append(type)
			.append(220)
			.append(TickType.ASK_SIZE)
			.append(12.34d)
			.toHashCode(), event.hashCode());
	}

}
