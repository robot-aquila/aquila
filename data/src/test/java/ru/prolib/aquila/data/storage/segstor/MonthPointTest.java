package ru.prolib.aquila.data.storage.segstor;

import static org.junit.Assert.*;

import java.time.Month;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class MonthPointTest {
	private MonthPoint point;

	@Before
	public void setUp() throws Exception {
		point = new MonthPoint(1996, Month.AUGUST);
	}
	
	@Test
	public void testCtor2() {
		assertEquals(1996, point.getYear());
		assertEquals(Month.AUGUST, point.getMonth());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(point.equals(point));
		assertFalse(point.equals(null));
		assertFalse(point.equals(this));
	}
	
	@Test
	public void testEquals() {
		Variant<Integer> vYear = new Variant<>(1996, 1764);
		Variant<Month> vMonth = new Variant<>(vYear, Month.AUGUST, Month.JANUARY);
		Variant<?> iterator = vMonth;
		int foundCnt = 0;
		MonthPoint x, found = null;
		do {
			x = new MonthPoint(vYear.get(), vMonth.get());
			if ( point.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(1996, found.getYear());
		assertEquals(Month.AUGUST, found.getMonth());
	}
	
	@Test
	public void testToString() {
		assertEquals("MonthPoint[1996, AUGUST]", point.toString());
	}
	
	@Test
	public void testHashCode() {
		assertEquals(new HashCodeBuilder(9001, 1528693)
				.append(1996)
				.append(Month.AUGUST)
				.toHashCode(), point.hashCode());
	}
	
	@Test
	public void testCompareTo() {
		assertTrue(point.compareTo(new MonthPoint(1996, Month.AUGUST)) == 0);
		assertTrue(point.compareTo(new MonthPoint(1990, Month.AUGUST)) > 0);
		assertTrue(point.compareTo(new MonthPoint(2000, Month.AUGUST)) < 0);
		assertTrue(point.compareTo(new MonthPoint(1996, Month.JANUARY)) > 0);
		assertTrue(point.compareTo(new MonthPoint(1996, Month.DECEMBER)) < 0);
	}
	
	@Test
	public void testGetFirstDatePoint() {
		assertEquals(new DatePoint(2016, 2, 1),
			new MonthPoint(2016, Month.FEBRUARY).getFirstDatePoint());
		assertEquals(new DatePoint(2000, 12, 1),
			new MonthPoint(2000, Month.DECEMBER).getFirstDatePoint());
	}
	
	@Test
	public void testGetLastDatePoint() {
		assertEquals(new DatePoint(2016, 2, 29),
			new MonthPoint(2016, Month.FEBRUARY).getLastDatePoint());
		assertEquals(new DatePoint(2000, 12, 31),
			new MonthPoint(2000, Month.DECEMBER).getLastDatePoint());
	}

}
