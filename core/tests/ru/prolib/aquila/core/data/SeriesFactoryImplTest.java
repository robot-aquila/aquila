package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;
import org.junit.*;

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
	
	@Test
	public void testConstruct() throws Exception {
		assertEquals(SeriesImpl.STORAGE_NOT_LIMITED, factory1.getLengthLimit());
		assertEquals(128, factory2.getLengthLimit());
	}
	
	@Test
	public void testCreateBoolean() throws Exception {
		assertEquals(new SeriesImpl<Boolean>(Series.DEFAULT_ID, 0),
				factory1.createBoolean());
		assertEquals(new SeriesImpl<Boolean>("foo", 0),
				factory1.createBoolean("foo"));
		assertEquals(new SeriesImpl<Boolean>(Series.DEFAULT_ID, 128),
				factory2.createBoolean());
		assertEquals(new SeriesImpl<Boolean>("bar", 128),
				factory2.createBoolean("bar"));
	}
	
	@Test
	public void testCreateCandle() throws Exception {
		assertEquals(new CandleSeriesImpl(Series.DEFAULT_ID, 0),
				factory1.createCandle());
		assertEquals(new CandleSeriesImpl("zulu", 0),
				factory1.createCandle("zulu"));
		assertEquals(new CandleSeriesImpl(Series.DEFAULT_ID, 128),
				factory2.createCandle());
		assertEquals(new CandleSeriesImpl("pimba", 128),
				factory2.createCandle("pimba"));
	}

	@Test
	public void testCreateTime() throws Exception {
		assertEquals(new TimeSeriesImpl(Series.DEFAULT_ID, 0),
				factory1.createTime());
		assertEquals(new TimeSeriesImpl("mobi", 0),
				factory1.createTime("mobi"));
		assertEquals(new TimeSeriesImpl(Series.DEFAULT_ID, 128),
				factory2.createTime());
		assertEquals(new TimeSeriesImpl("zippo", 128),
				factory2.createTime("zippo"));
	}

	@Test
	public void testCreateDouble() throws Exception {
		assertEquals(new DataSeriesImpl(Series.DEFAULT_ID, 0),
				factory1.createDouble());
		assertEquals(new DataSeriesImpl("jamal", 0),
				factory1.createDouble("jamal"));
		assertEquals(new DataSeriesImpl(Series.DEFAULT_ID, 128),
				factory2.createDouble());
		assertEquals(new DataSeriesImpl("panam", 128),
				factory2.createDouble("panam"));
	}

	@Test
	public void testCreateInteger() throws Exception {
		assertEquals(new SeriesImpl<Integer>(Series.DEFAULT_ID, 0),
				factory1.createInteger());
		assertEquals(new SeriesImpl<Integer>("zulu", 0),
				factory1.createInteger("zulu"));
		assertEquals(new SeriesImpl<Integer>(Series.DEFAULT_ID, 128),
				factory2.createInteger());
		assertEquals(new SeriesImpl<Integer>("illa", 128),
				factory2.createInteger("illa"));
	}

	@Test
	public void testCreateLong() throws Exception {
		assertEquals(new SeriesImpl<Long>(Series.DEFAULT_ID, 0),
				factory1.createLong());
		assertEquals(new SeriesImpl<Long>("antares", 0),
				factory1.createLong("antares"));
		assertEquals(new SeriesImpl<Long>(Series.DEFAULT_ID, 128),
				factory2.createLong());
		assertEquals(new SeriesImpl<Long>("bear", 128),
				factory2.createLong("bear"));
	}
	
	@Test
	public void testCreateString() throws Exception {
		assertEquals(new SeriesImpl<String>(Series.DEFAULT_ID, 0),
				factory1.createString());
		assertEquals(new SeriesImpl<String>("yummy", 0),
				factory1.createString("yummy"));
		assertEquals(new SeriesImpl<String>(Series.DEFAULT_ID, 128),
				factory2.createString());
		assertEquals(new SeriesImpl<String>("zax", 128),
				factory2.createString("zax"));
	}

}
