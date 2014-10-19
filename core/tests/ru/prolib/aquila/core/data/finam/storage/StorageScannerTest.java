package ru.prolib.aquila.core.data.finam.storage;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

public class StorageScannerTest {
	private StorageScanner scanner;
	private String basePath = "fixture/csv-storage/ticks";

	@Before
	public void setUp() throws Exception {
		scanner = new StorageScanner();
	}
	
	@Test
	public void testFindIntradayFiles() throws Exception {
		List<LocalDate> expected = new ArrayList<LocalDate>();
		expected.add(new LocalDate(2014, 10, 5));
		expected.add(new LocalDate(2014, 10, 13));
		expected.add(new LocalDate(2014, 10, 14));
		expected.add(new LocalDate(2014, 10, 15));
		expected.add(new LocalDate(2014, 10, 19));
		
		List<LocalDate> actual =
			scanner.findIntradayFiles(new File(basePath + "/2014/10"),
				new LocalDate(2014, 10, 5));
		
		assertEquals(expected, actual);
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
