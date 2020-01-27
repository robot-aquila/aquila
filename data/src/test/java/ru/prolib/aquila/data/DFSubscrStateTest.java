package ru.prolib.aquila.data;

import static org.junit.Assert.*;
import static ru.prolib.aquila.data.DFSubscrStatus.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.data.DFSubscrState;
import ru.prolib.aquila.data.DFSubscrStatus;

public class DFSubscrStateTest {
	private DFSubscrState service;

	@Before
	public void setUp() throws Exception {
		service = new DFSubscrState();
	}
	
	private static List<DFSubscrStatus> tol(DFSubscrStatus... to_status) {
		List<DFSubscrStatus> list = new ArrayList<>();
		for ( DFSubscrStatus status : to_status ) {
			list.add(status);
		}
		return list;
	}
	
	private void testAllowedCombs(DFSubscrStatus from_status, List<DFSubscrStatus> to_status) {
		for ( DFSubscrStatus status : to_status ) {
			//System.out.println("ALLOWED? From " + from_status + " To " + status);
			service = new DFSubscrState(from_status);
			try {
				service.switchTo(status);
				assertEquals(status, service.getStatus());
			} catch ( IllegalStateException e ) {
				fail("Expected without exceptions while switching from " + from_status + " to " + status);
			}
		}
	}
	
	private void testProhibitedCombs(DFSubscrStatus from_status, List<DFSubscrStatus> to_status) {
		for ( DFSubscrStatus status : to_status ) {
			//System.out.println("PROHIBITED? From " + from_status + " To " + status);
			service = new DFSubscrState(from_status);
			try {
				service.switchTo(status);
				fail("Expected expection while switching from " + from_status + " to " + status);
			} catch ( IllegalStateException e ) {
				assertEquals("Cannot switch from " + from_status + " to " + status, e.getMessage());
				assertEquals(from_status, service.getStatus());
			}
		}
	}
	
	private void testSwitchings(DFSubscrStatus from_status, List<DFSubscrStatus> allowed_to) {
		List<DFSubscrStatus> prohibited_to = new ArrayList<>();
		for ( DFSubscrStatus status : tol(DFSubscrStatus.values()) ) {
			if ( ! allowed_to.contains(status) ) {
				prohibited_to.add(status);
			}
		}
		testAllowedCombs(from_status, allowed_to);
		testProhibitedCombs(from_status, prohibited_to);
	}
	
	@Test
	public void testCtor1() {
		service = new DFSubscrState(DFSubscrStatus.PENDING_SUBSCR);
		assertEquals(DFSubscrStatus.PENDING_SUBSCR, service.getStatus());
	}
	
	@Test
	public void testCtor0_DefaultStatus() {
		assertEquals(DFSubscrStatus.NOT_SUBSCR, service.getStatus());
	}
	
	@Test
	public void testSwitchTo_() {
		testSwitchings(NOT_SUBSCR,		tol(NOT_SUBSCR, PENDING_SUBSCR, NOT_AVAILABLE));
		testSwitchings(PENDING_SUBSCR,	tol(NOT_SUBSCR, PENDING_SUBSCR, SUBSCR, NOT_AVAILABLE));
		testSwitchings(SUBSCR,			tol(NOT_SUBSCR, SUBSCR, PENDING_UNSUBSCR, NOT_AVAILABLE));
		testSwitchings(PENDING_UNSUBSCR,tol(NOT_SUBSCR, SUBSCR, PENDING_UNSUBSCR, NOT_AVAILABLE));
		testSwitchings(NOT_AVAILABLE,	tol(NOT_AVAILABLE));
	}

}
