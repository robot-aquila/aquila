package ru.prolib.aquila.utils.finexp.futures;

import static org.junit.Assert.*;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.data.storage.DatedSymbol;

public class DataStorageImplTest {
	private static String FS = File.separator;
	private static Symbol symbol1 = new Symbol("RTS-9.16"), symbol2 = new Symbol("MSFT");
	private DataStorageImpl storage;
	private File root = new File("fixture/temp");

	@Before
	public void setUp() throws Exception {
		FileUtils.forceMkdir(root);
		storage = new DataStorageImpl(new File("fixture/temp"));
	}
	
	@After
	public void tearDown() throws Exception {
		FileUtils.deleteDirectory(root);
	}
	
	@Test
	public void testGetSegmentTemporaryFile() throws Exception {
		File expected = new File("fixture" + FS + "temp" + FS + "B0" + FS + "MSFT"
				+ FS + "2016" + FS + "07" + FS + "MSFT-20160726.part.csv.gz");
		assertEquals(expected, storage.getSegmentTemporaryFile(new DatedSymbol(symbol2, LocalDate.of(2016, 7, 26))));
	}
	
	@Test
	public void testGetSegmentTemporaryFile_Mkdirs() throws Exception {
		storage.getSegmentTemporaryFile(new DatedSymbol(symbol2, LocalDate.of(2016, 7, 26)));
		
		assertTrue(new File("fixture/temp/B0/MSFT/2016/07").exists());
	}
	
	@Test (expected=DataStorageException.class)
	public void testGetSegmentTemporaryFile_ThrowsIfCannotCreateDirs() throws Exception {
		storage = new DataStorageImpl(new File("fixture/foobar"));
		
		storage.getSegmentTemporaryFile(new DatedSymbol(symbol1, LocalDate.of(2016, 7, 26)));
	}
	
	@Test
	public void testCommitSegmentTemporaryFile() throws Exception {
		FileUtils.forceMkdir(new File("fixture/temp/B0/MSFT/2005/12"));
		new File("fixture/temp/B0/MSFT/2005/12/MSFT-20051201.part.csv.gz").createNewFile();
		
		storage.commitSegmentTemporaryFile(new DatedSymbol(symbol2, LocalDate.of(2005, 12, 1)));
		
		assertFalse(new File("fixture/temp/B0/MSFT/2005/12/MSFT-20051201.part.csv.gz").exists());
		assertTrue(new File("fixture/temp/B0/MSFT/2005/12/MSFT-20051201.csv.gz").exists());
	}
	
	@Test (expected=DataStorageException.class)
	public void testComminSegmentTemporaryFile_ThrowsIfCannotMove() throws Exception {
		storage.commitSegmentTemporaryFile(new DatedSymbol(symbol2, LocalDate.of(2005, 12, 1)));
	}
	
	@Test
	public void testListExistingSegments() throws Exception {
		storage = new DataStorageImpl(new File("fixture"));
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
		
		assertEquals(expected, storage.listExistingSegments(symbol2, from, to));
	}

}
