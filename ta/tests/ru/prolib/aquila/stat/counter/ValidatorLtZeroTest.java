package ru.prolib.aquila.stat.counter;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;


public class ValidatorLtZeroTest {
	ValidatorLtZero validator;

	@Before
	public void setUp() throws Exception {
		validator = new ValidatorLtZero();
	}
	
	@Test
	public void testShouldCounted() throws Exception {
		Object fix[][] = {
			// value, expected result
			{ 100d, false  },
			{   0d, false },
			{ -50d, true },
			{ 0.00001d, false},
			{-0.08601d, true}
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			assertEquals("At #" + i, (Boolean)fix[i][1],
				validator.shouldCounted((Double) fix[i][0]));
		}
	}

}
