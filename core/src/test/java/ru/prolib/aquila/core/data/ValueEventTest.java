package ru.prolib.aquila.core.data;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.data.ValueEvent;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-04-28<br>
 * $Id: ValueEventTest.java 556 2013-03-04 17:18:03Z whirlwind $
 */
public class ValueEventTest {
	private IMocksControl control;
	private EventType type;
	private ValueEvent<Double> event;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		type = control.createMock(EventType.class);
		event = new ValueEvent<Double>(type, 123.45d, 123);
	}
	
	@Test
	public void testConstruct_Ok() throws Exception {
		assertSame(type, event.getType());
		assertEquals(123.45d, event.getNewValue(), 0.001d);
		assertEquals(123, event.getValueIndex());
	}
	
	@Test
	public void testConstruct_ValueIsNullOk() throws Exception {
		event = new ValueEvent<Double>(type, null, 123);
		assertSame(type, event.getType());
		assertNull(event.getNewValue());
		assertEquals(123, event.getValueIndex());
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfTypeIsNull() throws Exception {
		new ValueEvent<Double>(null, 123.45d, 123);
	}
	
	@Test
	public void testConstruct4() throws Exception {
		event = new ValueEvent<Double>(type, 123.45d, 200.00d, 321);
		assertSame(type, event.getType());
		assertEquals(123.45d, event.getOldValue(), 0.001d);
		assertEquals(200.00d, event.getNewValue(), 0.001d);
		assertEquals(321, event.getValueIndex());
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
		Variant<Double> vOld = new Variant<Double>(vType)
			.add(null)
			.add(157.21d);
		Variant<Double> vNew = new Variant<Double>(vOld)
			.add(123.45d)
			.add(827.18d);
		Variant<Integer> vIdx = new Variant<Integer>(vNew)
			.add(123)
			.add(543);
		Variant<?> iterator = vIdx;
		int foundCnt = 0;
		ValueEvent<Double> found = null, x = null;
		do {
			x = new ValueEvent<Double>(vType.get(), vOld.get(), vNew.get(),
					vIdx.get());
			if ( event.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(type, found.getType());
		assertNull(null, found.getOldValue());
		assertEquals(123.45d, found.getNewValue(), 0.001d);
		assertEquals(123, found.getValueIndex());
	}

}
