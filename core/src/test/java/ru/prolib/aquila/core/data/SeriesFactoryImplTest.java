package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;

import org.junit.*;
import org.threeten.extra.Interval;

/**
 * 2012-04-09<br>
 * $Id: SeriesFactoryImplTest.java 566 2013-03-11 01:52:40Z whirlwind $
 */
public class SeriesFactoryImplTest {
	private SeriesFactoryImpl factory1,factory2;
	
	@Before
	public void setUp() throws Exception {
		factory1 = new SeriesFactoryImpl();
		factory2 = new SeriesFactoryImpl(128);
	}
	
	@After
	public void tearDown() throws Exception {

	}
	
	@Test
	public void testConstruct() throws Exception {
		assertEquals(SeriesImpl.STORAGE_NOT_LIMITED, factory1.getLengthLimit());
		assertEquals(128, factory2.getLengthLimit());
	}
	
	@Test
	public void testCreateBoolean() throws Exception {
		SeriesImpl<Boolean>
		series = (SeriesImpl<Boolean>) factory1.createBoolean();
		assertNotNull(series);
		assertEquals(Series.DEFAULT_ID, series.getId());
		assertEquals(0, series.getStorageLimit());

		series = (SeriesImpl<Boolean>) factory1.createBoolean("foo");
		assertNotNull(series);
		assertEquals("foo", series.getId());
		assertEquals(0, series.getStorageLimit());
		
		series = (SeriesImpl<Boolean>) factory2.createBoolean();
		assertNotNull(series);
		assertEquals(Series.DEFAULT_ID, series.getId());
		assertEquals(128, series.getStorageLimit());

		series = (SeriesImpl<Boolean>) factory2.createBoolean("bar");
		assertNotNull(series);
		assertEquals("bar", series.getId());
		assertEquals(128, series.getStorageLimit());
	}
	
	@Test
	public void testCreateCandle() throws Exception {
		CandleSeriesImpl
		series = (CandleSeriesImpl) factory1.createCandle(ZTFrame.M1);
		assertNotNull(series);
		assertEquals(ZTFrame.M1, series.getTimeFrame());
		assertEquals(Series.DEFAULT_ID, series.getId());
		assertEquals(0, series.getStorageLimit());
		
		series = (CandleSeriesImpl) factory1.createCandle(ZTFrame.M5, "zulu");
		assertNotNull(series);
		assertEquals(ZTFrame.M5, series.getTimeFrame());
		assertEquals("zulu", series.getId());
		assertEquals(0, series.getStorageLimit());
		
		series = (CandleSeriesImpl) factory2.createCandle(ZTFrame.M10);
		assertNotNull(series);
		assertEquals(ZTFrame.M10, series.getTimeFrame());
		assertEquals(Series.DEFAULT_ID, series.getId());
		assertEquals(128, series.getStorageLimit());
		
		series = (CandleSeriesImpl) factory2.createCandle(ZTFrame.M15, "pimba");
		assertNotNull(series);
		assertEquals(ZTFrame.M15, series.getTimeFrame());
		assertEquals("pimba", series.getId());
		assertEquals(128, series.getStorageLimit());
	}

	@Test
	public void testCreateInterval() throws Exception {
		SeriesImpl<Interval>
		series = (SeriesImpl<Interval>) factory1.createInterval();
		assertNotNull(series);
		assertEquals(Series.DEFAULT_ID, series.getId());
		assertEquals(0, series.getStorageLimit());
		
		series = (SeriesImpl<Interval>) factory1.createInterval("mobi");
		assertNotNull(series);
		assertEquals("mobi", series.getId());
		assertEquals(0, series.getStorageLimit());
		
		series = (SeriesImpl<Interval>) factory2.createInterval();
		assertNotNull(series);
		assertEquals(Series.DEFAULT_ID, series.getId());
		assertEquals(128, series.getStorageLimit());
		
		series = (SeriesImpl<Interval>) factory2.createInterval("zippo");
		assertNotNull(series);
		assertEquals("zippo", series.getId());
		assertEquals(128, series.getStorageLimit());
	}

	@Test
	public void testCreateDouble() throws Exception {
		SeriesImpl<Double>
		series = (SeriesImpl<Double>) factory1.createDouble();
		assertNotNull(series);
		assertEquals(Series.DEFAULT_ID, series.getId());
		assertEquals(0, series.getStorageLimit());
		
		series = (SeriesImpl<Double>) factory1.createDouble("jamal");
		assertNotNull(series);
		assertEquals("jamal", series.getId());
		assertEquals(0, series.getStorageLimit());
		
		series = (SeriesImpl<Double>) factory2.createDouble();
		assertNotNull(series);
		assertEquals(Series.DEFAULT_ID, series.getId());
		assertEquals(128, series.getStorageLimit());
		
		series = (SeriesImpl<Double>) factory2.createDouble("panam");
		assertNotNull(series);
		assertEquals("panam", series.getId());
		assertEquals(128, series.getStorageLimit());
	}

	@Test
	public void testCreateInteger() throws Exception {
		SeriesImpl<Integer>
		series = (SeriesImpl<Integer>) factory1.createInteger();
		assertNotNull(series);
		assertEquals(Series.DEFAULT_ID, series.getId());
		assertEquals(0, series.getStorageLimit());
		
		series = (SeriesImpl<Integer>) factory1.createInteger("zulu");
		assertNotNull(series);
		assertEquals("zulu", series.getId());
		assertEquals(0, series.getStorageLimit());
		
		series = (SeriesImpl<Integer>) factory2.createInteger();
		assertNotNull(series);
		assertEquals(Series.DEFAULT_ID, series.getId());
		assertEquals(128, series.getStorageLimit());
		
		series = (SeriesImpl<Integer>) factory2.createInteger("illa");
		assertNotNull(series);
		assertEquals("illa", series.getId());
		assertEquals(128, series.getStorageLimit());
	}

	@Test
	public void testCreateLong() throws Exception {
		SeriesImpl<Long>
		series = (SeriesImpl<Long>) factory1.createLong();
		assertNotNull(series);
		assertEquals(Series.DEFAULT_ID, series.getId());
		assertEquals(0, series.getStorageLimit());
		
		series = (SeriesImpl<Long>) factory1.createLong("antares");
		assertNotNull(series);
		assertEquals("antares", series.getId());
		assertEquals(0, series.getStorageLimit());
		
		series = (SeriesImpl<Long>) factory2.createLong();
		assertNotNull(series);
		assertEquals(Series.DEFAULT_ID, series.getId());
		assertEquals(128, series.getStorageLimit());
		
		series = (SeriesImpl<Long>) factory2.createLong("bear");
		assertNotNull(series);
		assertEquals("bear", series.getId());
		assertEquals(128, series.getStorageLimit());
	}
	
	@Test
	public void testCreateString() throws Exception {
		SeriesImpl<String>
		series = (SeriesImpl<String>) factory1.createString();
		assertNotNull(series);
		assertEquals(Series.DEFAULT_ID, series.getId());
		assertEquals(0, series.getStorageLimit());
		
		series = (SeriesImpl<String>) factory1.createString("yummy");
		assertNotNull(series);
		assertEquals("yummy", series.getId());
		assertEquals(0, series.getStorageLimit());

		series = (SeriesImpl<String>) factory2.createString();
		assertNotNull(series);
		assertEquals(Series.DEFAULT_ID, series.getId());
		assertEquals(128, series.getStorageLimit());

		series = (SeriesImpl<String>) factory2.createString("zax");
		assertNotNull(series);
		assertEquals("zax", series.getId());
		assertEquals(128, series.getStorageLimit());
	}

}
