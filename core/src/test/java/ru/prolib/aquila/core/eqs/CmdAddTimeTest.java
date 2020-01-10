package ru.prolib.aquila.core.eqs;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class CmdAddTimeTest {
	private CmdAddTime service;

	@Before
	public void setUp() throws Exception {
		service = new CmdAddTime(827L, 340L, 216L);
	}
	
	@Test
	public void testGetters() {
		assertEquals(CmdType.ADD_TIME, service.getType());
		assertEquals(Long.valueOf(827L), service.getPreparing());
		assertEquals(Long.valueOf(340L), service.getDispatching());
		assertEquals(Long.valueOf(216L), service.getDelivery());
	}
	
	@Test
	public void testToString() {
		String expected = "CmdAddTime[preparing=827,dispatching=340,delivery=216]";
		
		assertEquals(expected, service.toString());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(7915633, 71)
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
			vPre = new Variant<>(827L, 441L, null),
			vDsp = new Variant<>(vPre, 340L, 112L, null),
			vDlv = new Variant<>(vDsp, 216L, 107L, null);
		Variant<?> iterator = vDlv;
		int found_cnt = 0;
		CmdAddTime x, found = null;
		do {
			x = new CmdAddTime(vPre.get(), vDsp.get(), vDlv.get());
			if ( service.equals(x) ) {
				found_cnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, found_cnt);
		assertEquals(Long.valueOf(827L), found.getPreparing());
		assertEquals(Long.valueOf(340L), found.getDispatching());
		assertEquals(Long.valueOf(216L), found.getDelivery());
	}

}
