package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;
import java.util.Date;
import org.junit.*;

/**
 * 2012-04-25<br>
 * $Id: GCandleTimeTest.java 556 2013-03-04 17:18:03Z whirlwind $
 */
public class GCandleTimeTest {
	private GCandleTime getter;
	
	@Before
	public void setUp() throws Exception {
		getter = new GCandleTime();
	}
	
	@Test
	public void testGet() throws Exception {
		Date time = new Date();
		Candle candle = new Candle(time, 0, 0, 0, 0, 0);
		assertSame(time, getter.get(candle));
		assertNull(getter.get(null));
		assertNull(getter.get(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(getter.equals(getter));
		assertTrue(getter.equals(new GCandleTime()));
		assertFalse(getter.equals(null));
		assertFalse(getter.equals(this));
	}

}
