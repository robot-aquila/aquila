package ru.prolib.aquila.ta;

import static org.junit.Assert.*;
import org.junit.*;

public class SignalTest {
	private Signal s;

	@Before
	public void setUp() throws Exception {
		s = new Signal(12345, Signal.BUY, 123.456, "zulu");
	}
	
	@Test
	public void testAccessors() {
		assertEquals(12345, s.getSourceId());
		assertEquals(Signal.BUY, s.getType());
		assertEquals(123.456, s.getPrice(), 0.001d);
		assertEquals("zulu", s.getComment());
	}

}
