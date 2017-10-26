package ru.prolib.aquila.core.data.timeframe;

import static org.junit.Assert.*;

import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

public class TFHoursTest {
	private ZoneId UTC, MSK;
	private TFHours H1, H4, H12;

	@Before
	public void setUp() throws Exception {
		UTC = ZoneId.of("UTC");
		MSK = ZoneId.of("Europe/Moscow");
		H1 = new TFHours(1);
		H4 = new TFHours(4);
		H12 = new TFHours(12);
	}
	
	@Test
	public void testGetLength() {
		assertEquals(1, H1.getLength());
		assertEquals(4, H4.getLength());
		assertEquals(12, H12.getLength());
	}
	
	@Test
	public void testGetUnit() {
		assertEquals(ChronoUnit.HOURS, H1.getUnit());
		assertEquals(ChronoUnit.HOURS, H4.getUnit());
		assertEquals(ChronoUnit.HOURS, H12.getUnit());
	}
	
	@Test
	public void testIsIntraday() {
		assertTrue(H1.isIntraday());
		assertTrue(H4.isIntraday());
		assertTrue(H12.isIntraday());
	}
	
	@Test
	public void testToTZFrame() {
		assertEquals(new ZTFHours( 1, UTC), H1.toZTFrame(UTC));
		assertEquals(new ZTFHours( 4, MSK), H4.toZTFrame(MSK));
		assertEquals(new ZTFHours(12, MSK), H12.toZTFrame(MSK));
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(H12.equals(H12));
		assertFalse(H12.equals(null));
		assertFalse(H12.equals(this));
	}
	
	@Test
	public void testEquals() {
		assertTrue(H4.equals(new TFHours(4)));
		assertFalse(H4.equals(H1));
	}
	
	@Test
	public void testToString() {
		assertEquals("H1", H1.toString());
		assertEquals("H4", H4.toString());
		assertEquals("H12", H12.toString());
	}

	@Test
	public void testHashCode() {
		assertEquals(new HashCodeBuilder(571921, 5527).append(1).toHashCode(), H1.hashCode());
		assertEquals(new HashCodeBuilder(571921, 5527).append(4).toHashCode(), H4.hashCode());
		assertEquals(new HashCodeBuilder(571921, 5527).append(12).toHashCode(), H12.hashCode());
	}

}
