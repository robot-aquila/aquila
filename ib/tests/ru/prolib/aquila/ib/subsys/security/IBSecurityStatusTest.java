package ru.prolib.aquila.ib.subsys.security;

import static org.junit.Assert.*;

import org.junit.*;

import ru.prolib.aquila.ib.subsys.security.IBSecurityStatus;

/**
 * 2012-11-20<br>
 * $Id: IBSecurityStatusTest.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBSecurityStatusTest {
	
	@Test
	public void testConst() throws Exception {
		assertEquals("None", IBSecurityStatus.NONE.getCode());
		assertEquals("Sent", IBSecurityStatus.SENT.getCode());
		assertEquals("NotFnd", IBSecurityStatus.NFND.getCode());
		assertEquals("Done", IBSecurityStatus.DONE.getCode());
	}

}
