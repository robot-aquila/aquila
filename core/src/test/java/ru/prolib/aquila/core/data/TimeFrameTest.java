package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;

import java.time.ZoneId;

import org.junit.*;
import ru.prolib.aquila.core.data.timeframe.*;

public class TimeFrameTest {

	@Test
	public void testConstants() throws Exception {
		ZoneId MSK = ZoneId.of("Europe/Moscow");
		assertEquals(new ZTFMinutes(1), ZTFrame.M1);
		assertEquals(new ZTFMinutes(1), ZTFrame.M1UTC);
		assertEquals(new ZTFMinutes(1, MSK), ZTFrame.M1MSK);
		assertEquals(new ZTFMinutes(2), ZTFrame.M2);
		assertEquals(new ZTFMinutes(2), ZTFrame.M2UTC);
		assertEquals(new ZTFMinutes(2, MSK), ZTFrame.M2MSK);
		assertEquals(new ZTFMinutes(3), ZTFrame.M3);
		assertEquals(new ZTFMinutes(3), ZTFrame.M3UTC);
		assertEquals(new ZTFMinutes(3, MSK), ZTFrame.M3MSK);
		assertEquals(new ZTFMinutes(5), ZTFrame.M5);
		assertEquals(new ZTFMinutes(5), ZTFrame.M5UTC);
		assertEquals(new ZTFMinutes(5, MSK), ZTFrame.M5MSK);
		assertEquals(new ZTFMinutes(10), ZTFrame.M10);
		assertEquals(new ZTFMinutes(10), ZTFrame.M10UTC);
		assertEquals(new ZTFMinutes(10, MSK), ZTFrame.M10MSK);
		assertEquals(new ZTFMinutes(15), ZTFrame.M15);
		assertEquals(new ZTFMinutes(15), ZTFrame.M15UTC);
		assertEquals(new ZTFMinutes(15, MSK), ZTFrame.M15MSK);
		assertEquals(new ZTFMinutes(30), ZTFrame.M30);
		assertEquals(new ZTFMinutes(30), ZTFrame.M30UTC);
		assertEquals(new ZTFMinutes(30, MSK), ZTFrame.M30MSK);
		assertEquals(new ZTFHours(1), ZTFrame.H1);
		assertEquals(new ZTFHours(1), ZTFrame.H1UTC);
		assertEquals(new ZTFHours(1, MSK), ZTFrame.H1MSK);
		assertEquals(new ZTFDays(1), ZTFrame.D1);
		assertEquals(new ZTFDays(1), ZTFrame.D1UTC);
		assertEquals(new ZTFDays(1, MSK), ZTFrame.D1MSK);
	}
	
}
