package ru.prolib.aquila.core;

import static org.junit.Assert.*;

import org.junit.*;

import ru.prolib.aquila.core.StarterStub;

/**
 * 2012-08-18<br>
 * $Id: StarterStubTest.java 322 2012-11-24 12:59:46Z whirlwind $
 */
public class StarterStubTest {
	private StarterStub starter;

	@Before
	public void setUp() throws Exception {
		starter = new StarterStub();
	}
	
	@Test
	public void testStarter() throws Exception {
		starter.start();
		starter.stop();
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(starter.equals(starter));
		assertTrue(starter.equals(new StarterStub()));
		assertFalse(starter.equals(null));
		assertFalse(starter.equals(this));
	}

}
