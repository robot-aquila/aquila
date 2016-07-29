package ru.prolib.aquila.data.storage.file;

import static org.junit.Assert.*;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.DatedSymbol;
import ru.prolib.aquila.data.storage.file.FileStorageImpl;

public class FileStorageImplTest {
	private static String FS = File.separator;
	private static Symbol symbol1 = new Symbol("MSFT");
	private static LocalDate date1 = LocalDate.of(2016, 7, 26),
			date2 = LocalDate.of(2005, 12, 1);
	private static DatedSymbol descr1 = new DatedSymbol(symbol1, date1),
			descr2 = new DatedSymbol(symbol1, date2);
	private static FilesetInfo filesetInfo;
	private FileStorageImpl storage;
	private File root = new File("fixture/temp");
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		filesetInfo = FilesetInfo.createInstance(".csv.gz", ".part.csv.gz");
	}

	@Before
	public void setUp() throws Exception {
		FileUtils.forceMkdir(root);
		storage = new FileStorageImpl(new File("fixture/temp"), filesetInfo);
	}
	
	@After
	public void tearDown() throws Exception {
		FileUtils.deleteDirectory(root);
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
		storage = new FileStorageImpl(new File("fixture/dummy"), filesetInfo);
		
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
	public void testListExistingSegments() throws Exception {
		storage = new FileStorageImpl(new File("fixture"), filesetInfo);
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

}
