package ru.prolib.aquila.core.data;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.util.List;
import java.util.Vector;

import org.easymock.IMocksControl;
import org.joda.time.*;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.Trade;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2013-03-11<br>
 * $Id: CandleSeriesImplTest.java 566 2013-03-11 01:52:40Z whirlwind $
 */
public class CandleSeriesImplTest {
	private EventSystem es;
	private IMocksControl control;
	private CandleSeriesImpl series;
	private DateTime time1, time2, time3;
	private Interval int1, int2, int3;
	private Candle candle1, candle2, candle3;

	@Before
	public void setUp() throws Exception {
		es = new EventSystemImpl();
		es.getEventQueue().start();
		control = createStrictControl();
		series = new CandleSeriesImpl(es, Timeframe.M5, "foo", 512);
		time1 = new DateTime(2013, 10, 7, 11, 0, 0);
		time2 = time1.plusMinutes(5);
		time3 = time2.plusMinutes(5);
		int1 = Timeframe.M5.getInterval(time1);
		int2 = Timeframe.M5.getInterval(time2);
		int3 = Timeframe.M5.getInterval(time3);
		candle1 = new Candle(int1, 144440d, 144440d, 143130d, 143210d, 39621L);
		candle2 = new Candle(int2, 143230d, 143390d, 143100d, 143290d, 12279L);
		candle3 = new Candle(int3, 143280d, 143320d, 143110d, 143190d, 11990L);
	}
	
	@After
	public void tearDown() throws Exception {
		es.getEventQueue().stop();
	}
	
	@Test
	public void testConstruct2() throws Exception {
		series = new CandleSeriesImpl(es, Timeframe.M15);
		assertEquals(Series.DEFAULT_ID, series.getId());
		assertEquals(SeriesImpl.STORAGE_NOT_LIMITED, series.getStorageLimit());
		assertEquals(Timeframe.M15, series.getTimeframe());
	}
	
	@Test
	public void testConstruct3() throws Exception {
		series = new CandleSeriesImpl(es, Timeframe.M1, "zulu24");
		assertEquals("zulu24", series.getId());
		assertEquals(SeriesImpl.STORAGE_NOT_LIMITED, series.getStorageLimit());
		assertEquals(Timeframe.M1, series.getTimeframe());
	}
	
