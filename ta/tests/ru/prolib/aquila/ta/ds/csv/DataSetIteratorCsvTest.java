package ru.prolib.aquila.ta.ds.csv;

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.*;

import com.csvreader.CsvReader;

public class DataSetIteratorCsvTest {
	private DataSetIteratorCsv dataSet;
	private CsvReader csv;
	
	@BeforeClass
	static public void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.ALL);
	}

	@Before
	public void setUp() throws Exception {
		csv = CsvReader.parse("header1, header2, h3\n" +
							  "123.456,foobar,190\n" +
							  "555.444,\"gucci gucci\",1259987");
		csv.readHeaders();
		csv.readRecord();
		dataSet = new DataSetIteratorCsv(csv);
	}
	
	@Test
	public void testAccessors() throws Exception {
		assertSame(csv, dataSet.getCsvReader());
	}
	
	@Test
	public void testGetString_Ok() throws Exception {
		assertEquals("foobar", dataSet.getString("header2"));
	}
	
	@Test (expected=DataSetIteratorCsvColumnNotExistsException.class)
	public void testGetString_ThrowsNotExists() throws Exception {
		dataSet.getString("kukaracha");
	}
	
	@Test (expected=DataSetIteratorCsvIoException.class)
	public void testGetString_ThrowsIoError() throws Exception {
		csv.close();
		dataSet.getString("header2");
	}
	
	@Test
	public void testGetDouble_Ok() throws Exception {
		assertEquals(123.456d, dataSet.getDouble("header1"), 0.001d);
	}
	
	@Test (expected=DataSetIteratorCsvColumnNotExistsException.class)
	public void testGetDouble_ThrowsNotExists() throws Exception {
		dataSet.getDouble("zulu4");
	}
	
	@Test (expected=DataSetIteratorCsvIoException.class)
	public void testGetDouble_ThrowsIoError() throws Exception {
		csv.close();
		dataSet.getDouble("header1");
	}
	
	@Test (expected=DataSetIteratorCsvFormatException.class)
	public void testGetDouble_ThrowsFormatException() throws Exception {
		dataSet.getDouble("header2");
	}
	
	@Test
	public void testGetLong_Ok() throws Exception {
		assertEquals((Long)190L, dataSet.getLong("h3"));
	}
	
	@Test (expected=DataSetIteratorCsvColumnNotExistsException.class)
	public void testGetLong_ThrowsNotExists() throws Exception {
		dataSet.getLong("zulu");
	}
	
	@Test (expected=DataSetIteratorCsvIoException.class)
	public void testGetLong_ThrowsIoError() throws Exception {
		csv.close();
		dataSet.getLong("h3");
	}
	
	@Test (expected=DataSetIteratorCsvFormatException.class)
	public void testGetLong_ThrowsFormatException() throws Exception {
		dataSet.getLong("header2");
	}
	
	@Test (expected=DataSetIteratorCsvUnsupportedException.class)
	public void testGetDate() throws Exception {
		dataSet.getDate("foobar");
	}
	
	@Test
	public void testNext_Ok() throws Exception {
		assertTrue(dataSet.next());
		assertEquals((Double)555.444d, dataSet.getDouble("header1"), 0.001d);
		assertEquals("gucci gucci", dataSet.getString("header2"));
		assertEquals((Long)1259987L, dataSet.getLong("h3"));
		assertFalse(dataSet.next());
	}
	
	@Test (expected=DataSetIteratorCsvIoException.class)
	public void testNext_ThrowsIoException() throws Exception {
		csv.close();
		dataSet.next();
	}
	
	@Test (expected=IOException.class)
	public void testClose_Ok() throws Exception {
		dataSet.close();
		dataSet.close();
		csv.readRecord(); // throws
	}

}
