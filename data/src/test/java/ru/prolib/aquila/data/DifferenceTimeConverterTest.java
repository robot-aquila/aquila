package ru.prolib.aquila.data;

import static org.junit.Assert.*;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

public class DifferenceTimeConverterTest {
	private DifferenceTimeConverter converter;

	@Before
	public void setUp() throws Exception {
		converter = new DifferenceTimeConverter();
	}
	
	private static Instant T(String timeString) {
		return Instant.parse(timeString);
	}

	@Test
	public void testConvert() {
		Instant currentTime = T("2016-08-02T00:00:00Z");
		assertEquals(T("2016-08-02T00:00:00Z"), converter.convert(currentTime, T("1970-01-01T15:30:00Z")));
		assertEquals(T("2016-08-02T00:00:10Z"), converter.convert(currentTime, T("1970-01-01T15:30:10Z")));
		assertEquals(T("2016-08-01T23:50:00Z"), converter.convert(currentTime, T("1970-01-01T15:20:00Z")));
		
		converter.reset();
		currentTime = T("1970-01-02T00:00:00Z");
		assertEquals(T("1970-01-02T00:00:00Z"), converter.convert(currentTime, T("2016-08-05T20:00:00Z")));
		assertEquals(T("1970-01-02T00:00:00Z"), converter.convert(currentTime, T("2016-08-05T20:00:00Z")));
		assertEquals(T("1970-01-02T00:10:00Z"), converter.convert(currentTime, T("2016-08-05T20:10:00Z")));
		assertEquals(T("1970-01-01T14:00:00Z"), converter.convert(currentTime, T("2016-08-05T10:00:00Z")));
	}

}
