package ru.prolib.aquila.core.data.row;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;

import com.csvreader.CsvReader;

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
	
	private IMocksControl control;
	private File file;
	private CsvRowSet rs;
	private CsvReader reader;
	private IOException exception;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		file = new File("fixture/SPFB.RTS.txt");
		rs = new CsvRowSet(file);
		reader = control.createMock(CsvReader.class);
		exception = new IOException("test");
	}
	
	/**
	 * Проверить ряд на соответствие ряду фикстуры.
	 * <p>
	 * @param row ряд
	 * @param index индекс ряда фикстуры
	 */
	private void assertRow(Row row, int index) throws Exception {
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
	
	@Test (expected=RowException.class)
	public void testGet_ThrowsIfNotPositioned() throws Exception {
		rs.get(TICKER);
	}
	
	@Test
	public void testGet_ThrowsIfReaderGetThrows() throws Exception {
		expect(reader.get(eq(TICKER))).andThrow(exception);
		control.replay();
		rs.setCsvReader(reader);
		
		try {
			rs.get(TICKER);
			fail("Expected: " + RowSetException.class.getSimpleName());
		} catch ( RowException e ) {
			control.verify();
			assertSame(exception, e.getCause());
		}
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
	public void testNext_ThrowsIfReadRecordThrows() throws Exception {
		expect(reader.readRecord()).andThrow(exception);
		reader.close();
		control.replay();
		rs.setCsvReader(reader);
		
		try {
			rs.next();
			fail("Expected: " + RowSetException.class.getSimpleName());
		} catch ( RowException e ) {
			control.verify();
			assertSame(exception, e.getCause());
		}
	}
	
	@Test
	public void testReset_IgnoredIfNotOpened() throws Exception {
		control.replay();
		
		rs.reset();
		
		control.verify();
	}
	
	@Test (expected=RowSetException.class)
	public void testGetRowCopy_ThrowsIfNoReader() throws Exception {
		rs.getRowCopy();
	}
	
	@Test
	public void testGetRowCopy_ThrowsIfGetHeadersThrows() throws Exception {
		expect(reader.getHeaders()).andThrow(exception);
		reader.close();
		control.replay();
		rs.setCsvReader(reader);
		
		try {
			rs.getRowCopy();
			fail("Expected: " + RowSetException.class.getSimpleName());
		} catch ( RowSetException e ) {
			control.verify();
			assertSame(exception, e.getCause());
		}
	}
	
	@Test
	public void testGetRowCopy_ThrowsIfGetValuesThrows() throws Exception {
		expect(reader.getHeaders()).andReturn(new String[] { "foo", "bar" });
		expect(reader.getValues()).andThrow(exception);
		reader.close();
		control.replay();
		rs.setCsvReader(reader);
		
		try {
			rs.getRowCopy();
			fail("Expected: " + RowSetException.class.getSimpleName());
		} catch ( RowSetException e ) {
			control.verify();
			assertSame(exception, e.getCause());
		}
	}
	
	@Test
	public void testGetRowCopy() throws Exception {
		assertTrue(rs.next());
		Row copy = rs.getRowCopy();
		assertRow(rs, 0);
		assertRow(copy, 0);
	}

}
