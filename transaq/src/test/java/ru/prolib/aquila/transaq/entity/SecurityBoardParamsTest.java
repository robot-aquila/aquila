package ru.prolib.aquila.transaq.entity;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.utils.Variant;

public class SecurityBoardParamsTest {
	private SecurityBoardParams service;

	@Before
	public void setUp() throws Exception {
		service = new SecurityBoardParams(2, of("0.01"), of("100"), ofRUB5("0.1"));
	}
	
	@Test
	public void testCtor4() {
		assertEquals(2, service.getDecimals());
		assertEquals(of("0.01"), service.getTickSize());
		assertEquals(of("100"), service.getLotSize());
		assertEquals(ofRUB5("0.1"), service.getTickValue());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testEquals() {
		Variant<Integer> vDec = new Variant<>(2, 5);
		Variant<CDecimal> vTSz = new Variant<>(vDec, of("0.01"), of("0.50"));
		Variant<CDecimal> vLSz = new Variant<>(vTSz, of("100"), of("1"));
		Variant<CDecimal> vTVa = new Variant<>(vLSz, ofRUB5("0.1"), ofUSD2("20"));
		Variant<?> iterator = vTVa;
		int foundCnt = 0;
		SecurityBoardParams x, found = null;
		do {
			x = new SecurityBoardParams(vDec.get(), vTSz.get(), vLSz.get(), vTVa.get());
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(2, found.getDecimals());
		assertEquals(of("0.01"), found.getTickSize());
		assertEquals(of("100"), found.getLotSize());
		assertEquals(ofRUB5("0.1"), found.getTickValue());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(9817239, 19201)
				.append(2)
				.append(of("0.01"))
				.append(of("100"))
				.append(ofRUB5("0.1"))
				.build();
		
		assertEquals(expected, service.hashCode());
	}

	@Test
	public void testToString() {
		String expected = new StringBuilder()
				.append("SecurityBoardParams[")
				.append("decimals=2,")
				.append("tickSize=0.01,")
				.append("lotSize=100,")
				.append("tickValue=0.10000 RUB")
				.append("]")
				.toString();
		
		assertEquals(expected, service.toString());
	}

}
