package ru.prolib.aquila.exante;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class XSymbolTest {
	private XSymbol service;

	@Before
	public void setUp() throws Exception {
		service = new XSymbol("AAPL", "AAPL.NASDAQ", "NASDAQ", "EXXXXX", "USD");
	}
	
	@Test
	public void testCtor5() {
		assertEquals("AAPL", service.getSymbol());
		assertEquals("AAPL.NASDAQ", service.getSecurityID());
		assertEquals("NASDAQ", service.getExchangeID());
		assertEquals("EXXXXX", service.getCFICode());
		assertEquals("USD", service.getCurrency());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(this));
		assertFalse(service.equals(null));
	}
	
	@Test
	public void testEquals() {
		Variant<String>
			vSym = new Variant<>("AAPL", "MSFT"),
			vSecID = new Variant<>("AAPL.NASDAQ", "MSFT.NYSE"),
			vExcID = new Variant<>("NASDAQ", "NYSE"),
			vCFI = new Variant<>("EXXXXX", "FXXXXX"),
			vCur = new Variant<>("USD", "EUR");
		Variant<?> iterator = vCur;
		int found_cnt = 0;
		XSymbol x, found = null;
		do {
			x = new XSymbol(vSym.get(), vSecID.get(), vExcID.get(), vCFI.get(), vCur.get());
			if ( service.equals(x) ) {
				found_cnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, found_cnt);
		assertEquals("AAPL", found.getSymbol());
		assertEquals("AAPL.NASDAQ", found.getSecurityID());
		assertEquals("NASDAQ", found.getExchangeID());
		assertEquals("EXXXXX", found.getCFICode());
		assertEquals("USD", found.getCurrency());
	}

	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(728181, 432763)
				.append("AAPL")
				.append("AAPL.NASDAQ")
				.append("NASDAQ")
				.append("EXXXXX")
				.append("USD")
				.build();
		
		assertEquals(expected, service.hashCode());
	}
	
	@Test
	public void testToString() {
		String expected = "XSymbol[symbol=AAPL,securityID=AAPL.NASDAQ,exchangeID=NASDAQ,cfi=EXXXXX,currency=USD]";
		
		assertEquals(expected, service.toString());
	}

}
