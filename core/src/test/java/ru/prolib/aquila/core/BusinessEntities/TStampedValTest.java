package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;

import java.time.Instant;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class TStampedValTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private TStampedVal<String> service;

	@Before
	public void setUp() throws Exception {
		service = new TStampedVal<>(T("2019-01-07T06:52:42Z"), "foo");
	}
	
	@Test
	public void testGetters() {
		assertEquals(T("2019-01-07T06:52:42Z"), service.getTime());
		assertEquals("foo", service.getValue());
	}
	
	@Test
	public void testToString() {
		String expected = new StringBuilder()
				.append("TStampedVal[")
				.append("time=2019-01-07T06:52:42Z,")
				.append("value=foo")
				.append("]")
				.toString();
		
		assertEquals(expected, service.toString());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}

	@Test
	public void testEquals() {
		Variant<Instant> vTM = new Variant<>(T("2019-01-07T06:52:42Z"), T("2005-12-31T23:59:59Z"));
		Variant<String> vVAL = new Variant<>(vTM, "foo", "bar");
		Variant<?> iterator = vVAL;
		int foundCnt = 0;
		TStampedVal<String> x, found = null;
		do {
			x = new TStampedVal<>(vTM.get(), vVAL.get());
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(T("2019-01-07T06:52:42Z"), found.getTime());
		assertEquals("foo", found.getValue());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(100976245, 905)
				.append(T("2019-01-07T06:52:42Z"))
				.append("foo")
				.build();
		
		assertEquals(expected, service.hashCode());
	}

}
