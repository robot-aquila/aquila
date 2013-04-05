package ru.prolib.aquila.ta.ds.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.ta.ds.DataSet;
import ru.prolib.aquila.ta.ds.DataSetIterator;


public class MarketDataReaderJdbcTest {
	static Object fixture[][] = {
		// time, open, high, low, close, volume
		{"2011-01-01 00:00:00", 50.25d, 55.40d, 48.15d, 52.95d, 100l},//#00
		{"2011-01-02 00:00:00", 52.95d, 59.45d, 51.10d, 51.10d, 150l},//#01
		{"2011-01-02 00:00:15", 51.10d, 51.10d, 48.25d, 49.00d, 200l},//#02
		{"2011-01-02 00:00:20", 49.00d, 49.50d, 46.80d, 47.45d, 110l},//#03
		{"2011-01-03 00:00:00", 80.15d, 85.25d, 78.60d, 78.75d, 180l},//#04
		{"2011-01-03 00:00:05", 78.75d, 78.75d, 78.75d, 78.75d, 100l},//#05
		{"2011-01-03 00:00:10", 10.25d, 15.40d, 18.15d, 12.95d, 300l},//#06
		{"2011-01-03 00:00:15", 10.15d, 15.25d, 18.60d, 18.75d, 180l},//#07
		{"2011-01-03 00:00:20", 19.00d, 19.50d, 16.80d, 17.45d, 110l},//#08
		{"2011-01-03 00:00:25", 10.25d, 15.40d, 18.15d, 12.95d, 130l},//#09
	};

	static final String table = "test_quote_5m";
	static final String column = "period_time";
	static DbAccessor dba;
	static SimpleDateFormat df;
	MarketDataReaderJdbc reader;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.ALL);
		dba = new DbAccessorImpl(System.getProperty("test.database"));
		df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	}
	
	@Before
	public void setUp() throws Exception {
		reader = new MarketDataReaderJdbc(dba, table, column,
				df.parse("2011-01-02 00:00:20"), 3, 2);
	}
	
	@After
	public void tearDown() throws Exception {
		dba.get().createStatement().executeUpdate("TRUNCATE TABLE " + table);
	}
	
	private void assertRow(String msg, DataSet rs, Object[] fixture)
		throws Exception
	{
		assertEquals(msg, df.parse((String) fixture[0]),
				df.parse(rs.getString(column)));
		assertEquals(msg, (Double) fixture[1], rs.getDouble("open"), 0.01d);
		assertEquals(msg, (Double) fixture[2], rs.getDouble("high"), 0.01d);
		assertEquals(msg, (Double) fixture[3], rs.getDouble("low"), 0.01d);
		assertEquals(msg, (Double) fixture[4], rs.getDouble("close"), 0.01d);
		assertEquals(msg, (Long) fixture[5], rs.getLong("volume"));
	}

	private void insertRow(Object[] fixture) throws Exception {
		PreparedStatement insertSth = dba.get().prepareStatement
			("INSERT INTO " + table + " VALUES (?,?,?,?,?,?)");
		insertSth.setString(1, (String) fixture[0]);
		insertSth.setDouble(2, (Double) fixture[1]);
		insertSth.setDouble(3, (Double) fixture[2]);
		insertSth.setDouble(4, (Double) fixture[3]);
		insertSth.setDouble(5, (Double) fixture[4]);
		insertSth.setLong(6, (Long) fixture[5]);
		insertSth.executeUpdate();
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
	}
	
	@Test
	public void testAccessors() throws Exception {
		assertSame(dba, reader.getDba());
		assertEquals(table, reader.getTable());
		assertEquals(column, reader.getColumn());
		assertEquals(3, reader.getPrepareLimit());
		assertEquals(2, reader.getUpdateLimit());
		assertEquals(df.parse("2011-01-02 00:00:20"), reader.getActualityPoint());
	}
	
	@Test
	public void testPrepare_Ok() throws Exception {
		// preload загрузит записи 1-3, пропустит 0
		for ( int i = 0; i <= 3; i ++ ) { insertRow(fixture[i]); }
		
		DataSetIterator ds = reader.prepare();
		for ( int i = 1; i <= 3; i ++ ) {
			String msg = "At index #" + i;
			assertTrue(msg, ds.next());
			assertRow(msg, ds, fixture[i]);
		}
		assertFalse(ds.next());
		ds.close();
	}
	
	@Test
	public void testUpdate_Ok() throws Exception {
		for ( int i = 0; i <= 3; i ++ ) { insertRow(fixture[i]); }
		reader.prepare();
		
		// Добавлены 3 новые строки, но лимит 2
		for ( int i = 4; i <= 6; i ++ ) { insertRow(fixture[i]); }
		DataSetIterator ds = reader.update();
		for ( int i = 4; i <= 5; i ++ ) {
			String msg = "At index #" + i;
			assertTrue(msg, ds.next());
			assertRow(msg, ds, fixture[i]);
		}
		assertFalse(ds.next());
		ds.close();
		
		for ( int i = 7; i <= 9; i ++ ) { insertRow(fixture[i]); }
		ds = reader.update();
		for ( int i = 6; i <= 7; i ++ ) {
			String msg = "At index #" + i;
			assertTrue(msg, ds.next());
			assertRow(msg, ds, fixture[i]);
		}
		assertFalse(ds.next());
		ds.close();
	}

}
