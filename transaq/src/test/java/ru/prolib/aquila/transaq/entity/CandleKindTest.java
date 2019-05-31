package ru.prolib.aquila.transaq.entity;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class CandleKindTest {
	private CandleKind service;

	@Before
	public void setUp() throws Exception {
		service = new CandleKind(14, 300, "5 minutes");
	}
	
	@Test
	public void testCtor3() {
		assertEquals(14, service.getID());
		assertEquals(300, service.getPeriod());
		assertEquals("5 minutes", service.getName());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testEquals() {
		Variant<Integer> vID = new Variant<>(14, 22);
		Variant<Integer> vPer = new Variant<>(vID, 300, 1200);
		Variant<String> vName = new Variant<>(vPer, "5 minutes", "foobar");
		Variant<?> iterator = vName;
		int foundCnt = 0;
		CandleKind x, found = null;
		do {
			x = new CandleKind(vID.get(), vPer.get(), vName.get());
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(14, found.getID());
		assertEquals(300, found.getPeriod());
		assertEquals("5 minutes", found.getName());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(65232311, 7751)
				.append(14)
				.append(300)
				.append("5 minutes")
				.build();
		
		assertEquals(expected, service.hashCode());
	}

	@Test
	public void testToString() {
		String expected = "CandleKind[id=14,period=300,name=5 minutes]";
		
		assertEquals(expected, service.toString());
	}

}
