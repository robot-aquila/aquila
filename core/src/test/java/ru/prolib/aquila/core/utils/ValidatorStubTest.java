package ru.prolib.aquila.core.utils;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.*;

/**
 * 2012-12-03<br>
 * $Id: ValidatorStubTest.java 327 2012-12-05 19:58:26Z whirlwind $
 */
public class ValidatorStubTest {
	private static ValidatorStub v1 = new ValidatorStub(true);
	private static ValidatorStub v2 = new ValidatorStub(false);

	@Test
	public void testValidate() throws Exception {
		assertTrue(v1.validate(null));
		assertFalse(v2.validate(null));
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder(20121203, 4033)
			.append(true)
			.toHashCode(), v1.hashCode());
		assertEquals(new HashCodeBuilder(20121203, 4033)
			.append(false)
			.toHashCode(), v2.hashCode());
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(v1.equals(v1));
		assertTrue(v1.equals(new ValidatorStub(true)));
		assertFalse(v1.equals(v2));
		assertFalse(v1.equals(null));
		assertFalse(v1.equals(this));
	}

}
