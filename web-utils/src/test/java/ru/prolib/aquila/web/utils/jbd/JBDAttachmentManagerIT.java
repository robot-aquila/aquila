package ru.prolib.aquila.web.utils.jbd;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.io.File;
import java.nio.charset.Charset;
import java.time.Instant;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.web.utils.httpattachment.HTTPAttachment;
import ru.prolib.aquila.web.utils.httpattachment.HTTPAttachmentCriteria;
import ru.prolib.aquila.web.utils.httpattachment.HTTPAttachmentCriteriaBuilder;
import ru.prolib.aquila.web.utils.httpattachment.di.Stub;

import com.machinepublishers.jbrowserdriver.JBrowserDriver;

public class JBDAttachmentManagerIT {
	private static Charset UTF8 = Charset.forName("UTF-8");
	private static File tempDir;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		tempDir = new File(FileUtils.getTempDirectory(), "jbd-attachment-manager-test");
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		FileUtils.forceDelete(tempDir);
	}
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private IMocksControl control;
	private JBrowserDriver driverMock;
	private JBDAttachmentManager service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		driverMock = control.createMock(JBrowserDriver.class);
		service = new JBDAttachmentManager(driverMock);
	}

	@Test
	public void testGetLast() throws Exception {
		expect(driverMock.attachmentsDir()).andReturn(tempDir);
		control.replay();
		
		File mf1 = new File(tempDir, "xxx.metadata");
		File cf1 = new File(tempDir, "xxx.content");
		FileUtils.writeStringToFile(mf1, "http://foo.bar/myfile.txt\ntext/html\nattachment; filename=\"myfile.txt\"", UTF8);
		FileUtils.writeStringToFile(cf1, "hello, world!");
		mf1.setLastModified(T("2018-04-14T21:25:15Z").toEpochMilli());
		
		File mf2 = new File(tempDir, "aaa.metadata");
		File cf2 = new File(tempDir, "aaa.content");
		FileUtils.writeStringToFile(mf2, "http://foo.bar/myfile.txt\ntext/html\nattachment; filename=\"myfile.txt\"", UTF8);
		FileUtils.writeStringToFile(cf2, "buzzy boo");
		mf2.setLastModified(T("2018-04-14T21:25:10Z").toEpochMilli());
		
		File mf3 = new File(tempDir, "zzz.metadata");
		File cf3 = new File(tempDir, "zzz.content");
		FileUtils.writeStringToFile(mf3, "http://foo.bar/foo.txt\nfoo/bar\nattachment; filename=\"foo.txt\"", UTF8);
		FileUtils.writeStringToFile(cf3, "");
		mf3.setLastModified(T("2018-04-14T21:25:20Z").toEpochMilli());
		
		HTTPAttachmentCriteria criteria = new HTTPAttachmentCriteriaBuilder()
			.withTimeOfStartDownload(T("2018-01-01T00:00:00Z"))
			.withURL("http://foo.bar/myfile.txt")
			.withFileName("myfile.txt")
			.withContentType("text/html")
			.withContentDisposition("attachment; filename=\"myfile.txt\"")
			.build();
		
		HTTPAttachment actual = service.getLast(criteria, new Stub());
		
		HTTPAttachment expected = new JBDAttachment(mf1, cf1);
		assertEquals(expected, actual);
	}

}
