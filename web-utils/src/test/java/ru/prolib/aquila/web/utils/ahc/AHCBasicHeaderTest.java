package ru.prolib.aquila.web.utils.ahc;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import com.google.common.net.HttpHeaders;

public class AHCBasicHeaderTest {
	private AHCBasicHeader service;

	@Before
	public void setUp() throws Exception {
		service = new AHCBasicHeader(HttpHeaders.ACCEPT, "*/*");
	}

	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testEquals() {
		assertTrue(service.equals(new AHCBasicHeader(HttpHeaders.ACCEPT, "*/*")));
		assertFalse(service.equals(new AHCBasicHeader(HttpHeaders.USER_AGENT, "*/*")));
		assertFalse(service.equals(new AHCBasicHeader(HttpHeaders.ACCEPT, "text/html")));
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(99173, 53)
			.append(HttpHeaders.ACCEPT)
			.append("*/*")
			.toHashCode();
		
		assertEquals(expected, service.hashCode());
	}
	
	@Test
	public void testToString() {
		String expected = "Accept: */*";
		
		assertEquals(expected, service.toString());
	}

}
