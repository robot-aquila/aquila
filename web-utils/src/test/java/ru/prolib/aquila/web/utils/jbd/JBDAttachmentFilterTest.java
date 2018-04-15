package ru.prolib.aquila.web.utils.jbd;

import static org.junit.Assert.*;

import java.io.File;
import java.time.Instant;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.web.utils.httpattachment.HTTPAttachmentCriteria;
import ru.prolib.aquila.web.utils.httpattachment.HTTPAttachmentCriteriaBuilder;

public class JBDAttachmentFilterTest {
	private static final String TEST_PREFIX = "jbd-attachment";
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private File metadata, content;
	private HTTPAttachmentCriteria criteria1, criteria2;
	private JBDAttachmentFilter service;

	@Before
	public void setUp() throws Exception {
		criteria1 = new HTTPAttachmentCriteriaBuilder()
			.withTimeOfStartDownload(T("2018-04-14T13:55:00Z"))
			.withURL("http://foo.bar/myfile.txt")
			.withFileName("myfile.txt")
			.withContentType("text/html")
			.withContentDisposition("attachment; filename=\"myfile.txt\"")
			.build();
		criteria2 = new HTTPAttachmentCriteriaBuilder()
			.withTimeOfStartDownload(T("2015-01-01T00:00:00Z"))
			.withURL("http://zulu24.com/myfile.dat")
			.withFileName("myfile.dat")
			.withContentType("application/octet-stream")
			.withContentDisposition("attachment; filename=\"myfile.dat\"")
			.build();
		service = new JBDAttachmentFilter(criteria1);
		metadata = new File(FileUtils.getTempDirectory(), TEST_PREFIX + ".metadata");
		content = new File(FileUtils.getTempDirectory(), TEST_PREFIX + ".content");
	}
	
	@After
	public void tearDown() throws Exception {
		if ( metadata != null ) {
			metadata.delete();
			metadata = null;
		}
		if ( content != null ) {
			content.delete();
			content = null;
		}
	}
	
