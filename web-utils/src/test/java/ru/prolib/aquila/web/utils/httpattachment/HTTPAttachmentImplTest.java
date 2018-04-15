package ru.prolib.aquila.web.utils.httpattachment;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HTTPAttachmentImplTest {
	private File temp1, temp2;
	private HTTPAttachmentImpl service;

	@Before
	public void setUp() throws Exception {
		temp1 = File.createTempFile("http-attachment1-", ".test");
		temp2 = File.createTempFile("http-attachment2-", ".test");
		
		service = new HTTPAttachmentImpl(temp1);
	}
	
	@After
	public void tearDown() throws Exception {
		if ( temp1 != null ) {
			temp1.delete();
		}
		if ( temp2 != null ) {
			temp2.delete();
		}
	}

	@Test
	public void testCtor() {
		assertEquals(temp1, service.getFile());
	}
	
	@Test
	public void testRemove() {
		assertTrue(temp1.exists());
		
		service.remove();
		
		assertFalse(temp1.exists());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testEquals() {
		assertTrue(service.equals(new HTTPAttachmentImpl(temp1)));
		assertFalse(service.equals(new HTTPAttachmentImpl(temp2)));
	}
	
	@Test
	public void testToString() {
		String expected = "HTTPAttachmentImpl[file=" + temp1 + "]";
		
		assertEquals(expected, service.toString());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(554917, 331557)
			.append(temp1)
			.toHashCode();
		
		assertEquals(expected, service.hashCode());
	}

}
