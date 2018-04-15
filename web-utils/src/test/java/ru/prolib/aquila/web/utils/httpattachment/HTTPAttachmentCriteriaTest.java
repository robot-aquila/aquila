package ru.prolib.aquila.web.utils.httpattachment;

import static org.junit.Assert.*;

import java.time.Instant;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class HTTPAttachmentCriteriaTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private HTTPAttachmentCriteria service;

	@Before
	public void setUp() throws Exception {
		service = new HTTPAttachmentCriteria(T("2018-04-14T00:00:00Z"),
				"http://export.finam.ru/SPFB.RTS_180413_180413.txt?market...",
				"SPFB.RTS_180413_180413.txt",
				"finam/expotfile",
				"attachment; filename=\"SPFB.RTS_180413_180413.txt\"");
	}
	
	@Test
	public void testCtor() {
		assertEquals(T("2018-04-14T00:00:00Z"), service.getTimeOfStartDownload());
		assertEquals("http://export.finam.ru/SPFB.RTS_180413_180413.txt?market...", service.getURL());
		assertEquals("SPFB.RTS_180413_180413.txt", service.getFileName());
		assertEquals("finam/expotfile", service.getContentType());
		assertEquals("attachment; filename=\"SPFB.RTS_180413_180413.txt\"", service.getContentDisposition());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}

	@Test
	public void testEquals() {
		Variant<Instant> vTSD = new Variant<>(T("2018-04-14T00:00:00Z"), T("1998-07-08T15:40:35Z"));
		Variant<String> vURL = new Variant<>(vTSD);
		vURL.add("http://export.finam.ru/SPFB.RTS_180413_180413.txt?market...")
			.add("http://foobar.buz/zulu24");
		Variant<String> vFN = new Variant<>(vURL, "SPFB.RTS_180413_180413.txt", "foo.bar");
		Variant<String> vCT = new Variant<>(vFN, "finam/expotfile", "text/html");
		Variant<String> vCD = new Variant<>(vCT);
		vCD.add("attachment; filename=\"SPFB.RTS_180413_180413.txt\"")
			.add("attachment; filename=\"foo.bar\"");
		Variant<?> iterator = vCD;
		int foundCnt = 0;
		HTTPAttachmentCriteria x, found = null;
		do {
			x = new HTTPAttachmentCriteria(vTSD.get(), vURL.get(), vFN.get(), vCT.get(), vCD.get());
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(T("2018-04-14T00:00:00Z"), found.getTimeOfStartDownload());
		assertEquals("http://export.finam.ru/SPFB.RTS_180413_180413.txt?market...", found.getURL());
		assertEquals("SPFB.RTS_180413_180413.txt", found.getFileName());
		assertEquals("finam/expotfile", found.getContentType());
		assertEquals("attachment; filename=\"SPFB.RTS_180413_180413.txt\"", found.getContentDisposition());
	}

	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(9282611, 87361)
			.append(T("2018-04-14T00:00:00Z"))
			.append("http://export.finam.ru/SPFB.RTS_180413_180413.txt?market...")
			.append("SPFB.RTS_180413_180413.txt")
			.append("finam/expotfile")
			.append("attachment; filename=\"SPFB.RTS_180413_180413.txt\"")
			.toHashCode();
		assertEquals(expected, service.hashCode());
	}
	
	@Test
	public void testToString() {
		String expected = "HTTPAttachmentCriteria["
				+ "startDownload=2018-04-14T00:00:00Z,"
				+ "url=http://export.finam.ru/SPFB.RTS_180413_180413.txt?market...,"
				+ "fileName=SPFB.RTS_180413_180413.txt,"
				+ "contentType=finam/expotfile,"
				+ "contentDisposition=attachment; filename=\"SPFB.RTS_180413_180413.txt\""
				+ "]";
		assertEquals(expected, service.toString());
	}

}
