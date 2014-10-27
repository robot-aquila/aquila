package ru.prolib.aquila.core.data.finam.storage;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.easymock.IMocksControl;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

import ru.prolib.aquila.core.data.Aqiterator;
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
	
	private DirListScanner createTestObject() {
		return createTestObject(new Vector<FileEntry>());
	}
	
	private DirListScanner createTestObject(List<FileEntry> dirList) {
		return createTestObject(new SimpleIterator<FileEntry>(dirList),
				new DirectoryScannerD(pfx));
	}
	
	private DirListScanner createTestObject(Aqiterator<FileEntry> dirList,
			DirectoryScanner dirScanner)
	{
		return new DirListScanner(dirList, dirScanner);		
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
		DirListScanner iterator = createTestObject(dirList);
		
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
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		DirListScanner iterator = createTestObject();
		assertTrue(iterator.equals(iterator));
		assertFalse(iterator.equals(null));
		assertFalse(iterator.equals(this));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testEquals() throws Exception {
		IMocksControl control = createStrictControl();
		Aqiterator<FileEntry> it1, it2;
		it1 = control.createMock(Aqiterator.class);
		it2 = control.createMock(Aqiterator.class);
		DirectoryScanner sc1, sc2;
		sc1 = control.createMock(DirectoryScanner.class);
		sc2 = control.createMock(DirectoryScanner.class);
		
		DirListScanner obj = new DirListScanner(it1, sc1);
		assertTrue(obj.equals(new DirListScanner(it1, sc1)));
		assertFalse(obj.equals(new DirListScanner(it2, sc1)));
		assertFalse(obj.equals(new DirListScanner(it2, sc2)));
		assertFalse(obj.equals(new DirListScanner(it1, sc2)));
	}
	
}