	@Test
	public void testToContentFile() throws Exception {
		File actual = JBDAttachmentFilter.toContentFile(new File("foo/bar/zulu24.metadata"));
		
		File expected = new File("foo/bar/zulu24.content");
		assertEquals(expected, actual);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testToContentFile_ThrowsIfNotMetadataFile() throws Exception {
		JBDAttachmentFilter.toContentFile(new File("foo/bar/zulu24.buz"));
	}
	
	@Test
	public void testAccept_BasicTest_SkipIfItIsNotMetadataFile() throws Exception {
		metadata = File.createTempFile(TEST_PREFIX, ".foobar");
		
		assertFalse(service.accept(metadata.getParentFile(), metadata.getName()));
	}
	
	@Test
	public void testAccept_BasicTest_SkipIfMetadataIsNotExists() throws Exception {
		assertFalse(service.accept(metadata.getParentFile(), metadata.getName()));
	}
	
	@Test
	public void testAccept_BasicTest_SkipIfMetadataIsDirectory() throws Exception {
		FileUtils.forceMkdir(metadata);
		
		assertFalse(service.accept(metadata.getParentFile(), metadata.getName()));
	}
	
	@Test
	public void testAccept_BasicTest_SkipIfMetadataFileIsObsolete() throws Exception {
		FileUtils.writeStringToFile(metadata, "");
		metadata.setLastModified(T("2018-04-14T13:55:00Z").minusMillis(1).toEpochMilli());
		
		assertFalse(service.accept(metadata.getParentFile(), metadata.getName()));
	}
	
	@Test
	public void testAccept_BasicTest_SkipIfContentIsNotExists() throws Exception {
		FileUtils.writeStringToFile(metadata, "");
		metadata.setLastModified(T("2018-04-14T13:55:00Z").plusMillis(1).toEpochMilli());
		
		assertFalse(service.accept(metadata.getParentFile(), metadata.getName()));
	}
	
	@Test
	public void testAccept_BasicTest_SkipIfContentIsDirectory() throws Exception {
		FileUtils.writeStringToFile(metadata, "");
		metadata.setLastModified(T("2018-04-14T13:55:00Z").plusMillis(1).toEpochMilli());
		FileUtils.forceMkdir(content);

		assertFalse(service.accept(metadata.getParentFile(), metadata.getName()));
	}
	
	@Test
	public void testAccept_CriteriaTest_SkipIfMetadataContainsLessThan3Lines() throws Exception {
		FileUtils.writeStringToFile(content, "hello, world!");
		FileUtils.writeStringToFile(metadata, "http://foobar.local/myfile.txt\ntext/html");
		
		assertFalse(service.accept(metadata.getParentFile(), metadata.getName()));
	}

	@Test
	public void testAccept_CriteriaTest_SkipIfURLMismatch() throws Exception {
		criteria1 = new HTTPAttachmentCriteriaBuilder()
			.withURL("http://foo.local/myfile.txt")
			.build();
		service = new JBDAttachmentFilter(criteria1);
		FileUtils.writeStringToFile(content, "");
		FileUtils.writeStringToFile(metadata, "http://foo.bar/myfile.txt\ntext/html\nattachment; filename=\"myfile.txt\"\n");
		
		assertFalse(service.accept(metadata.getParentFile(), metadata.getName()));
	}

	@Test
	public void testAccept_CriteriaTest_SkipIfContentTypeMismatch() throws Exception {
		criteria1 = new HTTPAttachmentCriteriaBuilder()
			.withContentType("image/jpeg")
			.build();
		service = new JBDAttachmentFilter(criteria1);
		FileUtils.writeStringToFile(content, "");
		FileUtils.writeStringToFile(metadata, "http://foo.bar/myfile.txt\ntext/html\nattachment; filename=\"myfile.txt\"\n");
		
		assertFalse(service.accept(metadata.getParentFile(), metadata.getName()));
	}
	
	@Test
	public void testAccept_CriteriaTest_SkipIfContentDispositionMismatch() throws Exception {
		criteria1 = new HTTPAttachmentCriteriaBuilder()
			.withContentDisposition("attachment; filename=\"zoo.jpg\"")
			.build();
		service = new JBDAttachmentFilter(criteria1);
		FileUtils.writeStringToFile(content, "");
		FileUtils.writeStringToFile(metadata, "http://foo.bar/myfile.txt\ntext/html\nattachment; filename=\"myfile.txt\"\n");
		
		assertFalse(service.accept(metadata.getParentFile(), metadata.getName()));
	}

	@Test
	public void testAccept_CombinedCriteriaTest_SkipIfURLMismatch() throws Exception {
		FileUtils.writeStringToFile(content, "");
		FileUtils.writeStringToFile(metadata, "http://123.bar/myfile.txt\ntext/html\nattachment; filename=\"myfile.txt\"\n");
		
		assertFalse(service.accept(metadata.getParentFile(), metadata.getName()));
	}

	@Test
	public void testAccept_CombinedCriteriaTest_SkipIfContentTypeMismatch() throws Exception {
		FileUtils.writeStringToFile(content, "");
		FileUtils.writeStringToFile(metadata, "http://foo.bar/myfile.txt\nimage/jpeg\nattachment; filename=\"myfile.txt\"\n");
		
		assertFalse(service.accept(metadata.getParentFile(), metadata.getName()));
	}
	
	@Test
	public void testAccept_CombinedCriteriaTest_SkipIfContentDispositionMismatch() throws Exception {
		FileUtils.writeStringToFile(content, "");
		FileUtils.writeStringToFile(metadata, "http://foo.bar/myfile.txt\ntext/html\nattachment; filename=\"boo.txt\"\n");
		
		assertFalse(service.accept(metadata.getParentFile(), metadata.getName()));
	}
	
	@Test
	public void testAccept_OK() throws Exception {
		FileUtils.writeStringToFile(content, "");
		FileUtils.writeStringToFile(metadata, "http://foo.bar/myfile.txt\ntext/html\nattachment; filename=\"myfile.txt\"\n");
		
		assertTrue(service.accept(metadata.getParentFile(), metadata.getName()));
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testEquals() {
		assertTrue(service.equals(new JBDAttachmentFilter(criteria1)));
		assertFalse(service.equals(new JBDAttachmentFilter(criteria2)));
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(71237841, 983475)
			.append(criteria1)
			.toHashCode();
		
		assertEquals(expected, service.hashCode());
	}

	@Test
	public void testToString() {
		String expected = "JBDAttachmentFilter[criteria=" + criteria1.toString() + "]";

		assertEquals(expected, service.toString());
	}

}
