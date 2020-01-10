package ru.prolib.aquila.core.eqs;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class CmdAddCountTest {
	private CmdAddCount service;

	@Before
	public void setUp() throws Exception {
		service = new CmdAddCount(827L, 340L, 216L);
	}
	
	@Test
	public void testGetters() {
		assertEquals(CmdType.ADD_COUNT, service.getType());
		assertEquals(Long.valueOf(827L), service.getEnqueued());
		assertEquals(Long.valueOf(340L), service.getSent());
		assertEquals(Long.valueOf(216L), service.getDispatched());
	}
	
	@Test
	public void testToString() {
		String expected = "CmdAddCount[enqueued=827,sent=340,dispatched=216]";
		
		assertEquals(expected, service.toString());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(1152009, 37)
				.append(827L)
				.append(340L)
				.append(216L)
				.build();
		
		assertEquals(expected, service.hashCode());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(this));
		assertFalse(service.equals(null));
	}

	@Test
	public void testEquals() {
		Variant<Long>
			vEnq = new Variant<>(827L, 441L, null),
			vSnt = new Variant<>(vEnq, 340L, 112L, null),
			vDsp = new Variant<>(vSnt, 216L, 107L, null);
		Variant<?> iterator = vDsp;
		int found_cnt = 0;
		CmdAddCount x, found = null;
		do {
			x = new CmdAddCount(vEnq.get(), vSnt.get(), vDsp.get());
			if ( service.equals(x) ) {
				found_cnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, found_cnt);
		assertEquals(Long.valueOf(827L), found.getEnqueued());
		assertEquals(Long.valueOf(340L), found.getSent());
		assertEquals(Long.valueOf(216L), found.getDispatched());
	}

}
