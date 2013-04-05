package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;
import java.util.Date;
import org.junit.*;

/**
 * 2012-04-25<br>
 * $Id: GCandleCloseTest.java 556 2013-03-04 17:18:03Z whirlwind $
 */
public class GCandleCloseTest {
	private GCandleClose getter;
	
	@Before
	public void setUp() throws Exception {
		getter = new GCandleClose();
	}

	@Test
	public void testGet() throws Exception {
		Candle candle = new Candle(new Date(), 0, 0, 0, 80, 0);
		assertEquals(80d, getter.get(candle), 0.1d);
		assertNull(getter.get(null));
		assertNull(getter.get(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(getter.equals(getter));
		assertTrue(getter.equals(new GCandleClose()));
		assertFalse(getter.equals(null));
		assertFalse(getter.equals(this));
	}

}
