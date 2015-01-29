package ru.prolib.aquila.core.data.finam;

import static org.junit.Assert.*;

import java.io.File;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.data.finam.FileEntry;

public class FileEntryTest {
	private FileEntry e1, e2, e3, e4, e5;

	@Before
	public void setUp() throws Exception {
		e1 = new FileEntry(new File("/foo/bar"), new LocalDate(2014, 10, 21));
		e2 = new FileEntry(new File("/foo/buz"), new LocalDate(2014, 10, 20));
		e3 = new FileEntry(new File("/foo/bar_"), new LocalDate(2014, 10, 21));
		e4 = new FileEntry(new File("/foo/bar"), new LocalDate(2014, 10, 21));
		e5 = new FileEntry(new File("/foo/bar"), new LocalDate(2014, 10, 25));
	}

	@Test
	public void testConstruct2() {
		assertEquals(new File("/foo/bar"), e1.getFile());
		assertEquals(new LocalDate(2014, 10,21), e1.getDate());
	}
	
	@Test
	public void testCompareTo() {
		assertEquals( 1, e1.compareTo(null));
		assertEquals( 1, e1.compareTo(e2));
		assertEquals(-1, e2.compareTo(e1));
		assertEquals(-1, e1.compareTo(e3));
		assertEquals( 1, e3.compareTo(e1));
		assertEquals( 0, e1.compareTo(e4));
		assertEquals(-1, e1.compareTo(e5));
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(e1.equals(e1));
		assertFalse(e1.equals(null));
		assertFalse(e1.equals(this));
	}
	
	@Test
	public void testEquals() {
		assertFalse(e1.equals(e2));
		assertFalse(e1.equals(e3));
		assertTrue(e1.equals(e4));
	}
	
	@Test
	public void testToString() {
		assertEquals("FileEntry[date=2014-10-21 file="
				+ new File("/foo/bar") + "]", e1.toString());
	}

}
