package ru.prolib.aquila.core.data;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.easymock.IMocksControl;
import org.joda.time.format.*;
import org.junit.*;

import ru.prolib.aquila.core.data.finam.*;

public class SubScanIteratorTest {
	private static final DateTimeFormatter df;
	 
	static {
		df = DateTimeFormat.forPattern("yyyy-MM-dd");
	}
	
	private final String basePath = "fixture/GAZP-EQBR-RUR-STK/2014";
	private final String pfx = "GAZP-EQBR-RUR-STK-";
	
	private IMocksControl control;
	private Aqiterator<FileEntry> it1, it2;
	private SubScanner<FileEntry> sc1, sc2;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		it1 = control.createMock(Aqiterator.class);
		it2 = control.createMock(Aqiterator.class);
		sc1 = control.createMock(SubScanner.class);
		sc2 = control.createMock(SubScanner.class);
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
	
	private Aqiterator<FileEntry> createTestObject() {
		return createTestObject(new Vector<FileEntry>());
	}
	
	private Aqiterator<FileEntry> createTestObject(List<FileEntry> dirList) {
		return createTestObject(new SimpleIterator<FileEntry>(dirList),
				new DirectoryScannerD(pfx));
	}
	
	private Aqiterator<FileEntry> createTestObject(Aqiterator<FileEntry> dirList,
			SubScanner<FileEntry> dirScanner)
	{
		return new SubScanIterator<FileEntry>(dirList, dirScanner);		
	}

	@Test
	public void testIterator() throws Exception {
		List<FileEntry> dirList = new Vector<FileEntry>(),
				expected = new Vector<FileEntry>(),
				actual = new Vector<FileEntry>();
		dirList.add(fileEntry(basePath, "/02", "2014-02-01"));
		dirList.add(fileEntry(basePath, "/10", "2014-10-14")); // start from day 14
		dirList.add(fileEntry(basePath, "/11", "2014-11-01")); // empty dir
		dirList.add(fileEntry(basePath, "/12", "2014-01-01"));
		Aqiterator<FileEntry> iterator = createTestObject(dirList);
		
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
		Aqiterator<FileEntry> iterator = createTestObject();
		assertTrue(iterator.equals(iterator));
		assertFalse(iterator.equals(null));
		assertFalse(iterator.equals(this));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testEquals() throws Exception {
		SubScanIterator obj = new SubScanIterator(it1, sc1);
		assertTrue(obj.equals(new SubScanIterator(it1, sc1)));
		assertFalse(obj.equals(new SubScanIterator(it2, sc1)));
		assertFalse(obj.equals(new SubScanIterator(it2, sc2)));
		assertFalse(obj.equals(new SubScanIterator(it1, sc2)));
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testClose() throws Exception {
		SubScanIterator obj = new SubScanIterator(it1, sc1);
		it1.close();
		control.replay();
		
		obj.close();
		obj.close();
		
		control.verify();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testClose_ClosesSubList() throws Exception {
		List<FileEntry> list = new Vector<FileEntry>();
		list.add(fileEntry("foo/", "bar", "2014-02-01"));
		expect(sc1.makeScan(eq(fileEntry("foo/", "bar", "2014-02-01"))))
			.andReturn(it2);
		expect(it2.next()).andReturn(true);
		it2.close();
		control.replay();
		
		SubScanIterator obj = new SubScanIterator(new SimpleIterator(list), sc1);
		obj.next();
		obj.close();
		obj.close();
		
		control.verify();
	}
	
}
