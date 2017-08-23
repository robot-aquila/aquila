package ru.prolib.aquila.core.data.tseries;

import static org.junit.Assert.*;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;
import org.threeten.extra.Interval;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;
import ru.prolib.aquila.core.data.TSeriesUpdate;
import ru.prolib.aquila.core.utils.Variant;

public class TSeriesEventImplTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private EventType type1, type2;
	private Interval interval1, interval2;
	private TSeriesUpdateImpl update1, update2;
	private TSeriesEventImpl<Integer> event;

	@Before
	public void setUp() throws Exception {
		type1 = new EventTypeImpl();
		type2 = new EventTypeImpl();
		interval1 = Interval.of(T("2017-08-23T18:30:00Z"), T("2017-08-23T18:35:00Z"));
		interval2 = Interval.of(T("2017-08-23T18:35:00Z"), T("2017-08-23T18:40:00Z"));
		update1 = new TSeriesUpdateImpl(interval1);
		update2 = new TSeriesUpdateImpl(interval2);
		event = new TSeriesEventImpl<Integer>(type1, update1);
	}
	
	@Test
	public void testIsType() {
		assertTrue(event.isType(type1));
		assertFalse(event.isType(type2));
	}
	
	@Test
	public void testGetType() {
		assertSame(type1, event.getType());
	}
	
	@Test
	public void testIsNewInterval() {
		update1.setNewNode(true);
		assertTrue(event.isNewInterval());
		update1.setNewNode(false);
		assertFalse(event.isNewInterval());
	}
	
	@Test
	public void testGetOldValue() {
		assertNull(event.getOldValue());
		update1.setOldValue(12);
		assertEquals(12, (int) event.getOldValue());
	}
	
	@Test
	public void testGetNewValue() {
		assertNull(event.getNewValue());
		update1.setNewValue(48);
		assertEquals(48, (int) event.getNewValue());
	}
	
	@Test
	public void testGetIndex() {
		update1.setNodeIndex(826);
		assertEquals(826, event.getIndex());
	}
	
	@Test
	public void testGetInterval() {
		assertEquals(interval1, event.getInterval());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(event.equals(event));
		assertFalse(event.equals(null));
		assertFalse(event.equals(this));
	}

	@Test
	public void testEquals() {
		Variant<EventType> vTyp = new Variant<>(type1, type2);
		Variant<TSeriesUpdate> vUpd = new Variant<>(vTyp, update1, update2);
		Variant<?> iterator = vUpd;
		int foundCnt = 0;
		TSeriesEventImpl<Integer> x, found = null;
		do {
			x = new TSeriesEventImpl<>(vTyp.get(), vUpd.get());
			if ( event.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(type1, found.getType());
		assertSame(update1, found.getUpdate());
	}

}
