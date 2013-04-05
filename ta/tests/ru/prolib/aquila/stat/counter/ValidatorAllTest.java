package ru.prolib.aquila.stat.counter;


import static org.junit.Assert.*;

import org.junit.*;

/**
 * 2012-02-06
 * $Id: ValidatorAllTest.java 198 2012-02-06 13:04:25Z whirlwind $
 */
public class ValidatorAllTest {
	ValidatorAll validator;

	@Before
	public void setUp() throws Exception {
		validator = new ValidatorAll();
	}
	
	@Test
	public void testShouldCounted() throws Exception {
		assertTrue(validator.shouldCounted(123d));
		assertTrue(validator.shouldCounted(321d));
		assertTrue(validator.shouldCounted(-12.345d));
	}

}
