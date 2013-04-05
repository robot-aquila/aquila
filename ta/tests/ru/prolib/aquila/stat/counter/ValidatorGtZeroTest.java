package ru.prolib.aquila.stat.counter;


import static org.junit.Assert.*;

import org.junit.*;

/**
 * 2012-02-06
 * $Id: ValidatorGtZeroTest.java 198 2012-02-06 13:04:25Z whirlwind $
 */
public class ValidatorGtZeroTest {
	ValidatorGtZero validator;

	@Before
	public void setUp() throws Exception {
		validator = new ValidatorGtZero();
	}
	
	@Test
	public void testShouldCounted() throws Exception {
		Object fix[][] = {
			// value, expected result
			{ 100d, true  },
			{   0d, false },
			{ -50d, false },
			{ 0.00001d, true},
			{-0.08601d, false}
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			assertEquals("At #" + i, (Boolean)fix[i][1],
				validator.shouldCounted((Double) fix[i][0]));
		}
	}

}
