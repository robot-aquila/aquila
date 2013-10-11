package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;

import org.junit.*;
import ru.prolib.aquila.core.data.timeframe.*;

public class TimeframeTest {

	@Test
	public void testConstants() throws Exception {
		assertEquals(new TFMinutes(1), Timeframe.M1);
		assertEquals(new TFMinutes(2), Timeframe.M2);
		assertEquals(new TFMinutes(3), Timeframe.M3);
		assertEquals(new TFMinutes(5), Timeframe.M5);
		assertEquals(new TFMinutes(10), Timeframe.M10);
		assertEquals(new TFMinutes(15), Timeframe.M15);
		assertEquals(new TFMinutes(30), Timeframe.M30);
		assertEquals(new TFMinutes(60), Timeframe.M60);
	}
	
}
