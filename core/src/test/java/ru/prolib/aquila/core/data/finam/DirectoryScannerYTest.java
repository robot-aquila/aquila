package ru.prolib.aquila.core.data.finam;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.data.Aqiterator;
import ru.prolib.aquila.core.data.DataException;
import ru.prolib.aquila.core.data.finam.DirectoryScannerY;
import ru.prolib.aquila.core.data.finam.FileEntry;

public class DirectoryScannerYTest {
	private static final DateTimeFormatter df;
	
	static {
		df = DateTimeFormat.forPattern("yyyy-MM-dd");
	}
	
	private DirectoryScannerY scanner;
	private String basePath = "fixture/GAZP-EQBR-RUR-STK";

	@Before
	public void setUp() throws Exception {
		scanner = new DirectoryScannerY();
	}
	
	/**
	 * Создать дескриптор файла.
	 * <p>
	 * @param pfx префикс
	 * @param name имя файла
	 * @param date дата в формате yyyy-MM-dd
	 * @return дескриптор файла
	 */
	private FileEntry fileEntry(String pfx, String name, String date) {
		return new FileEntry(new File(pfx + name), df.parseLocalDate(date));
	}
	
	@Test
	public void testMakeScan() throws Exception {
		final String fpfx = basePath + "/";
		List<FileEntry> actual = new Vector<FileEntry>(),
				expected = new Vector<FileEntry>();
		expected.add(fileEntry(fpfx, "2011", "2011-01-01"));
		expected.add(fileEntry(fpfx, "2014", "2014-01-01"));
		expected.add(fileEntry(fpfx, "2029", "2029-01-01"));
		
		Aqiterator<FileEntry> it =
				scanner.makeScan(new FileEntry(new File(basePath),
						new LocalDate(2011, 12, 15)));
		while ( it.next() ) {
			actual.add(it.item());
		}
		
		assertEquals(expected, actual);
	}
	
	@Test (expected=DataException.class)
	public void testMakeScan_ThrowsIfDirectoryNotExists() throws Exception {
		scanner.makeScan(new FileEntry(new File("lakumba/barumba"),
				new LocalDate(2014, 10, 15)));
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(scanner.equals(scanner));
		assertTrue(scanner.equals(new DirectoryScannerY()));
		assertFalse(scanner.equals(null));
		assertFalse(scanner.equals(this));
	}

}