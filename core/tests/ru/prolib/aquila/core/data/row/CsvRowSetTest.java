package ru.prolib.aquila.core.data.row;


import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.log4j.BasicConfigurator;
import org.junit.*;

/**
 * 2013-03-03<br>
 * $Id: CsvRowSetTest.java 563 2013-03-08 20:02:34Z whirlwind $
 */
public class CsvRowSetTest {
	private static final String TICKER = "<TICKER>";
	private static final String PER = "<PER>";
	private static final String DATE = "<DATE>";
	private static final String TIME = "<TIME>";
	private static final String OPEN = "<OPEN>";
	private static final String HIGH = "<HIGH>";
	private static final String LOW = "<LOW>";
	private static final String CLOSE = "<CLOSE>";
	//<TICKER>,<PER>,<DATE>,<TIME>,<OPEN>,<HIGH>,<LOW>,<CLOSE>
	private static String fixture[][] = {
		{"20130301","100000","152910.0000000","152910.0000000",
				"152270.0000000","152350.0000000"},
		{"20130301","100500","152350.0000000","152430.0000000",
				"152260.0000000","152370.0000000"},
		{"20130301","101000","152370.0000000","152380.0000000",
				"152060.0000000","152200.0000000"},
		{"20130301","101500","152200.0000000","152250.0000000",
				"152130.0000000","152190.0000000"},
	};
	
	private File file;
	private CsvRowSet rs;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}
	
	@Before
	public void setUp() throws Exception {
		file = new File("fixture/SPFB.RTS.txt");
		rs = new CsvRowSet(file);
	}
	
	/**
	 * Проверить ряд на соответствие ряду фикстуры.
	 * <p>
	 * @param row ряд
	 * @param index индекс ряда фикстуры
	 */
	private void assertRow(Row row, int index) {
		assertEquals("SPFB.RTS", row.get(TICKER));
		assertEquals("5", row.get(PER));
		assertEquals(fixture[index][0], row.get(DATE));
		assertEquals(fixture[index][1], row.get(TIME));
		assertEquals(fixture[index][2], row.get(OPEN));
		assertEquals(fixture[index][3], row.get(HIGH));
		assertEquals(fixture[index][4], row.get(LOW));
		assertEquals(fixture[index][5], row.get(CLOSE));		
	}
	
	@Test
	public void testReset() throws Exception {
		for ( int i = 0; i < 2; i ++ ) {
			assertTrue(rs.next());
			assertRow(rs, i);
		}
		rs.reset();
		for ( int i = 0; i < 4; i ++ ) {
			assertTrue(rs.next());
			assertRow(rs, i);
		}
		assertFalse(rs.next());
	}
	
	@Test
	public void testClose() throws Exception {
		for ( int i = 0; i < 2; i ++ ) {
			assertTrue(rs.next());
			assertRow(rs, i);
		}
		rs.close(); // same as reset
		for ( int i = 0; i < 4; i ++ ) {
			assertTrue(rs.next());
			assertRow(rs, i);
		}
		assertFalse(rs.next());
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGet_ThrowsIfNotPositioned() throws Exception {
		rs.get(TICKER);
	}
	
	@Test (expected=FileNotFoundException.class)
	public void testConstruct_ThrowsIfFileNotExists() throws Exception {
		file = new File("fixture/notexists.txt");
		new CsvRowSet(file);
	}

	@Test (expected=RowSetException.class)
	public void testNext_ThrowsIfFileNotExists() throws Exception {
		file = File.createTempFile("csvtest", null);
		rs = new CsvRowSet(file);
		file.delete();
		rs.next();
	}
	
	@Test
	public void testReset_IgnoredIfNotOpened() throws Exception {
		rs.reset();
	}

}
