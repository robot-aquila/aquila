package ru.prolib.aquila.data;

import static org.junit.Assert.*;
import static ru.prolib.aquila.data.DFSubscrStatus.*;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class DFGroupTest {
	private static final int FEED1 = 11;
	private static final int FEED2 = 12;
	private static final int FEED3 = 13;
	private static final int FEED4 = 14;
	private DFGroup<String, Integer> service;
	private Map<Integer, DFSubscrState> states;

	@Before
	public void setUp() throws Exception {
		states = new LinkedHashMap<>();
		states.put(FEED1, new DFSubscrState());
		states.put(FEED2, new DFSubscrState());
		states.put(FEED3, new DFSubscrState());
		states.put(FEED4, new DFSubscrState());
		service = new DFGroup<>("foo", states);
	}
	
	@Test
	public void testGetters() {
		assertEquals("foo", service.getKey());
		assertFalse(service.isNotFound());
	}
	
	@Test
	public void testSetNotFound() {
		assertFalse(service.isNotFound());
		service.setNotFound(true);
		assertTrue(service.isNotFound());
		service.setNotFound(false);
		assertFalse(service.isNotFound());
	}
	
	@Test
	public void testGetFeedStatus() {
		states.put(FEED1, new DFSubscrState(SUBSCR));
		states.put(FEED2, new DFSubscrState(PENDING_UNSUBSCR));
		states.put(FEED3, new DFSubscrState(PENDING_SUBSCR));
		states.put(FEED4, new DFSubscrState(NOT_AVAILABLE));
		
		assertEquals(SUBSCR, service.getFeedStatus(FEED1));
		assertEquals(PENDING_UNSUBSCR, service.getFeedStatus(FEED2));
		assertEquals(PENDING_SUBSCR, service.getFeedStatus(FEED3));
		assertEquals(NOT_AVAILABLE, service.getFeedStatus(FEED4));
	}

	@Test
	public void testSetFeedStatus() {
		assertNotEquals(PENDING_SUBSCR, states.get(FEED1).getStatus());
		assertNotEquals(NOT_AVAILABLE, states.get(FEED2).getStatus());
		
		service.setFeedStatus(FEED1, PENDING_SUBSCR);
		service.setFeedStatus(FEED2, NOT_AVAILABLE);
		
		assertEquals(PENDING_SUBSCR, states.get(FEED1).getStatus());
		assertEquals(NOT_AVAILABLE, states.get(FEED2).getStatus());
	}
	
	@Test
	public void testMarkAllNotSubscribed() {
		states.put(FEED1, new DFSubscrState(SUBSCR));
		states.put(FEED2, new DFSubscrState(PENDING_UNSUBSCR));
		states.put(FEED3, new DFSubscrState(PENDING_SUBSCR));
		states.put(FEED4, new DFSubscrState(NOT_AVAILABLE));
		
		service.markAllNotSubscribed();
		
		assertEquals(NOT_SUBSCR, service.getFeedStatus(FEED1));
		assertEquals(NOT_SUBSCR, service.getFeedStatus(FEED2));
		assertEquals(NOT_SUBSCR, service.getFeedStatus(FEED3));
		assertEquals(NOT_AVAILABLE, service.getFeedStatus(FEED4));
	}
	
	@Test
	public void testMarkToSubscribe() {
		Object fixture[][] = {
				// from status,		expected status,	expected return
				{ NOT_SUBSCR,		PENDING_SUBSCR,		true },
				{ PENDING_SUBSCR,	PENDING_SUBSCR,		true },
				{ PENDING_UNSUBSCR,	SUBSCR,				false },
				{ SUBSCR,			SUBSCR,				false },
				{ NOT_AVAILABLE,	NOT_AVAILABLE,		false },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			states.put(FEED1, new DFSubscrState((DFSubscrStatus) fixture[i][0]));
			DFSubscrStatus expected_status = (DFSubscrStatus) fixture[i][1];
			boolean expected_return = (boolean) fixture[i][2];
			assertEquals("At#" + i, expected_return, service.markToSubscribe(FEED1));
			assertEquals("At#" + i, expected_status, service.getFeedStatus(FEED1));
		}
	}

	@Test
	public void testMarkToUnsubscribe() {
		Object fixture[][] = {
				// from status,		expected status,	expected return
				{ NOT_SUBSCR,		NOT_SUBSCR,			false },
				{ PENDING_SUBSCR,	NOT_SUBSCR,			false },
				{ PENDING_UNSUBSCR, PENDING_UNSUBSCR,   true },
				{ SUBSCR,			PENDING_UNSUBSCR,	true },
				{ NOT_AVAILABLE,	NOT_AVAILABLE,		false },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			states.put(FEED1, new DFSubscrState((DFSubscrStatus) fixture[i][0]));
			DFSubscrStatus expected_status = (DFSubscrStatus) fixture[i][1];
			boolean expected_return = (boolean) fixture[i][2];
			assertEquals("At#" + i, expected_return, service.markToUnsubscribe(FEED1));
			assertEquals("At#" + i, expected_status, service.getFeedStatus(FEED1));
		}
	}

}
