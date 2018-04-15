package ru.prolib.aquila.web.utils.jbd;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class JBDAttachmentTest {
	private File tempDir, metadataFile, contentFile;
	private JBDAttachment service;

	@Before
	public void setUp() throws Exception {
		tempDir = new File(FileUtils.getTempDirectory(), "jbd-attachment-test");
		FileUtils.forceMkdir(tempDir);
		metadataFile = new File(tempDir, "12345.metadata");
		contentFile = new File(tempDir, "12345.content");
		FileUtils.writeStringToFile(metadataFile, "http://some.url/test.txt\ntext/html\nattachment; filename=\"test.txt\"\n");
		FileUtils.writeStringToFile(contentFile, "hello, world!");
		service = new JBDAttachment(metadataFile, contentFile);
	}
	
	@After
	public void tearDown() throws Exception {
		FileUtils.deleteQuietly(tempDir);
	}

	@Test
	public void testGetFile() {
		assertEquals(contentFile, service.getFile());
	}
	
	@Test
	public void testRemove() {
		service.remove();
		
		assertFalse(contentFile.exists());
		assertFalse(metadataFile.exists());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testEquals() {
		Variant<File> vMF = new Variant<>(metadataFile, new File("zulu/charlie"));
		Variant<File> vCF = new Variant<>(vMF, contentFile, new File("foo/bar"));
		Variant<?> iterator = vCF;
		int foundCnt = 0;
		JBDAttachment x, found = null;
		do {
			x = new JBDAttachment(vMF.get(), vCF.get());
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(metadataFile, found.getMetaDataFile());
		assertEquals(contentFile, found.getFile());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(265491261, 555123)
			.append(metadataFile)
			.append(contentFile)
			.toHashCode();
		
		assertEquals(expected, service.hashCode());
	}
	
	@Test
	public void testToString() {
		String expected = "JBDAttachment[metadata=" + metadataFile + ",content=" + contentFile + "]";
		
		assertEquals(expected, service.toString());
	}

}
