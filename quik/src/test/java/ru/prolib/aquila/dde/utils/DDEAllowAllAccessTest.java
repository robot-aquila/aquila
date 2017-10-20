package ru.prolib.aquila.dde.utils;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.*;

/**
 * 2012-1106<br>
 * $Id: DDEAllowAllAccessTest.java 304 2012-11-06 09:17:07Z whirlwind $
 */
public class DDEAllowAllAccessTest {
	private DDEAccessSubject subj;
	private DDEAllowAllAccess ctrl;

	@Before
	public void setUp() throws Exception {
		subj = new DDEAccessSubject("foo", "bar");
		ctrl = new DDEAllowAllAccess();
	}
	
	@Test
	public void testIsAllowed() throws Exception {
		assertTrue(ctrl.isAllowed(subj));
		assertTrue(ctrl.isAllowed(null));
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(ctrl.equals(ctrl));
		assertTrue(ctrl.equals(new DDEAllowAllAccess()));
		assertFalse(ctrl.equals(this));
		assertFalse(ctrl.equals(null));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121107, /*0*/71705).toHashCode();
		assertEquals(hashCode, ctrl.hashCode());
	}

}
