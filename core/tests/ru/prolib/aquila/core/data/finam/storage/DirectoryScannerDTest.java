package ru.prolib.aquila.core.data.finam.storage;

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

public class DirectoryScannerDTest {
	private static final DateTimeFormatter df;
	
	static {
		df = DateTimeFormat.forPattern("yyyy-MM-dd");
	}
	
	private DirectoryScannerD scanner;
	private String basePath = "fixture/csv-storage/ticks";

	@Before
	public void setUp() throws Exception {
		scanner = new DirectoryScannerD("GAZP-EQBR-RUR-STK-");
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
		final String pfx = "GAZP-EQBR-RUR-STK-",
				fpfx = basePath + "/2014/10/" + pfx;
		
		List<FileEntry> actual = new Vector<FileEntry>(),
				expected = new Vector<FileEntry>();
		expected.add(fileEntry(fpfx, "20141005.csv.gz", "2014-10-05"));
		expected.add(fileEntry(fpfx, "20141013.csv",    "2014-10-13"));
		expected.add(fileEntry(fpfx, "20141014.csv",    "2014-10-14"));
		expected.add(fileEntry(fpfx, "20141015.csv.gz", "2014-10-15"));
		expected.add(fileEntry(fpfx, "20141019.csv",    "2014-10-19"));

		Aqiterator<FileEntry> it =
				scanner.makeScan(new FileEntry(new File(basePath + "/2014/10"),
						new LocalDate(2014, 10, 5)));
		while ( it.next() ) {
			actual.add(it.item());
		}
		
		assertEquals(expected, actual);
	}
	


}
