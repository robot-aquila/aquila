package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;

import org.joda.time.Interval;
import org.junit.*;

import ru.prolib.aquila.core.*;

/**
 * 2012-04-09<br>
 * $Id: SeriesFactoryImplTest.java 566 2013-03-11 01:52:40Z whirlwind $
 */
public class SeriesFactoryImplTest {
	private EventSystem es;
	private SeriesFactoryImpl factory1,factory2;
	
	@Before
	public void setUp() throws Exception {
		es = new EventSystemImpl();
		es.getEventQueue().start();
		factory1 = new SeriesFactoryImpl(es);
		factory2 = new SeriesFactoryImpl(es, 128);
	}
	
	@After
	public void tearDown() throws Exception {
		es.getEventQueue().stop();
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertEquals(SeriesImpl.STORAGE_NOT_LIMITED, factory1.getLengthLimit());
		assertEquals(128, factory2.getLengthLimit());
	}
	
	@Test
	public void testCreateBoolean() throws Exception {
		assertEquals(new SeriesImpl<Boolean>(es, Series.DEFAULT_ID, 0),
				factory1.createBoolean());
		assertEquals(new SeriesImpl<Boolean>(es, "foo", 0),
				factory1.createBoolean("foo"));
		assertEquals(new SeriesImpl<Boolean>(es, Series.DEFAULT_ID, 128),
				factory2.createBoolean());
		assertEquals(new SeriesImpl<Boolean>(es, "bar", 128),
				factory2.createBoolean("bar"));
	}
	
	@Test
	public void testCreateCandle() throws Exception {
		assertEquals(new CandleSeriesImpl(es, Timeframe.M1, Series.DEFAULT_ID, 0),
				factory1.createCandle(Timeframe.M1));
		assertEquals(new CandleSeriesImpl(es, Timeframe.M5, "zulu", 0),
				factory1.createCandle(Timeframe.M5, "zulu"));
		assertEquals(new CandleSeriesImpl(es, Timeframe.M10,Series.DEFAULT_ID, 128),
				factory2.createCandle(Timeframe.M10));
		assertEquals(new CandleSeriesImpl(es, Timeframe.M15, "pimba", 128),
				factory2.createCandle(Timeframe.M15, "pimba"));
	}

	@Test
	public void testCreateInterval() throws Exception {
		assertEquals(new SeriesImpl<Interval>(es, Series.DEFAULT_ID, 0),
				factory1.createInterval());
		assertEquals(new SeriesImpl<Interval>(es, "mobi", 0),
				factory1.createInterval("mobi"));
		assertEquals(new SeriesImpl<Interval>(es, Series.DEFAULT_ID, 128),
				factory2.createInterval());
		assertEquals(new SeriesImpl<Interval>(es, "zippo", 128),
				factory2.createInterval("zippo"));
	}

	@Test
	public void testCreateDouble() throws Exception {
		assertEquals(new SeriesImpl<Double>(es, Series.DEFAULT_ID, 0),
				factory1.createDouble());
		assertEquals(new SeriesImpl<Double>(es, "jamal", 0),
				factory1.createDouble("jamal"));
		assertEquals(new SeriesImpl<Double>(es, Series.DEFAULT_ID, 128),
				factory2.createDouble());
		assertEquals(new SeriesImpl<Double>(es, "panam", 128),
				factory2.createDouble("panam"));
	}

	@Test
	public void testCreateInteger() throws Exception {
		assertEquals(new SeriesImpl<Integer>(es, Series.DEFAULT_ID, 0),
				factory1.createInteger());
		assertEquals(new SeriesImpl<Integer>(es, "zulu", 0),
				factory1.createInteger("zulu"));
		assertEquals(new SeriesImpl<Integer>(es, Series.DEFAULT_ID, 128),
				factory2.createInteger());
		assertEquals(new SeriesImpl<Integer>(es, "illa", 128),
				factory2.createInteger("illa"));
	}

	@Test
	public void testCreateLong() throws Exception {
		assertEquals(new SeriesImpl<Long>(es, Series.DEFAULT_ID, 0),
				factory1.createLong());
		assertEquals(new SeriesImpl<Long>(es, "antares", 0),
				factory1.createLong("antares"));
		assertEquals(new SeriesImpl<Long>(es, Series.DEFAULT_ID, 128),
				factory2.createLong());
		assertEquals(new SeriesImpl<Long>(es, "bear", 128),
				factory2.createLong("bear"));
	}
	
	@Test
	public void testCreateString() throws Exception {
		assertEquals(new SeriesImpl<String>(es, Series.DEFAULT_ID, 0),
				factory1.createString());
		assertEquals(new SeriesImpl<String>(es, "yummy", 0),
				factory1.createString("yummy"));
		assertEquals(new SeriesImpl<String>(es, Series.DEFAULT_ID, 128),
				factory2.createString());
		assertEquals(new SeriesImpl<String>(es, "zax", 128),
				factory2.createString("zax"));
	}

}
