package ru.prolib.aquila.core.utils;

import static org.junit.Assert.*;

import org.junit.*;

public class StrCoderTest {
	private StrCoder encoder; 

	@Before
	public void setUp() throws Exception {
		encoder = new StrCoder();
	}

	@Test
	public void testEncode() throws Exception {
		assertEquals("%2E", encoder.encode("."));
		assertEquals("%2D", encoder.encode("-"));
		assertEquals("%2A", encoder.encode("*"));
		assertEquals("%5F", encoder.encode("_"));
		assertEquals("%20", encoder.encode(" "));
		assertEquals("%D1%8F", encoder.encode("я"));
		assertEquals("%26", encoder.encode("&"));
		// etc...
	}
	
	@Test
	public void testDecode() throws Exception {
		assertEquals(".", encoder.decode("%2E"));
		assertEquals("-", encoder.decode("%2D"));
		assertEquals("*", encoder.decode("%2A"));
		assertEquals("_", encoder.decode("%5F"));
		assertEquals(" ", encoder.decode("%20"));
		assertEquals("я", encoder.decode("%D1%8F"));
		assertEquals("&", encoder.decode("%26"));
		// etc...
	}

}
