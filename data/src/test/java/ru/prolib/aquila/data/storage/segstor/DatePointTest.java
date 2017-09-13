package ru.prolib.aquila.data.storage.segstor;

import static org.junit.Assert.*;

import java.time.LocalDate;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class DatePointTest {
	private DatePoint point;

	@Before
	public void setUp() throws Exception {
		point = new DatePoint(2017, 9, 11);
	}
	
	@Test
	public void testCtor3() {
		assertEquals(LocalDate.of(2017,  9, 11), point.getDate());
	}
	
	@Test
	public void testCtor1() {
		point = new DatePoint(LocalDate.of(1998, 6, 15));
		assertEquals(LocalDate.of(1998, 6, 15), point.getDate());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(point.equals(point));
		assertFalse(point.equals(null));
		assertFalse(point.equals(this));
	}
	
	@Test
	public void testEquals() {
		Variant<Integer> vYear = new Variant<>(2017, 1998);
		Variant<Integer> vMonth = new Variant<>(vYear, 9, 6);
		Variant<Integer> vDoM = new Variant<>(vMonth, 11, 15);
		Variant<?> iterator = vDoM;
		int foundCnt = 0;
		DatePoint x, found = null;
		do {
			x = new DatePoint(vYear.get(), vMonth.get(), vDoM.get());
			if ( point.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(LocalDate.of(2017, 9, 11), found.getDate());
	}
	
	@Test
	public void testToString() {
		assertEquals("DatePoint[2017-09-11]", point.toString());
	}
	
	@Test
	public void testHashCode() {
		assertEquals(new HashCodeBuilder(9113, 46815)
				.append(LocalDate.of(2017, 9, 11))
				.toHashCode(), point.hashCode());
	}
	
	@Test
	public void testCompareTo() {
		assertEquals( 0, point.compareTo(new DatePoint(2017, 9, 11)));
		assertEquals(-1, point.compareTo(new DatePoint(2017, 9, 12)));
		assertEquals( 1, point.compareTo(new DatePoint(2017, 9, 10)));
	}

}
