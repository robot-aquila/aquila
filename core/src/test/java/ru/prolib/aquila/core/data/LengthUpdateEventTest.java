package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;
import ru.prolib.aquila.core.utils.Variant;

public class LengthUpdateEventTest {
	private EventType type1, type2;
	private LengthUpdateEvent service;

	@Before
	public void setUp() throws Exception {
		type1 = new EventTypeImpl();
		type2 = new EventTypeImpl();
		service = new LengthUpdateEvent(type1, 5, 10);
	}
	
	@Test
	public void testCtor() {
		assertSame(type1, service.getType());
		assertEquals( 5, service.getPrevLength());
		assertEquals(10, service.getCurrLength());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(this));
		assertFalse(service.equals(null));
	}

	@Test
	public void testEquals() {
		Variant<EventType> vType = new Variant<>(type1, type2);
		Variant<Integer> vPrev = new Variant<>(vType, 5, 25), vCurr = new Variant<>(vPrev, 10, 90);
		Variant<?> iterator = vCurr;
		int foundCnt = 0;
		LengthUpdateEvent x, found = null;
		do {
			x = new LengthUpdateEvent(vType.get(), vPrev.get(), vCurr.get());
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(type1, found.getType());
		assertEquals( 5, found.getPrevLength());
		assertEquals(10, found.getCurrLength());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(878614327, 9031)
				.append(type1)
				.append(5)
				.append(10)
				.build();
		
		assertEquals(expected, service.hashCode());
	}
	
	@Test
	public void testToString() {
		service = new LengthUpdateEvent(new EventTypeImpl("MY_EVENT_TYPE"), 3, 4);
		
		String expected = "MY_EVENT_TYPE.LengthUpdate[3->4]";
		assertEquals(expected, service.toString());
	}

}
