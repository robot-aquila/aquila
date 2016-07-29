package ru.prolib.aquila.data.storage.file;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Test;

public class FilesetInfoTest {

	@After
	public void tearDown() throws Exception {
		FilesetInfo.clearSuffixesCache();
	}
	
	@Test
	public void testCreateInstance2() throws Exception  {
		FilesetInfo info = FilesetInfo.createInstance("foo", "bar");
		
		assertEquals("foo", info.getRegularSuffix());
		assertEquals("bar", info.getTemporarySuffix());
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testCreateInstance2_ThrowsIfRegularExists() throws Exception {
		FilesetInfo.createInstance("foo", "bar");
		FilesetInfo.createInstance("foo", "zulu");
	}

	@Test (expected=IllegalArgumentException.class)
	public void testCreateInstance2_ThrowsIfTemporaryExists() throws Exception {
		FilesetInfo.createInstance("foo", "bar");
		FilesetInfo.createInstance("zulu", "bar");
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testCreateInstance2_ThrowsIfSuffixesAreEquals() throws Exception {
		FilesetInfo.createInstance("foo", "foo");
	}

}
