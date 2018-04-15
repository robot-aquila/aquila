package ru.prolib.aquila.web.utils.httpattachment;

import static org.junit.Assert.*;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

public class HTTPAttachmentCriteriaBuilderTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private HTTPAttachmentCriteriaBuilder service;

	@Before
	public void setUp() throws Exception {
		service = new HTTPAttachmentCriteriaBuilder();
	}
	
	@Test
	public void testBuild() {
		assertSame(service, service.withTimeOfStartDownload(T("2015-01-02T03:04:05Z")));
		assertSame(service, service.withURL("http://zulu.com/foobar.txt"));
		assertSame(service, service.withFileName("foobar.txt"));
		assertSame(service, service.withContentType("text/html"));
		assertSame(service, service.withContentDisposition("attachment; filename=\"foobar.txt\""));
		
		HTTPAttachmentCriteria actual = service.build();
		
		HTTPAttachmentCriteria expected = new HTTPAttachmentCriteria(
				T("2015-01-02T03:04:05Z"),
				"http://zulu.com/foobar.txt",
				"foobar.txt",
				"text/html",
				"attachment; filename=\"foobar.txt\""
		);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testBuild_WithNoTime() {
		HTTPAttachmentCriteria actual = service.build();

		assertTrue(System.currentTimeMillis() - actual.getTimeOfStartDownload().toEpochMilli() < 5);
		assertNull(actual.getURL());
		assertNull(actual.getFileName());
		assertNull(actual.getContentType());
		assertNull(actual.getContentDisposition());
	}
	
	@Test
	public void testBuild_WithTimeOfStartDownloadCurrent() throws Exception {
		assertSame(service, service.withTimeOfStartDownloadCurrent());
		Thread.sleep(500L);
		
		HTTPAttachmentCriteria actual = service.build();
		
		assertTrue(System.currentTimeMillis() - actual.getTimeOfStartDownload().toEpochMilli() >= 500);
	}

}
