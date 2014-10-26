package ru.prolib.aquila.core.data.finam.storage;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

import ru.prolib.aquila.core.data.SimpleIterator;

public class DirListScannerTest {
	private static final DateTimeFormatter df;
	 
	static {
		df = DateTimeFormat.forPattern("yyyy-MM-dd");
	}
	
	private final String basePath = "fixture/csv-storage/ticks/2014";
	private final String pfx = "GAZP-EQBR-RUR-STK-";
	
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
	public void testScanner() throws Exception {
		List<FileEntry> dirList = new Vector<FileEntry>(),
				expected = new Vector<FileEntry>(),
				actual = new Vector<FileEntry>();
		dirList.add(fileEntry(basePath, "/02", "2014-02-01"));
		dirList.add(fileEntry(basePath, "/10", "2014-10-14")); // start from day 14
		dirList.add(fileEntry(basePath, "/11", "2014-11-01")); // empty dir
		dirList.add(fileEntry(basePath, "/12", "2014-01-01"));
		DirListScanner iterator =
				new DirListScanner(new SimpleIterator<FileEntry>(dirList),
						new DirectoryScannerD(pfx));
		
		expected.add(fileEntry(basePath + "/02/" + pfx, "20140201.csv", "2014-02-01"));
		expected.add(fileEntry(basePath + "/02/" + pfx, "20140218.csv", "2014-02-18"));
		expected.add(fileEntry(basePath + "/10/" + pfx, "20141014.csv", "2014-10-14"));
		expected.add(fileEntry(basePath + "/10/" + pfx, "20141015.csv.gz", "2014-10-15"));
		expected.add(fileEntry(basePath + "/10/" + pfx, "20141019.csv", "2014-10-19"));
		expected.add(fileEntry(basePath + "/12/" + pfx, "20141204.csv", "2014-12-04"));
		
		while ( iterator.next() ) {
			actual.add(iterator.item());
		}
		
		assertEquals(expected, actual);
	}
}
