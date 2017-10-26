package ru.prolib.aquila.core.data.timeframe;

import static org.junit.Assert.*;

import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

public class TFDaysTest {
	private ZoneId UTC, MSK;
	private TFDays D1, D5, D20;

	@Before
	public void setUp() throws Exception {
		UTC = ZoneId.of("UTC");
		MSK = ZoneId.of("Europe/Moscow");
		D1 = new TFDays(1);
		D5 = new TFDays(5);
		D20 = new TFDays(20);
	}
	
	@Test
	public void testGetLength() {
		assertEquals(1, D1.getLength());
		assertEquals(5, D5.getLength());
		assertEquals(20, D20.getLength());
	}
	
	@Test
	public void testGetUnit() {
		assertEquals(ChronoUnit.DAYS, D1.getUnit());
		assertEquals(ChronoUnit.DAYS, D5.getUnit());
		assertEquals(ChronoUnit.DAYS, D20.getUnit());
	}
	
	@Test
	public void testIsIntraday() {
		assertFalse(D1.isIntraday());
		assertFalse(D5.isIntraday());
		assertFalse(D20.isIntraday());
	}
	
	@Test
	public void testToZTFrame() {
		assertEquals(new ZTFDays(1, MSK), D1.toZTFrame(MSK));
		assertEquals(new ZTFDays(5, UTC), D5.toZTFrame(UTC));
		assertEquals(new ZTFDays(20, MSK), D20.toZTFrame(MSK));
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(D1.equals(D1));
		assertFalse(D1.equals(null));
		assertFalse(D1.equals(this));
	}
	
	@Test
	public void testEquals() {
		assertTrue(D1.equals(new TFDays(1)));
		assertFalse(D1.equals(new TFDays(5)));
	}

	@Test
	public void testToString() {
		assertEquals("D1", D1.toString());
		assertEquals("D5", D5.toString());
		assertEquals("D20", D20.toString());
	}
	
	@Test
	public void testHashCode() {
		assertEquals(new HashCodeBuilder(1839115, 19013).append( 1).toHashCode(), D1.hashCode());
		assertEquals(new HashCodeBuilder(1839115, 19013).append( 5).toHashCode(), D5.hashCode());
		assertEquals(new HashCodeBuilder(1839115, 19013).append(20).toHashCode(), D20.hashCode());
	}

}
