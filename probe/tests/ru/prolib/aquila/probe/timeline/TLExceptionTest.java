package ru.prolib.aquila.probe.timeline;

import static org.junit.Assert.*;
import org.junit.*;

public class TLExceptionTest {
	
	@Test
	public void testConstruct0() throws Exception {
		TLException e = new TLException();
		assertNull(e.getMessage());
		assertNull(e.getCause());
	}
	
	@Test
	public void testConstruct1S() throws Exception {
		TLException e = new TLException("foo");
		assertEquals("foo", e.getMessage());
	}
	
	@Test
	public void testConstruct1T() throws Exception {
		Exception cause = new Exception();
		TLException e = new TLException(cause);
		assertSame(cause, e.getCause());
	}
	
	@Test
	public void testConstruct2() throws Exception {
		Exception cause = new Exception();
		TLException e = new TLException("bar", cause);
		assertEquals("bar", e.getMessage());
		assertSame(cause, e.getCause());
	}

}
