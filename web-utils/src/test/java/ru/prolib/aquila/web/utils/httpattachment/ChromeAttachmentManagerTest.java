package ru.prolib.aquila.web.utils.httpattachment;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ChromeAttachmentManagerTest {
	private static File tempDir;

	static void newFile(String filename) {
		try {
			new File(tempDir, filename).createNewFile();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	ChromeAttachmentManager service;
	HTTPAttachmentCriteria criteria;
	
	@Before
	public void setUp() throws Exception {
		tempDir = new File("fixture/temp");
		FileUtils.forceMkdir(tempDir);
		service = new ChromeAttachmentManager(tempDir, 500L);
		criteria = new HTTPAttachmentCriteriaBuilder().withFileName("data.csv").build();
	}
	
	@After
	public void tearDown() throws Exception {
		FileUtils.forceDelete(tempDir);
	}
	
	@Test
	public void testGetLast_TimeoutException() throws Exception {
		try {
			service.getLast(criteria, () -> {
				try {
					Thread.sleep(600L);
				} catch ( Exception e ) { } 
			});
			fail("Expected: " + HTTPAttachmentException.class.getSimpleName());
		} catch ( HTTPAttachmentException e ) {
			assertEquals("Timeout", e.getMessage());
			assertEquals(TimeoutException.class, e.getCause().getClass());
		}
	}
	
	@Test
	public void testGetLast_ExecutionException() throws Exception {
		try {
			service.getLast(criteria,  () -> {
				throw new IOException("Test error");
			});
			fail("Expected: " + HTTPAttachmentException.class.getSimpleName());
		} catch ( HTTPAttachmentException e ) {
			assertEquals("Unexpected exception: ", e.getMessage());
			assertEquals(ExecutionException.class, e.getCause().getClass());
			assertEquals(IllegalStateException.class, e.getCause().getCause().getClass());
			assertEquals(IOException.class, e.getCause().getCause().getCause().getClass());
			assertEquals("Test error", e.getCause().getCause().getCause().getMessage());
		}
	}
	
	@Test
	public void testGetLast_AttachmentNotFoundAfterAll() throws Exception {
		newFile("cumbry.csv");
		newFile("cumbry (1).csv.crdownload");
		newFile("cumbry (2).csv");
		try {
			service.getLast(criteria, () -> {
				newFile("any-file.csv");
				newFile("any-file (1).csv");
			});
			fail("Expected: " + HTTPAttachmentNotFoundException.class.getSimpleName());
		} catch ( HTTPAttachmentNotFoundException e ) {
			assertEquals(criteria, e.getCriteria());
			assertEquals("Procedure finished but expected file not found: data.csv", e.getMessage());
		}
	}
	
	@Test
	public void testGetLast_FileWithHighestNumberIsMostPriority() throws Exception {
		newFile("data (1).csv");
		newFile("data (456).csv"); // must be ignored because exists before start
		
		HTTPAttachment actual = service.getLast(criteria, () -> {
			newFile("data (58).csv");
			newFile("data (3).csv");
			newFile("data.csv"); // must be ignored because of low priority
		});
		
		HTTPAttachment expected = new HTTPAttachmentImpl(new File(tempDir, "data (58).csv"));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetLast_FileAsExpectedIfNoOtherOptions() throws Exception {
		newFile("data (1).csv");
		newFile("data (126).csv"); // must be ignored
		
		HTTPAttachment actual = service.getLast(criteria, () -> {
			newFile("boomba.csv");
			newFile("kabucha.tts");
			newFile("data.csv");
		});
		
		HTTPAttachment expected = new HTTPAttachmentImpl(new File(tempDir, "data.csv"));
		assertEquals(expected, actual);
	}

}
