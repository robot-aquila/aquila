package ru.prolib.aquila.core.data.tseries;

import static org.junit.Assert.*;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;
import org.threeten.extra.Interval;

import ru.prolib.aquila.core.data.TSeriesUpdate;
import ru.prolib.aquila.core.utils.Variant;

public class TSeriesUpdateImplTest {
	private Interval interval1, interval2;
	private TSeriesUpdateImpl update;
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}

	@Before
	public void setUp() throws Exception {
		interval1 = Interval.of(T("2017-08-21T21:40:00Z"), T("2017-08-21T21:45:00Z"));
		interval2 = Interval.of(T("2017-08-21T21:45:00Z"), T("2017-08-21T21:50:00Z"));
		update = new TSeriesUpdateImpl(interval1);
	}
	
	@Test
	public void testCtor() {
		assertEquals(interval1, update.getInterval());
		assertEquals(-1, update.getNodeIndex());
	}
	
	@Test
	public void testSetNewNode() {
		assertFalse(update.isNewNode());
		assertSame(update, update.setNewNode());
		assertTrue(update.isNewNode());
		assertSame(update, update.setNewNode(false));
		assertFalse(update.isNewNode());
		assertSame(update, update.setNewNode(true));
		assertTrue(update.isNewNode());
	}
	
	@Test
	public void testSetNodeIndex() {
		assertEquals(-1, update.getNodeIndex());
		assertSame(update, update.setNodeIndex(12));
		assertEquals(12, update.getNodeIndex());
	}
	
	@Test
	public void testSetOldValue() {
		assertNull(update.getOldValue());
		assertSame(update, update.setOldValue(215));
		assertEquals(215, update.getOldValue());
	}
	
	@Test
	public void testSetNewValue() {
		assertNull(update.getNewValue());
		assertSame(update, update.setNewValue(826));
		assertEquals(826, update.getNewValue());
	}
	
	@Test
	public void testHasChanged() {
		assertFalse(update.hasChanged());
		update.setNewValue(215).setOldValue(215);
		assertFalse(update.hasChanged());
		update.setNewValue(null).setOldValue(215);
		assertTrue(update.hasChanged());
		update.setNewValue(215).setOldValue(null);
		assertTrue(update.hasChanged());
		update.setNewValue(null).setOldValue(null);
		assertFalse(update.hasChanged());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertFalse(update.equals(null));
		assertFalse(update.equals(this));
		assertTrue(update.equals(update));
	}

	@Test
	public void testEquals() {
		update.setNewNode(true)
			.setNodeIndex(256)
			.setOldValue(15)
			.setNewValue(25);
		Variant<Interval> vInt = new Variant<>(interval1, interval2);
		Variant<Boolean> vNew = new Variant<>(vInt, true, false);
		Variant<Integer> vInd = new Variant<>(vNew, 256, 400);
		Variant<Integer> vOVa = new Variant<>(vInd, 15, 58, null);
		Variant<Integer> vNVa = new Variant<>(vOVa, 25, 63, null);
		Variant<?> iterator = vNVa;
		int foundCnt = 0;
		TSeriesUpdate x, found = null;
		do {
			x = new TSeriesUpdateImpl(vInt.get())
					.setNewNode(vNew.get())
					.setNodeIndex(vInd.get())
					.setOldValue(vOVa.get())
					.setNewValue(vNVa.get());
			if ( update.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(interval1, found.getInterval());
		assertTrue(found.isNewNode());
		assertEquals(256, found.getNodeIndex());
		assertEquals(15, found.getOldValue());
		assertEquals(25, found.getNewValue());
	}

}
