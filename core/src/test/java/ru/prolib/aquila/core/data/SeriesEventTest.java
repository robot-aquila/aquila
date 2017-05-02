package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;
import ru.prolib.aquila.core.utils.Variant;

public class SeriesEventTest {
	private EventType type;
	private SeriesEvent<Double> event;

	@Before
	public void setUp() throws Exception {
		type = new EventTypeImpl("zulu24");
		event = new SeriesEvent<>(type, 500, 26.78d);
	}

	@Test
	public void testGetters() {
		assertSame(type, event.getType());
		assertEquals(500, event.getIndex());
		assertEquals(26.78d, event.getValue(), 0.001d);
	}
	
	@Test
	public void testToString() {
		assertEquals("SeriesEvent[zulu24@500 26.78]", event.toString());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(event.equals(event));
		assertFalse(event.equals(null));
		assertFalse(event.equals(this));
	}
	
	@Test
	public void testEquals() {
		Variant<EventType> vType = new Variant<>(type, new EventTypeImpl());
		Variant<Integer> vIdx = new Variant<>(vType, 500, 250);
		Variant<Double> vVal = new Variant<>(vIdx, 26.78d, 14.08);
		Variant<?> iterator = vVal;
		int foundCnt = 0;
		SeriesEvent<Double> x, found = null;
		do {
			x = new SeriesEvent<>(vType.get(), vIdx.get(), vVal.get());
			if ( event.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(type, found.getType());
		assertEquals(500, found.getIndex());
		assertEquals(26.78d, found.getValue(), 0.0001d);
	}

}
