package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;
import java.util.Date;
import org.junit.*;

/**
 * 2012-04-25<br>
 * $Id: GCandleVolumeTest.java 566 2013-03-11 01:52:40Z whirlwind $
 */
public class GCandleVolumeTest {
	private GCandleVolume getter;
	
	@Before
	public void setUp() throws Exception {
		getter = new GCandleVolume();
	}
	
	@Test
	public void testGet() throws Exception {
		Candle candle = new Candle(new Date(), 0, 0, 0, 0, 1000L);
		assertEquals(new Double(1000.0), getter.get(candle));
		assertNull(getter.get(null));
		assertNull(getter.get(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(getter.equals(getter));
		assertTrue(getter.equals(new GCandleVolume()));
		assertFalse(getter.equals(null));
		assertFalse(getter.equals(this));
	}

}
