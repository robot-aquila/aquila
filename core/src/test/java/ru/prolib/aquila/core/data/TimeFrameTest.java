package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;

import org.junit.*;
import ru.prolib.aquila.core.data.timeframe.*;

public class TimeFrameTest {

	@Test
	public void testConstants() throws Exception {
		assertEquals(new TFMinutes(1), TimeFrame.M1);
		assertEquals(new TFMinutes(2), TimeFrame.M2);
		assertEquals(new TFMinutes(3), TimeFrame.M3);
		assertEquals(new TFMinutes(5), TimeFrame.M5);
		assertEquals(new TFMinutes(10), TimeFrame.M10);
		assertEquals(new TFMinutes(15), TimeFrame.M15);
		assertEquals(new TFMinutes(30), TimeFrame.M30);
		assertEquals(new TFMinutes(60), TimeFrame.M60);
		assertEquals(new TFDays(1), TimeFrame.D1);
	}
	
}
