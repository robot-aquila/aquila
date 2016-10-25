package ru.prolib.aquila.data.storage.file;

import static org.junit.Assert.*;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.data.DatedSymbol;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.file.FileStorageImpl;

public class FileStorageImplTest {
	private static String FS = File.separator;
	private static Symbol symbol1 = new Symbol("MSFT");
	private static LocalDate date1 = LocalDate.of(2016, 7, 26),
			date2 = LocalDate.of(2005, 12, 1);
	private static DatedSymbol descr1 = new DatedSymbol(symbol1, date1),
			descr2 = new DatedSymbol(symbol1, date2);
	private static FileConfig fileConfig;
	private FileStorageImpl storage;
	private File root = new File("fixture/temp");
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		fileConfig = new FileConfig(".csv.gz", ".part.csv.gz");
	}

	@Before
	public void setUp() throws Exception {
		FileUtils.forceMkdir(root);
		storage = new FileStorageImpl(root, "test", fileConfig);
	}
	
	@After
	public void tearDown() throws Exception {
		FileUtils.deleteDirectory(root);
	}
	
	@Test
	public void testCtor3() throws Exception {
		assertEquals(root, storage.getRoot());
		assertEquals("test", storage.getStorageID());
		assertEquals(fileConfig, storage.getConfig());
	}
	
	@Test
	public void testGetSegmentFile() {
		File expected = new File("fixture" + FS + "temp" + FS + "B0" + FS + "MSFT"
				+ FS + "2016" + FS + "07" + FS + "MSFT-20160726.csv.gz");
		
		assertEquals(expected, storage.getSegmentFile(descr1));
	}
	
	@Test
	public void testGetTemporarySegmentFile() throws Exception {
		File expected = new File("fixture" + FS + "temp" + FS + "B0" + FS + "MSFT"
				+ FS + "2016" + FS + "07" + FS + "MSFT-20160726.part.csv.gz");
		
		assertEquals(expected, storage.getTemporarySegmentFile(descr1));
	}
	
	@Test
	public void testGetTemporarySegmentFile_Mkdirs() throws Exception {
		storage.getTemporarySegmentFile(descr1);
		
		assertTrue(new File("fixture/temp/B0/MSFT/2016/07").exists());
	}
	
	@Test (expected=DataStorageException.class)
	public void testGetTemporarySegmentFile_ThrowsIfCannotCreateDirs() throws Exception {
		File root = new File("fixture/dummy");
		storage = new FileStorageImpl(root, "foo", fileConfig);
		
		storage.getTemporarySegmentFile(descr1);
	}
	
	@Test
	public void testCommitTemporarySegmentFile() throws Exception {
		FileUtils.forceMkdir(new File("fixture/temp/B0/MSFT/2005/12"));
		new File("fixture/temp/B0/MSFT/2005/12/MSFT-20051201.part.csv.gz").createNewFile();
		
		storage.commitTemporarySegmentFile(descr2);
		
		assertFalse(new File("fixture/temp/B0/MSFT/2005/12/MSFT-20051201.part.csv.gz").exists());
		assertTrue(new File("fixture/temp/B0/MSFT/2005/12/MSFT-20051201.csv.gz").exists());
	}
	
	@Test (expected=DataStorageException.class)
	public void testComminTemporarySegmentFile_ThrowsIfCannotMove() throws Exception {
		storage.commitTemporarySegmentFile(descr2);
	}
	
	@Test
	public void testListExistingSegments_SDD() throws Exception {
		File root = new File("fixture");
		storage = new FileStorageImpl(root, "bar", fileConfig);
		LocalDate from = LocalDate.of(2006, 6, 14);
		LocalDate to = LocalDate.of(2012, 1, 10);		
		List<LocalDate> expected = new ArrayList<>();
		expected.add(LocalDate.of(2006, 7, 1));
		expected.add(LocalDate.of(2006, 7, 2));
		expected.add(LocalDate.of(2006, 7, 3));
		expected.add(LocalDate.of(2006, 7, 20));
		expected.add(LocalDate.of(2006, 8, 1));
		expected.add(LocalDate.of(2008, 12, 1));
		expected.add(LocalDate.of(2008, 12, 31));
		expected.add(LocalDate.of(2009, 9, 20));
		expected.add(LocalDate.of(2009, 9, 22));
		expected.add(LocalDate.of(2012, 1, 9));
		expected.add(LocalDate.of(2012, 1, 10));
		
		assertEquals(expected, storage.listExistingSegments(symbol1, from, to));
	}
	
	@Test
	public void testListExistingSegments_SDI_EndOfData() throws Exception {
		File root = new File("fixture");
		storage = new FileStorageImpl(root, "bar", fileConfig);
		LocalDate from = LocalDate.of(2008, 12, 10);
		
		List<LocalDate> actual = storage.listExistingSegments(symbol1, from, 100);
		
		List<LocalDate> expected = new ArrayList<>();
		expected.add(LocalDate.of(2008, 12, 31));
		expected.add(LocalDate.of(2009,  9, 20));
		expected.add(LocalDate.of(2009,  9, 22));
		expected.add(LocalDate.of(2012,  1,  9));
		expected.add(LocalDate.of(2012,  1, 10));
		expected.add(LocalDate.of(2012,  1, 11));
		expected.add(LocalDate.of(2012,  2,  1));
		expected.add(LocalDate.of(2013,  5,  1));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testListExistingSegments_SDI_LimitByMaxCount() throws Exception {
		File root = new File("fixture");
		storage = new FileStorageImpl(root, "bar", fileConfig);
		LocalDate from = LocalDate.of(2008, 12, 10);
		
		List<LocalDate> actual = storage.listExistingSegments(symbol1, from, 5);

		List<LocalDate> expected = new ArrayList<>();
		expected.add(LocalDate.of(2008, 12, 31));
		expected.add(LocalDate.of(2009,  9, 20));
		expected.add(LocalDate.of(2009,  9, 22));
		expected.add(LocalDate.of(2012,  1,  9));
		expected.add(LocalDate.of(2012,  1, 10));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testListExistingSegments_SDI_DateFromInclusive() throws Exception {
		File root = new File("fixture");
		storage = new FileStorageImpl(root, "bar", fileConfig);
		LocalDate from = LocalDate.of(2009, 9, 22);
		
		List<LocalDate> actual = storage.listExistingSegments(symbol1, from, 3);

		List<LocalDate> expected = new ArrayList<>();
		expected.add(LocalDate.of(2009,  9, 22));
		expected.add(LocalDate.of(2012,  1,  9));
		expected.add(LocalDate.of(2012,  1, 10));
		assertEquals(expected, actual);
	}
	
	@Test (expected=DataStorageException.class)
	public void testGetDataFileForWriting_ThrowsIfCannotCreateDirs() throws Exception {
		File root = new File("fixture/dummy");
		storage = new FileStorageImpl(root, "foo", fileConfig);
		
		storage.getDataFileForWriting(symbol1);
	}
	
	@Test
	public void testGetDataFile_Mkdirs() throws Exception {
		storage.getDataFileForWriting(symbol1);
		
		assertTrue(new File("fixture/temp/B0/MSFT").exists());
	}

	@Test
	public void testGetDataFileForWriting() throws Exception {
		File expected = new File("fixture/temp/B0/MSFT/MSFT.csv.gz");
		
		File actual = storage.getDataFileForWriting(symbol1);
		
		assertEquals(expected, actual);
		assertTrue(new File("fixture/temp/B0/MSFT").exists());
	}
	
	@Test
	public void testGetDataFile() throws Exception {
		File expected = new File("fixture/temp/B0/MSFT/MSFT.csv.gz");
		
		File actual = storage.getDataFile(symbol1);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testScanForSymbols_() throws Exception {
		new File(root, "zulu").mkdirs(); // must be skipped by L1 filter
		new File(root, "charlie").mkdirs(); // must be skipped by L1 filter
		new File(root, "15/RG1EU%2D11%2E16/2016").mkdirs();
		new File(root, "15/W4EXU%2D9%2E16/2016").mkdirs();
		new File(root, "84/MTSI%2D12%2E16/2009").mkdirs();
		new File(root, "84/A@B@C").mkdirs(); // must be skipped by L2 filter
		new File(root, "C6/RVI%2D8%2E16/2000").mkdirs();
		
		Set<Symbol> actual = storage.scanForSymbols();
		
		Set<Symbol> expected = new HashSet<>();
		expected.add(new Symbol("RG1EU-11.16"));
		expected.add(new Symbol("W4EXU-9.16"));
		expected.add(new Symbol("MTSI-12.16"));
		expected.add(new Symbol("RVI-8.16"));
		assertEquals(expected, actual);
	}

}
