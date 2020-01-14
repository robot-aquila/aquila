package ru.prolib.aquila.core.eqs.v4;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class V4QueueStatsTest {
	private V4QueueStats service;

	@Before
	public void setUp() throws Exception {
		service = new V4QueueStats(400L, 162L, 772L, 1762L, 6611L, 1822L);
	}
	
	@Test
	public void testGetters() {
		assertEquals(400L, service.getTotalEventsEnqueued());
		assertEquals(162L, service.getTotalEventsSent());
		assertEquals(772L, service.getTotalEventsDispatched());
		assertEquals(1762L, service.getPreparingTime());
		assertEquals(6611L, service.getDispatchingTime());
		assertEquals(1822L, service.getDeliveryTime());
	}
	
	@Test
	public void testToString() {
		String expected = "V4QueueStats[enqueued=400,sent=162,dispatched=772,preparingTime=1762,"
				+ "dispatchingTime=6611,deliveryTime=1822]";
		
		assertEquals(expected, service.toString());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(99016125, 761)
				.append(400L)
				.append(162L)
				.append(772L)
				.append(1762L)
				.append(6611L)
				.append(1822L)
				.build();
		
		assertEquals(expected, service.hashCode());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}

	@Test
	public void testEquals() {
		Variant<Long> vEnq = new Variant<>(400L, 211L),
				vSnt = new Variant<>(vEnq, 162L, 215L),
				vDsp = new Variant<>(vSnt, 772L, 924L),
				vTPrep = new Variant<>(vDsp, 1762L, 8871L),
				vTDisp = new Variant<>(vTPrep, 6611L, 4611L),
				vTDlvr = new Variant<>(vTDisp, 1822L, 4463L);
		Variant<?> iterator = vTDlvr;
		int found_cnt = 0;
		V4QueueStats x, found = null;
		do {
			x = new V4QueueStats(vEnq.get(), vSnt.get(), vDsp.get(), vTPrep.get(), vTDisp.get(), vTDlvr.get());
			if ( service.equals(x) ) {
				found_cnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, found_cnt);
		assertEquals(400L, found.getTotalEventsEnqueued());
		assertEquals(162L, found.getTotalEventsSent());
		assertEquals(772L, found.getTotalEventsDispatched());
		assertEquals(1762L, found.getPreparingTime());
		assertEquals(6611L, found.getDispatchingTime());
		assertEquals(1822L, found.getDeliveryTime());
	}

}
