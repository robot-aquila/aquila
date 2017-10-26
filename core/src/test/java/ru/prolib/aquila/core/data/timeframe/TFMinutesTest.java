package ru.prolib.aquila.core.data.timeframe;

import static org.junit.Assert.*;

import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

public class TFMinutesTest {
	private ZoneId UTC, MSK;
	private TFMinutes M1, M5, M7;

	@Before
	public void setUp() throws Exception {
		UTC = ZoneId.of("UTC");
		MSK = ZoneId.of("Europe/Moscow");
		M1 = new TFMinutes(1);
		M5 = new TFMinutes(5);
		M7 = new TFMinutes(7);
	}
	
	@Test
	public void testGetLength() {
		assertEquals(1, M1.getLength());
		assertEquals(5, M5.getLength());
		assertEquals(7, M7.getLength());
	}
	
	@Test
	public void testGetUnit() {
		assertEquals(ChronoUnit.MINUTES, M1.getUnit());
		assertEquals(ChronoUnit.MINUTES, M5.getUnit());
		assertEquals(ChronoUnit.MINUTES, M7.getUnit());
	}
	
	@Test
	public void testIsIntraday() {
		assertTrue(M1.isIntraday());
		assertTrue(M5.isIntraday());
		assertTrue(M7.isIntraday());
	}
	
	@Test
	public void testToTZFrame() {
		assertEquals(new ZTFMinutes(1, MSK), M1.toZTFrame(MSK));
		assertEquals(new ZTFMinutes(5, UTC), M5.toZTFrame(UTC));
		assertEquals(new ZTFMinutes(7, MSK), M7.toZTFrame(MSK));
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(M7.equals(M7));
		assertFalse(M7.equals(null));
		assertFalse(M7.equals(this));
	}
	
	@Test
	public void testEquals() {
		assertTrue(M7.equals(new TFMinutes(7)));
		assertFalse(M7.equals(M5));
	}
	
	@Test
	public void testToString() {
		assertEquals("M1", M1.toString());
		assertEquals("M5", M5.toString());
		assertEquals("M7", M7.toString());
	}

	@Test
	public void testHashCode() {
		assertEquals(new HashCodeBuilder(4856141, 13219).append(1).toHashCode(), M1.hashCode());
		assertEquals(new HashCodeBuilder(4856141, 13219).append(5).toHashCode(), M5.hashCode());
		assertEquals(new HashCodeBuilder(4856141, 13219).append(7).toHashCode(), M7.hashCode());
	}

}
