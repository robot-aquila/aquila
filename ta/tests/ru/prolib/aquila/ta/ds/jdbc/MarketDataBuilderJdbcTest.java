package ru.prolib.aquila.ta.ds.jdbc;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.ChaosTheory.ServiceLocator;
import ru.prolib.aquila.ChaosTheory.ServiceLocatorImpl;
import ru.prolib.aquila.ta.ds.MarketData;
import ru.prolib.aquila.ta.ds.MarketDataImpl;
import ru.prolib.aquila.ta.ds.MarketDataReaderUseDecorator;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class MarketDataBuilderJdbcTest {
	static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	IMocksControl control;
	DbAccessor dba;
	ServiceLocator locator;
	HierarchicalStreamReader reader;
	MarketDataBuilderJdbc builder;
	static Object fixture[][] = {
		// time, open, high, low, close, vol, median
		{"2010-12-30 23:00:00", 101d, 106d,  97d, 100d, 100d, 101.5d },
		{"2010-12-31 00:00:00", 100d, 105d,  98d,  99d, 100d, 101.5d },
		{"2010-12-31 01:00:00",  99d, 101d,  99d, 100d,  10d, 100.0d },
		{"2010-12-31 02:00:00", 100d, 108d,  96d, 105d, 110d, 102.0d },
		{"2011-01-01 10:00:00", 110d, 118d, 102d, 109d, 118d, 110.0d },
	};
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.ALL);
	}

	@Before
	public void setUp() throws Exception {
		locator = new ServiceLocatorImpl();
		dba = new DbAccessorImpl(System.getProperty("test.database"));
		locator.setDatabase(dba);
		builder = new MarketDataBuilderJdbc();
		createReader("<MarketData type=\"jdbc\" table=\"test_quote_5m\" " +
				"column=\"period_time\" prepareLimit=\"2\" " +
				"updateLimit=\"3\" startTime=\"2011-01-01 00:00:00\" />");
	}
	
	@After
	public void tearDown() throws Exception {
		PreparedStatement sth = dba.get()
			.prepareStatement("TRUNCATE TABLE test_quote_5m");
		sth.executeUpdate();
		sth.close();
	}
	
	private void createReader(String src) throws Exception {
		ByteArrayInputStream stream = new ByteArrayInputStream(src.getBytes());
		reader = new DomDriver().createReader(stream);
	}
	
	/**
	 * Вставить в таблицу строку фикстуры
	 * 
	 * @param row
	 * @throws Exception
	 */
	private void insertRow(Object row[]) throws Exception {
		PreparedStatement sth = dba.get()
			.prepareStatement("INSERT INTO test_quote_5m VALUES (?,?,?,?,?,?)");
		sth.setString(1, (String) row[0]);
		sth.setDouble(2, (Double) row[1]);
		sth.setDouble(3, (Double) row[2]);
		sth.setDouble(4, (Double) row[3]);
		sth.setDouble(5, (Double) row[4]);
		sth.setDouble(6, (Double) row[5]);
		sth.executeUpdate();
		sth.close();
	}
	
	/**
	 * Заполнить таблицу фикстурой.
	 * 
	 * Заполняет таблицу строками фикстуры начиная с ffirst в количестве count
	 * строк.
	 * 
	 * @param ffirst
	 * @param count
	 * @throws Exception
	 */
	private void insertRows(int ffirst, int count) throws Exception {
		for ( int i = 0; i < count; i ++ ) {
			insertRow(fixture[ffirst + i]);
		}
	}
	
	/**
	 * Проверить соответствие рыночных данных.
	 * 
	 * Проверяет соответствуют ли данные о торгах, доступные под указанным
	 * индексом данным фикстуры.
	 * 
	 * @param md данные о торгах
	 * @param i индекс данных в объектах-значениях
	 * @param row фикстура
	 * @throws Exception
	 */
	private void assertRow(MarketData md, int i, Object row[])
		throws Exception
	{
		String msg = "Row #" + i + " ";
		Date time = df.parse((String) row[0]);
		assertEquals(msg + "time",  time, md.getTime().get(i));
		assertEquals(msg + "open",  (Double)row[1], md.getOpen().get(i), 0.01d);
		assertEquals(msg + "high",  (Double)row[2], md.getHigh().get(i), 0.01d);
		assertEquals(msg + "low",   (Double)row[3], md.getLow().get(i), 0.01d);
		assertEquals(msg + "close", (Double)row[4], md.getClose().get(i),0.01d);
		assertEquals(msg + "volume",(Double)row[5],md.getVolume().get(i),0.01d);
		assertEquals(msg + "median",(Double)row[6],md.getMedian().get(i),0.01d);
	}
	
	/**
	 * Проверить соответствие рыночных данных.
	 * 
	 * Проверяет соответствуют ли последовательность данных о торгах данным
	 * фикстуры, начиная с индекса first в количестве count.
	 * 
	 * @param md
	 * @param first
	 * @param count
	 * @throws Exception
	 */
	private void assertRows(MarketData md, int first, int count)
		throws Exception
	{
		assertEquals(count, md.getTime().getLength());
		for ( int i = 0; i < count; i ++ ) {
			assertRow(md, i, fixture[first + i]);
		}
	}
	
	private MarketDataReaderJdbc getMdr(MarketDataImpl md) throws Exception {
		return (MarketDataReaderJdbc)
			((MarketDataReaderUseDecorator)md.getMarketDataReader())
				.getReader();
	}
	
	@Test
	public void testCreate_ConstructedState() throws Exception {
		MarketDataImpl md = (MarketDataImpl) builder.create(locator, reader);

		MarketDataReaderJdbc mdr = getMdr(md);
		assertEquals("test_quote_5m", mdr.getTable());
		assertEquals("period_time", mdr.getColumn());
		assertEquals(df.parse("2011-01-01 00:00:00"), mdr.getActualityPoint());
		assertEquals(2, mdr.getPrepareLimit());
		assertEquals(3, mdr.getUpdateLimit());
	}
	
	@Test
	public void testCreate_PrepareAndUpdate() throws Exception {
		MarketDataImpl md = (MarketDataImpl) builder.create(locator, reader);
		
		MarketDataReaderJdbc mdr = getMdr(md);
		
		insertRows(0, 4);
		md.prepare();
		assertEquals(df.parse("2011-01-01 00:00:00"), mdr.getActualityPoint());
		assertRows(md, 2, 2);
		
		insertRows(4, 1);
		md.update();
		assertEquals(df.parse("2011-01-01 10:00:00"), mdr.getActualityPoint());
		assertRows(md, 2, 3);
	}

}
