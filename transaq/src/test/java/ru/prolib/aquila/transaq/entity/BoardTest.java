package ru.prolib.aquila.transaq.entity;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class BoardTest {
	private Board service;

	@Before
	public void setUp() throws Exception {
		service = new Board("AUCT", "Auction", 1, 2);
	}
	
	@Test
	public void testCtor4() {
		assertEquals("AUCT", service.getCode());
		assertEquals("Auction", service.getName());
		assertEquals(1, service.getMarketID());
		assertEquals(2, service.getTypeID());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(this));
		assertFalse(service.equals(null));
	}
	
	@Test
	public void testEquals() {
		Variant<String> vCode = new Variant<>("AUCT", "ZULU");
		Variant<String> vName = new Variant<>(vCode, "Auction", "Zumbadu");
		Variant<Integer> vMktID = new Variant<>(vName, 1, 595);
		Variant<Integer> vTypID = new Variant<>(vMktID, 2, 9);
		Variant<?> iterator = vTypID;
		int foundCnt = 0;
		Board x, found = null;
		do {
			x = new Board(vCode.get(), vName.get(), vMktID.get(), vTypID.get());
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("AUCT", found.getCode());
		assertEquals("Auction", found.getName());
		assertEquals(1, found.getMarketID());
		assertEquals(2, found.getTypeID());
	}

	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(1365413, 59)
				.append("AUCT")
				.append("Auction")
				.append(1)
				.append(2)
				.build();
		
		assertEquals(expected, service.hashCode());
	}
	
	@Test
	public void testToString() {
		String expected = "Board[code=AUCT,name=Auction,marketID=1,typeID=2]";
		
		assertEquals(expected, service.toString());
	}

}