	@Test
	public void testConstruct4() throws Exception {
		series = new CandleSeriesImpl(es, Timeframe.M10, "zulu24", 128);
		assertEquals("zulu24", series.getId());
		assertEquals(128, series.getStorageLimit());
		assertEquals(Timeframe.M10, series.getTimeframe());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(series.equals(series));
		assertFalse(series.equals(null));
		assertFalse(series.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		List<Candle> rows1 = new Vector<Candle>();
		rows1.add(candle1);
		rows1.add(candle2);
		List<Candle> rows2 = new Vector<Candle>();
		rows2.add(candle1);
		rows2.add(candle2);
		rows2.add(candle3);
		for ( Candle candle : rows1 ) series.add(candle);
		
		Variant<Timeframe> vTf = new Variant<Timeframe>()
			.add(Timeframe.M5)
			.add(Timeframe.M15);
		Variant<String> vId = new Variant<String>(vTf)
			.add("foo")
			.add("bar");
		Variant<Integer> vLmt = new Variant<Integer>(vId)
			.add(512)
			.add(128);
		Variant<List<Candle>> vRows = new Variant<List<Candle>>(vLmt)
			.add(rows1)
			.add(rows2);
		Variant<?> iterator = vRows;
		int foundCnt = 0;
		CandleSeriesImpl x, found = null;
		do {
			Timeframe t = vTf.get();
			x = new CandleSeriesImpl(es, t, vId.get(), vLmt.get());
			for ( Candle candle : vRows.get() ) {
				DateTime from = null;
				if ( x.getLength() == 0 ) {
					from = candle.getStartTime();
				} else {
					from = x.get().getEndTime();
				}
				x.add(new Candle(t.getInterval(from),
						candle.getOpen(), candle.getHigh(), candle.getLow(),
						candle.getClose(), candle.getVolume()));
			}
			if ( series.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(Timeframe.M5, found.getTimeframe());
		assertEquals("foo", found.getId());
		assertEquals(512, found.getStorageLimit());
		// check series data
		assertEquals(2, found.getLength());
		for ( int i = 0; i < rows1.size(); i ++ ) {
			assertEquals(rows1.get(i), found.get(i));
		}
	}
	
	@Test
	public void testGetOpenSeries() throws Exception {
		series.add(candle1);
		series.add(candle2);
		series.add(candle3);
		Double expected[] = { 144440d, 143230d, 143280d };
		DataSeries s = series.getOpenSeries();
		assertEquals(expected.length, s.getLength());
		for ( int i = 0; i < expected.length; i ++ ) {
			assertEquals("At #" + i, expected[i], s.get(i), 0.1d);
		}
	}
	
	@Test
	public void testGetHighSeries() throws Exception {
		series.add(candle1);
		series.add(candle2);
		series.add(candle3);
		Double expected[] = { 144440d, 143390d, 143320d };
		DataSeries s = series.getHighSeries();
		assertEquals(expected.length, s.getLength());
		for ( int i = 0; i < expected.length; i ++ ) {
			assertEquals("At #" + i, expected[i], s.get(i), 0.1d);
		}
	}

	@Test
	public void testGetLowSeries() throws Exception {
		series.add(candle1);
		series.add(candle2);
		series.add(candle3);
		Double expected[] = { 143130d, 143100d, 143110d };
		DataSeries s = series.getLowSeries();
		assertEquals(expected.length, s.getLength());
		for ( int i = 0; i < expected.length; i ++ ) {
			assertEquals("At #" + i, expected[i], s.get(i), 0.1d);
		}
	}
	
	@Test
	public void testGetCloseSeries() throws Exception {
		series.add(candle1);
		series.add(candle2);
		series.add(candle3);
		Double expected[] = { 143210d, 143290d, 143190d };
		DataSeries s = series.getCloseSeries();
		assertEquals(expected.length, s.getLength());
		for ( int i = 0; i < expected.length; i ++ ) {
			assertEquals("At #" + i, expected[i], s.get(i), 0.1d);
		}
	}

	@Test
	public void testGetVolumeSeries() throws Exception {
		series.add(candle1);
		series.add(candle2);
		series.add(candle3);
		Double expected[] = { 39621d, 12279d, 11990d };
		DataSeries s = series.getVolumeSeries();
		assertEquals(expected.length, s.getLength());
		for ( int i = 0; i < expected.length; i ++ ) {
			assertEquals("At #" + i, expected[i], s.get(i), 0.1d);
		}
	}
	
	@Test
	public void testGetIntervalSeries() throws Exception {
		series.add(candle1);
		series.add(candle2);
		series.add(candle3);
		Interval expected[] = { int1, int2, int3 };
		IntervalSeries s = series.getIntervalSeries();
		assertEquals(expected.length, s.getLength());
		for ( int i = 0; i < expected.length; i ++ ) {
			assertEquals("At #" + i, expected[i], s.get(i));
		}
	}
	
	@Test
	public void testAdd() throws Exception {
		assertNull(series.getPOA());
		
		series.add(candle1);
		assertEquals(1, series.getLength());
		assertEquals(candle1, series.get());
		assertEquals(candle1, series.get(0));
		assertEquals(candle1.getEndTime(), series.getPOA());
		
		series.add(candle2);
		assertEquals(2, series.getLength());
		assertEquals(candle2, series.get());
		assertEquals(candle2, series.get(1));
		assertEquals(candle2.getEndTime(), series.getPOA());
		
		series.add(candle3);
		assertEquals(3, series.getLength());
		assertEquals(candle3, series.get());
		assertEquals(candle3, series.get(2));
		assertEquals(candle3.getEndTime(), series.getPOA());
	}
	
	@Test (expected=OutOfDateException.class)
	public void testAdd_ThrowsIfBeforeAP() throws Exception {
		series.add(candle2);
		series.add(candle1);
	}
	
	@Test (expected=OutOfIntervalException.class)
	public void testAdd_ThrowsIfTimeframeMismatch() throws Exception {
		series.add(new Candle(Timeframe.M3.getInterval(time1),0d,0d,0d,0d,0L));
	}
	
	@Test
	public void testAdd_Events() throws Exception {
		List<Event> expected = new Vector<Event>();
		final List<Event> actual = new Vector<Event>();
		EventListener listener = new EventListener() {
			@Override
			public void onEvent(Event event) {
				actual.add(event);
			}
		};
		series.OnAdded().addSyncListener(listener);
		
		series.add(candle1);
		expected.add(new ValueEvent<Candle>((EventTypeSI) series.OnAdded(), candle1, 0));
		assertEquals(expected, actual);
		
		series.add(candle2);
		expected.add(new ValueEvent<Candle>((EventTypeSI) series.OnAdded(), candle2, 1));
		assertEquals(expected, actual);
		
		series.add(candle3);
		expected.add(new ValueEvent<Candle>((EventTypeSI) series.OnAdded(), candle3, 2));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testSet() throws Exception {
		Candle expected = new Candle(candle1.getInterval(),
				145000d, 145200d, 144950d, 144980d, 41924L);
		series.add(candle1);
		series.set(expected);
		
		assertEquals(expected, series.get());
	}
	
	@Test (expected=ValueNotExistsException.class)
	public void testSet_ThrowsIfNoCandle() throws Exception {
		series.set(candle2);
	}
	
	@Test (expected=OutOfIntervalException.class)
	public void testSet_ThrowsIfIntervalMismatch() throws Exception {
		Candle bad = new Candle(Timeframe.M10.getInterval(time1),
				145000d, 145200d, 144950d, 144980d, 41924L);
		series.add(candle1);
		series.set(bad);
	}
	
	@Test
	public void testSet_NudgePOAForAggregatedCandle() throws Exception {
		series.aggregate(new Tick(time1.plusMinutes(3), 142350d, 120d), true);
		Candle expected = new Candle(candle1.getInterval(),
				142350d, 142350d, 142350d, 142350d, 120L);
		assertEquals(expected, series.get());
		assertEquals(time1.plusMinutes(3), series.getPOA());
	}
	
	/**
	 * Интерфейс действия теста агрегирования.
	 * <p>
	 * Класс используется для передачи вызова метода агрегирования конкретной
	 * сигнатуры в функцию тестирования.
	 */
	interface AggregateAction<T> {
		public void aggregate(T arg) throws Exception;
	}
	
	/**
	 * Протестировать агрегирование тиков.
	 * <p>
	 * Данный метод используется для тестирования методов агрегирования тиков
	 * валидной последовательности: тики датируются последовательно. 
	 * <p>
	 * @param action функция вызова 
	 * @throws Exception
	 */
	private void aggregateTick_TestValidSeq(AggregateAction<Tick> action)
		throws Exception
	{
		Tick tick[] = {
				// candle 1
				new Tick(time1.plusMinutes(1), 144440d,     1d),
				new Tick(time1.plusMinutes(1), 143130d,    20d),
				new Tick(time1.plusMinutes(3), 143210d, 39600d),
				
				// candle 2
				new Tick(time2,				   143230d,     9d),
				new Tick(time2.plusMinutes(2), 143290d,    70d),
				new Tick(time2.plusMinutes(3), 143390d,   200d),
				new Tick(time2.plusMinutes(4), 143290d, 12000d),
				
				// candle3
				new Tick(time3.plusMinutes(1), 143280d,    90d),
				new Tick(time3.plusMinutes(2), 143110d,   900d),
				new Tick(time3.plusMinutes(3), 143320d,  1000d),
				new Tick(time3.plusMinutes(4), 143190d, 10000d),
		};
		Candle candle[] = {
				// candle 1 changes
				new Candle(int1, 144440d, 144440d, 144440d, 144440d,     1L),
				new Candle(int1, 144440d, 144440d, 143130d, 143130d,    21L),
				new Candle(int1, 144440d, 144440d, 143130d, 143210d, 39621L),
				
				// candle 2 changes
				new Candle(int2, 143230d, 143230d, 143230d, 143230d,     9L),
				new Candle(int2, 143230d, 143290d, 143230d, 143290d,    79L),
				new Candle(int2, 143230d, 143390d, 143230d, 143390d,   279L),
				new Candle(int2, 143230d, 143390d, 143230d, 143290d, 12279L),
				
				// candle 3 changes
				new Candle(int3, 143280d, 143280d, 143280d, 143280d,    90L),
				new Candle(int3, 143280d, 143280d, 143110d, 143110d,   990L),
				new Candle(int3, 143280d, 143320d, 143110d, 143320d,  1990L),
				new Candle(int3, 143280d, 143320d, 143110d, 143190d, 11990L),
		};
		Event event[] = {
				new ValueEvent<Candle>((EventTypeSI) series.OnAdded(), candle[0], 0),
				new ValueEvent<Candle>((EventTypeSI) series.OnUpdated(), candle[0], candle[1], 0),
				new ValueEvent<Candle>((EventTypeSI) series.OnUpdated(), candle[1], candle[2], 0),
				
				new ValueEvent<Candle>((EventTypeSI) series.OnAdded(), candle[3], 1),
				new ValueEvent<Candle>((EventTypeSI) series.OnUpdated(), candle[3], candle[4], 1),
				new ValueEvent<Candle>((EventTypeSI) series.OnUpdated(), candle[4], candle[5], 1),
				new ValueEvent<Candle>((EventTypeSI) series.OnUpdated(), candle[5], candle[6], 1),
				
				new ValueEvent<Candle>((EventTypeSI) series.OnAdded(), candle[7], 2),
				new ValueEvent<Candle>((EventTypeSI) series.OnUpdated(), candle[7], candle[8], 2),
				new ValueEvent<Candle>((EventTypeSI) series.OnUpdated(), candle[8], candle[9], 2),
				new ValueEvent<Candle>((EventTypeSI) series.OnUpdated(), candle[9], candle[10],2),
		};
		DateTime poa[] = {
				time1.plusMinutes(1),
				time1.plusMinutes(1),
				time1.plusMinutes(3),
				
				time2,
				time2.plusMinutes(2),
				time2.plusMinutes(3),
				time2.plusMinutes(4),
				
				time3.plusMinutes(1),
				time3.plusMinutes(2),
				time3.plusMinutes(3),
				time3.plusMinutes(4),
		};
		
		assertEquals(tick.length, candle.length);
		assertEquals(tick.length, event.length);
		assertEquals(tick.length, poa.length);
		
		final List<Event> events = new Vector<Event>();
		final EventListener listener = new EventListener() {
			@Override
			public void onEvent(Event event) {
				events.add(event);
			}
		};
		series.OnAdded().addSyncListener(listener);
		series.OnUpdated().addSyncListener(listener);
		for ( int i = 0; i < tick.length; i ++ ) {
			String msg = "At #" + i;
			action.aggregate(tick[i]);
			assertEquals(msg, candle[i], series.get());
			assertEquals(msg, poa[i], series.getPOA());
			
			assertEquals(msg, 1, events.size());
			assertEquals(msg, event[i], events.get(0));
			events.clear();
		}

	}
	
	@Test
	public void testAggregateTick2_Strict_ValidSeq() throws Exception {
		AggregateAction<Tick> action = new AggregateAction<Tick>() {
			@Override
			public void aggregate(Tick tick) throws Exception {
				series.aggregate(tick, false);
			}
		};
		aggregateTick_TestValidSeq(action);
	}
	
	@Test
	public void testAggregateTick2_Silent_ValidSeq() throws Exception {
		AggregateAction<Tick> action = new AggregateAction<Tick>() {
			@Override
			public void aggregate(Tick tick) throws Exception {
				series.aggregate(tick, true);
			}
		};
		aggregateTick_TestValidSeq(action);		
	}

	@Test
	public void testAggregateTick1_ValidSeq() throws Exception {
		AggregateAction<Tick> action = new AggregateAction<Tick>() {
			@Override
			public void aggregate(Tick tick) throws Exception {
				series.aggregate(tick);
			}
		};
		aggregateTick_TestValidSeq(action);		
	}
	
	@Test (expected=OutOfDateException.class)
	public void testAggregateTick2_Strict_ThrowsIfOutOfDate() throws Exception {
		series.aggregate(new Tick(time1.plusMinutes(2), 144440d, 1d), false);
		series.aggregate(new Tick(time1.plusMinutes(1), 144390d, 1d), false);
	}
	
	@Test  (expected=OutOfDateException.class)
	public void testAggregateTick1_ThrowsOutOfDate() throws Exception {
		series.aggregate(new Tick(time1.plusMinutes(2), 144440d, 1d));
		series.aggregate(new Tick(time1.plusMinutes(1), 144390d, 1d));
	}
	
	@Test
	public void testAggregateTick2_Silent_SkipsIfOutOfDate() throws Exception {
		series.aggregate(new Tick(time1.plusMinutes(2), 144440d, 1d), false);
		EventListener listener = control.createMock(EventListener.class);
		series.OnAdded().addListener(listener);
		series.OnUpdated().addListener(listener);
		control.replay();
		
		series.aggregate(new Tick(time1.plusMinutes(1), 144390d, 1d), true);
		
		control.verify();
		Candle exp = new Candle(int1, 144440d, 144440d, 144440d, 144440d, 1L);
		assertEquals(exp, series.get());
		assertEquals(time1.plusMinutes(2), series.getPOA());
	}
	
	/**
	 * Конструктор сделки.
	 * <p>
	 * @param time время
	 * @param price цена
	 * @param qty количество
	 * @return
	 */
	private Trade trade(DateTime time, double price, long qty) {
		Trade trade = new Trade(null);
		trade.setTime(time);
		trade.setPrice(price);
		trade.setQty(qty);
		return trade;
	}
	
	/**
	 * Протестировать агрегирование сделок.
	 * <p>
	 * Данный метод используется для тестирования методов агрегирования сделок
	 * валидной последовательности: сделки датируются последовательно. 
	 * <p>
	 * @param action функция вызова 
	 * @throws Exception
	 */
	private void aggregateTrade_TestValidSeq(AggregateAction<Trade> action)
		throws Exception
	{
		Trade trade[] = {
				// candle 1
				trade(time1.plusMinutes(1), 144440d,     1L),
				trade(time1.plusMinutes(1), 143130d,    20L),
				trade(time1.plusMinutes(3), 143210d, 39600L),
				
				// candle 2
				trade(time2,				143230d,     9L),
				trade(time2.plusMinutes(2), 143290d,    70L),
				trade(time2.plusMinutes(3), 143390d,   200L),
				trade(time2.plusMinutes(4), 143290d, 12000L),
				
				// candle3
				trade(time3.plusMinutes(1), 143280d,    90L),
				trade(time3.plusMinutes(2), 143110d,   900L),
				trade(time3.plusMinutes(3), 143320d,  1000L),
				trade(time3.plusMinutes(4), 143190d, 10000L),
		};
		Candle candle[] = {
				// candle 1 changes
				new Candle(int1, 144440d, 144440d, 144440d, 144440d,     1L),
				new Candle(int1, 144440d, 144440d, 143130d, 143130d,    21L),
				new Candle(int1, 144440d, 144440d, 143130d, 143210d, 39621L),
				
				// candle 2 changes
				new Candle(int2, 143230d, 143230d, 143230d, 143230d,     9L),
				new Candle(int2, 143230d, 143290d, 143230d, 143290d,    79L),
				new Candle(int2, 143230d, 143390d, 143230d, 143390d,   279L),
				new Candle(int2, 143230d, 143390d, 143230d, 143290d, 12279L),
				
				// candle 3 changes
				new Candle(int3, 143280d, 143280d, 143280d, 143280d,    90L),
				new Candle(int3, 143280d, 143280d, 143110d, 143110d,   990L),
				new Candle(int3, 143280d, 143320d, 143110d, 143320d,  1990L),
				new Candle(int3, 143280d, 143320d, 143110d, 143190d, 11990L),
		};
		Event event[] = {
				new ValueEvent<Candle>((EventTypeSI) series.OnAdded(), candle[0], 0),
				new ValueEvent<Candle>((EventTypeSI) series.OnUpdated(), candle[0], candle[1], 0),
				new ValueEvent<Candle>((EventTypeSI) series.OnUpdated(), candle[1], candle[2], 0),
				
				new ValueEvent<Candle>((EventTypeSI) series.OnAdded(), candle[3], 1),
				new ValueEvent<Candle>((EventTypeSI) series.OnUpdated(), candle[3], candle[4], 1),
				new ValueEvent<Candle>((EventTypeSI) series.OnUpdated(), candle[4], candle[5], 1),
				new ValueEvent<Candle>((EventTypeSI) series.OnUpdated(), candle[5], candle[6], 1),
				
				new ValueEvent<Candle>((EventTypeSI) series.OnAdded(), candle[7], 2),
				new ValueEvent<Candle>((EventTypeSI) series.OnUpdated(), candle[7], candle[8], 2),
				new ValueEvent<Candle>((EventTypeSI) series.OnUpdated(), candle[8], candle[9], 2),
				new ValueEvent<Candle>((EventTypeSI) series.OnUpdated(), candle[9], candle[10],2),
		};
		DateTime poa[] = {
				time1.plusMinutes(1),
				time1.plusMinutes(1),
				time1.plusMinutes(3),
				
				time2,
				time2.plusMinutes(2),
				time2.plusMinutes(3),
				time2.plusMinutes(4),
				
				time3.plusMinutes(1),
				time3.plusMinutes(2),
				time3.plusMinutes(3),
				time3.plusMinutes(4),
		};
		
		assertEquals(trade.length, candle.length);
		assertEquals(trade.length, event.length);
		assertEquals(trade.length, poa.length);
		
		final List<Event> events = new Vector<Event>();
		final EventListener listener = new EventListener() {
			@Override
			public void onEvent(Event event) {
				events.add(event);
			}
		};
		series.OnAdded().addSyncListener(listener);
		series.OnUpdated().addSyncListener(listener);
		for ( int i = 0; i < trade.length; i ++ ) {
			String msg = "At #" + i;
			action.aggregate(trade[i]);
			assertEquals(msg, candle[i], series.get());
			assertEquals(msg, poa[i], series.getPOA());
			
			assertEquals(msg, 1, events.size());
			assertEquals(msg, event[i], events.get(0));
			events.clear();
		}
	}
	
	@Test
	public void testAggregateTrade2_Strict_ValidSeq() throws Exception {
		AggregateAction<Trade> action = new AggregateAction<Trade>() {
			@Override
			public void aggregate(Trade trade) throws Exception {
				series.aggregate(trade, false);
			}
		};
		aggregateTrade_TestValidSeq(action);
	}
	
	@Test
	public void testAggregateTrade2_Silent_ValidSeq() throws Exception {
		AggregateAction<Trade> action = new AggregateAction<Trade>() {
			@Override
			public void aggregate(Trade trade) throws Exception {
				series.aggregate(trade, true);
			}
		};
		aggregateTrade_TestValidSeq(action);
	}
	
	@Test
	public void testAggregateTrade1_ValidSeq() throws Exception {
		AggregateAction<Trade> action = new AggregateAction<Trade>() {
			@Override
			public void aggregate(Trade trade) throws Exception {
				series.aggregate(trade);
			}
		};
		aggregateTrade_TestValidSeq(action);
	}

	@Test (expected=OutOfDateException.class)
	public void testAggregateTrade2_Strict_ThrowsIfOutOfDate() throws Exception {
		series.aggregate(trade(time1.plusMinutes(2), 144440d, 1L), false);
		series.aggregate(trade(time1.plusMinutes(1), 144390d, 1L), false);
	}

	@Test  (expected=OutOfDateException.class)
	public void testAggregateTrade1_ThrowsOutOfDate() throws Exception {
		series.aggregate(trade(time1.plusMinutes(2), 144440d, 1L));
		series.aggregate(trade(time1.plusMinutes(1), 144390d, 1L));
	}

	@Test
	public void testAggregateTrade2_Silent_SkipsIfOutOfDate() throws Exception {
		series.aggregate(trade(time1.plusMinutes(2), 144440d, 1L), false);
		EventListener listener = control.createMock(EventListener.class);
		series.OnAdded().addListener(listener);
		series.OnUpdated().addListener(listener);
		control.replay();
		
		series.aggregate(trade(time1.plusMinutes(1), 144390d, 1L), true);
		
		control.verify();
		Candle exp = new Candle(int1, 144440d, 144440d, 144440d, 144440d, 1L);
		assertEquals(exp, series.get());
		assertEquals(time1.plusMinutes(2), series.getPOA());
	}
	
	/**
	 * Протестировать агрегирование свечей.
	 * <p>
	 * Данный метод используется для тестирования методов агрегирования свечей
	 * валидной последовательности: свечи подходящего интервала и датируются
	 * последовательно. 
	 * <p>
	 * @param action функция вызова 
	 * @throws Exception
	 */
	private void aggregateCandle_TestValidSeq(AggregateAction<Candle> action)
		throws Exception
	{
		Candle input[] = {
			// candle 1
			new Candle(Timeframe.M2.getInterval(time1.plusMinutes(1)),
					144440d, 144440d, 144390d, 144400d,     1L),
			new Candle(Timeframe.M1.getInterval(time1.plusMinutes(2)),
					144410d, 144420d, 143130d, 143200d,    20L),
			new Candle(Timeframe.M1.getInterval(time1.plusMinutes(4)),
					143190d, 144050d, 143280d, 143290d, 39600L),
			
			// candle 2
			new Candle(Timeframe.M1.getInterval(time2),
					143230d, 143240d, 143230d, 143240d,     9L),
			new Candle(Timeframe.M1.getInterval(time2.plusMinutes(2)),
					143240d, 143300d, 143240d, 143290d,    70L),
			new Candle(Timeframe.M1.getInterval(time2.plusMinutes(3)),
					143300d, 143390d, 143290d, 143390d,   200L),
			new Candle(Timeframe.M1.getInterval(time2.plusMinutes(4)),
					143320d, 143330d, 143240d, 143290d, 12000L),
			
			// candle3
			new Candle(Timeframe.M1.getInterval(time3),
					143210d, 143280d, 143200d, 143280d,    90L),
			new Candle(Timeframe.M1.getInterval(time3.plusMinutes(1)),
					143220d, 143260d, 143110d, 143110d,   900L),
			new Candle(Timeframe.M1.getInterval(time3.plusMinutes(2)),
					143270d, 143320d, 143240d, 143320d,  1000L),
			new Candle(Timeframe.M1.getInterval(time3.plusMinutes(3)),
					143300d, 143310d, 143200d, 143190d, 10000L),
		};
		Candle candle[] = {
			// candle 1 changes
			new Candle(int1, 144440d, 144440d, 144390d, 144400d,     1L),
			new Candle(int1, 144440d, 144440d, 143130d, 143200d,    21L),
			new Candle(int1, 144440d, 144440d, 143130d, 143290d, 39621L),
			
			// candle 2 changes
			new Candle(int2, 143230d, 143240d, 143230d, 143240d,     9L),
			new Candle(int2, 143230d, 143300d, 143230d, 143290d,    79L),
			new Candle(int2, 143230d, 143390d, 143230d, 143390d,   279L),
			new Candle(int2, 143230d, 143390d, 143230d, 143290d, 12279L),
			
			// candle 3 changes
			new Candle(int3, 143210d, 143280d, 143200d, 143280d,    90L),
			new Candle(int3, 143210d, 143280d, 143110d, 143110d,   990L),
			new Candle(int3, 143210d, 143320d, 143110d, 143320d,  1990L),
			new Candle(int3, 143210d, 143320d, 143110d, 143190d, 11990L),
		};
		Event event[] = {
			// candle 1 events
			new ValueEvent<Candle>((EventTypeSI) series.OnAdded(), candle[0], 0),
			new ValueEvent<Candle>((EventTypeSI) series.OnUpdated(), candle[0], candle[1], 0),
			new ValueEvent<Candle>((EventTypeSI) series.OnUpdated(), candle[1], candle[2], 0),
			
			// candle 2 events
			new ValueEvent<Candle>((EventTypeSI) series.OnAdded(), candle[3], 1),
			new ValueEvent<Candle>((EventTypeSI) series.OnUpdated(), candle[3], candle[4], 1),
			new ValueEvent<Candle>((EventTypeSI) series.OnUpdated(), candle[4], candle[5], 1),
			new ValueEvent<Candle>((EventTypeSI) series.OnUpdated(), candle[5], candle[6], 1),
			
			// candle 3 events
			new ValueEvent<Candle>((EventTypeSI) series.OnAdded(), candle[7], 2),
			new ValueEvent<Candle>((EventTypeSI) series.OnUpdated(), candle[7], candle[8], 2),
			new ValueEvent<Candle>((EventTypeSI) series.OnUpdated(), candle[8], candle[9], 2),
			new ValueEvent<Candle>((EventTypeSI) series.OnUpdated(), candle[9], candle[10],2),
		};
		DateTime poa[] = {
			// candle 1 POA changes
			time1.plusMinutes(2),
			time1.plusMinutes(3),
			time1.plusMinutes(5),
			
			// candle 2 POA changes
			time2.plusMinutes(1),
			time2.plusMinutes(3),
			time2.plusMinutes(4),
			time2.plusMinutes(5),
			
			// candle 3 POA changes
			time3.plusMinutes(1),
			time3.plusMinutes(2),
			time3.plusMinutes(3),
			time3.plusMinutes(4),
		};
		
		assertEquals(input.length, candle.length);
		assertEquals(input.length, event.length);
		assertEquals(input.length, poa.length);
		
		final List<Event> events = new Vector<Event>();
		final EventListener listener = new EventListener() {
			@Override
			public void onEvent(Event event) {
				events.add(event);
			}
		};
		series.OnAdded().addSyncListener(listener);
		series.OnUpdated().addSyncListener(listener);
		for ( int i = 0; i < input.length; i ++ ) {
			String msg = "At #" + i;
			action.aggregate(input[i]);
			assertEquals(msg, candle[i], series.get());
			assertEquals(msg, poa[i], series.getPOA());
			
			assertEquals(msg, 1, events.size());
			assertEquals(msg, event[i], events.get(0));
			events.clear();
		}
	}
	
	@Test
	public void testAggregateCandle2_Strict_ValidSeq() throws Exception {
		AggregateAction<Candle> action = new AggregateAction<Candle>() {
			@Override
			public void aggregate(Candle candle) throws Exception {
				series.aggregate(candle, false);
			}
		};
		aggregateCandle_TestValidSeq(action);
	}
	
	@Test
	public void testAggregateCandle2_Silent_ValidSeq() throws Exception {
		AggregateAction<Candle> action = new AggregateAction<Candle>() {
			@Override
			public void aggregate(Candle candle) throws Exception {
				series.aggregate(candle, true);
			}
		};
		aggregateCandle_TestValidSeq(action);
	}
	
	@Test
	public void testAggregateCandle1_ValidSeq() throws Exception {
		AggregateAction<Candle> action = new AggregateAction<Candle>() {
			@Override
			public void aggregate(Candle candle) throws Exception {
				series.aggregate(candle);
			}
		};
		aggregateCandle_TestValidSeq(action);
	}
	
	@Test
	public void testAggregateCandle2_Strict_ThrowsIfOutOfDate()
		throws Exception
	{
		series.add(candle2);
		try {
			series.aggregate(candle1, false);
			fail("Expected: " + OutOfDateException.class.getSimpleName());
		} catch ( OutOfDateException e ) {
			
		}
	}
	
	@Test
	public void testAggregateCandle1_ThrowsIfOutOfDate() throws Exception {
		series.add(candle2);
		try {
			series.aggregate(candle1);
			fail("Expected: " + OutOfDateException.class.getSimpleName());
		} catch ( OutOfDateException e ) {
			
		}
	}
	
	@Test
	public void testAggregateCandle2_Silent_SkipsIfOutOfDate()
		throws Exception
	{
		series.add(candle2);
		series.aggregate(candle1, true);
		
		assertEquals(1, series.getLength());
		assertEquals(candle2, series.get());
		assertEquals(candle2.getEndTime(), series.getPOA());
	}
	
	@Test (expected=OutOfIntervalException.class)
	public void testAggregateCandle2_Strict_ThrowsIfBiggerTF()
		throws Exception
	{
		series.aggregate(new Candle(Timeframe.M10.getInterval(time1),
				143200d, 143290d, 143190d, 143250d, 1L), false);
	}
	
	@Test (expected=OutOfIntervalException.class)
	public void testAggregateCandle2_Silent_ThrowsIfBiggerTF()
		throws Exception
	{
		series.aggregate(new Candle(Timeframe.M10.getInterval(time1),
				143200d, 143290d, 143190d, 143250d, 1L), true);
	}
	
	@Test (expected=OutOfIntervalException.class)
	public void testAggregateCandle1_ThrowsIfBiggerTF()
		throws Exception
	{
		series.aggregate(new Candle(Timeframe.M10.getInterval(time1),
				143200d, 143290d, 143190d, 143250d, 1L));
	}
	
	/**
	 * Тест реакции на перекрытие интервала при агрегировании свечи.
	 * <p>
	 * @param action действие
	 * @throws Exception
	 */
	private void
		aggregateCandle_TestOverlapsInterval(AggregateAction<Candle> action)
			throws Exception
	{
		DateTime time = new DateTime(2013, 10, 9, 0, 0, 0, 0);
		Candle c1, c2;
		c1 = new Candle(Timeframe.M3.getInterval(time),
				146110d, 146110d, 145910d, 145930d, 1L);
		c2 = new Candle(Timeframe.M3.getInterval(c1.getEndTime()),
				145940d, 145960d, 145820d, 145910d, 1L);
		
		action.aggregate(c1);
		assertEquals(time.plusMinutes(3), series.getPOA());
		
		try {
			action.aggregate(c2);
			fail("Expected: " + OutOfIntervalException.class.getSimpleName());
		} catch ( OutOfIntervalException e ) {
			
		}		
	}
	
	@Test
	public void testAggregateCandle2_Strict_ThrowsIfOverlapsInterval()
		throws Exception
	{
		AggregateAction<Candle> action = new AggregateAction<Candle>() {
			@Override
			public void aggregate(Candle candle) throws Exception {
				series.aggregate(candle, false);
			}
		};
		aggregateCandle_TestOverlapsInterval(action);
	}
	
	@Test
	public void testAggregateCandle2_Silent_ThrowsIfOverlapsInterval()
		throws Exception
	{
		AggregateAction<Candle> action = new AggregateAction<Candle>() {
			@Override
			public void aggregate(Candle candle) throws Exception {
				series.aggregate(candle, true);
			}
		};
		aggregateCandle_TestOverlapsInterval(action);		
	}
	
	@Test
	public void testAggregateCandle1_ThrowsIfOverlapsInterval()
		throws Exception
	{
		AggregateAction<Candle> action = new AggregateAction<Candle>() {
			@Override
			public void aggregate(Candle candle) throws Exception {
				series.aggregate(candle);
			}
		};
		aggregateCandle_TestOverlapsInterval(action);
	}
	
	@Test
	public void testAggregateTime() throws Exception {
		series.add(candle1);
		series.add(candle2);
		
		series.aggregate(new DateTime(2013, 10, 7, 11, 13, 0, 0));
		assertEquals(new DateTime(2013, 10, 7, 11, 13, 0, 0), series.getPOA());
		
		series.aggregate(new DateTime(2013, 10, 7, 11, 14, 0, 0), false);
		assertEquals(new DateTime(2013, 10, 7, 11, 14, 0, 0), series.getPOA());
		
		series.aggregate(new DateTime(2013, 10, 7, 11, 16, 0, 0), true);
		assertEquals(new DateTime(2013, 10, 7, 11, 16, 0, 0), series.getPOA());
	}
	
	@Test
	public void testAggregateTime2_Strict_ThrowsIfOutOfDate() throws Exception {
		series.add(candle1);
		assertEquals(new DateTime(2013, 10, 7, 11, 5, 0, 0), series.getPOA());
		
		try {
			series.aggregate(new DateTime(2013, 10, 7, 10, 59, 0, 0), false);
			fail("Expected: " + OutOfDateException.class.getSimpleName());
		} catch ( OutOfDateException e ) {
			
		}
	}
	
	@Test
	public void testAggregateTime1_ThrowsIfOutOfDate() throws Exception {
		series.add(candle1);
		
		try {
			series.aggregate(new DateTime(2013, 10, 7, 10, 59, 0, 0));
			fail("Expected: " + OutOfDateException.class.getSimpleName());
		} catch ( OutOfDateException e ) {
			
		}
	}
	
	@Test
	public void testAggregateTime2_Silent_SkipsIfOutOfDate() throws Exception {
		series.add(candle1);
		
		series.aggregate(new DateTime(2013, 10, 7, 10, 59, 0, 0), true);
	
		assertEquals(candle1.getEndTime(), series.getPOA());
	}
	
	@Test
	public void testAggregateTime2_Strict_FirstTime() throws Exception {
		series.aggregate(new DateTime(2013, 10, 7, 8, 0, 0, 0), false);
		assertEquals(new DateTime(2013, 10, 7, 8, 0, 0), series.getPOA());
	}
	
	@Test
	public void testAggregateTime2_Silent_FirstTime() throws Exception {
		series.aggregate(new DateTime(2013, 10, 7, 8, 0, 0, 0), true);
		assertEquals(new DateTime(2013, 10, 7, 8, 0, 0), series.getPOA());
	}
	
	@Test
	public void testAggregateTime1_FirstTime() throws Exception {
		series.aggregate(new DateTime(2013, 10, 7, 8, 0, 0, 0));
		assertEquals(new DateTime(2013, 10, 7, 8, 0, 0), series.getPOA());
	}

	@Test
	public void testFindFirstIntradayCandle() throws Exception {
		assertNull(series.findFirstIntradayCandle());
		
		DateTime prev = candle1.getStartTime().minusDays(1);
		Timeframe t = Timeframe.M5;
		Candle candle0 = new Candle(t.getInterval(prev), 0d, 0d, 0d, 0d, 0L);
		series.add(candle0);
		
		assertEquals(candle0, series.findFirstIntradayCandle());
		
		series.add(candle1);
		series.add(candle2);
		series.add(candle3);

		assertEquals(candle1, series.findFirstIntradayCandle());
	}

}
