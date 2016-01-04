package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.*;
import org.threeten.extra.Interval;

import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.BusinessEntities.Trade;

/**
 * 2013-03-11<br>
 * $Id: CandleSeriesImplTest.java 566 2013-03-11 01:52:40Z whirlwind $
 */
public class CandleSeriesImplTest {
	private CandleSeriesImpl series;
	private Instant time1, time2, time3;
	private Interval int1, int2, int3;
	private Candle candle1, candle2, candle3;

	@Before
	public void setUp() throws Exception {
		series = new CandleSeriesImpl(TimeFrame.M5, "foo", 512);
		time1 = Instant.parse("2013-10-07T11:00:00Z");
		time2 = time1.plusSeconds(5 * 60);
		time3 = time2.plusSeconds(5 * 60);
		int1 = TimeFrame.M5.getInterval(time1);
		int2 = TimeFrame.M5.getInterval(time2);
		int3 = TimeFrame.M5.getInterval(time3);
		candle1 = new Candle(int1, 144440d, 144440d, 143130d, 143210d, 39621L);
		candle2 = new Candle(int2, 143230d, 143390d, 143100d, 143290d, 12279L);
		candle3 = new Candle(int3, 143280d, 143320d, 143110d, 143190d, 11990L);
	}
	
	@After
	public void tearDown() throws Exception {

	}
	
	@Test
	public void testConstruct1() throws Exception {
		series = new CandleSeriesImpl(TimeFrame.M15);
		assertEquals(Series.DEFAULT_ID, series.getId());
		assertEquals(SeriesImpl.STORAGE_NOT_LIMITED, series.getStorageLimit());
		assertEquals(TimeFrame.M15, series.getTimeFrame());
	}
	
	@Test
	public void testConstruct2() throws Exception {
		series = new CandleSeriesImpl(TimeFrame.M1, "zulu24");
		assertEquals("zulu24", series.getId());
		assertEquals(SeriesImpl.STORAGE_NOT_LIMITED, series.getStorageLimit());
		assertEquals(TimeFrame.M1, series.getTimeFrame());
	}
	
	@Test
	public void testConstruct3() throws Exception {
		series = new CandleSeriesImpl(TimeFrame.M10, "zulu24", 128);
		assertEquals("zulu24", series.getId());
		assertEquals(128, series.getStorageLimit());
		assertEquals(TimeFrame.M10, series.getTimeFrame());
	}
	
	@Test
	public void testGetOpenSeries() throws Exception {
		series.add(candle1);
		series.add(candle2);
		series.add(candle3);
		Double expected[] = { 144440d, 143230d, 143280d };
		Series<Double> s = series.getOpenSeries();
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
		Series<Double> s = series.getHighSeries();
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
		Series<Double> s = series.getLowSeries();
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
		Series<Double> s = series.getCloseSeries();
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
		Series<Double> s = series.getVolumeSeries();
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
		Series<Interval> s = series.getIntervalSeries();
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
		series.add(new Candle(TimeFrame.M3.getInterval(time1),0d,0d,0d,0d,0L));
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
		Candle bad = new Candle(TimeFrame.M10.getInterval(time1),
				145000d, 145200d, 144950d, 144980d, 41924L);
		series.add(candle1);
		series.set(bad);
	}
	
	@Test
	public void testSet_NudgePOAForAggregatedCandle() throws Exception {
		series.aggregate(Tick.of(time1.plusSeconds(3 * 60), 142350d, 120), true);
		Candle expected = new Candle(candle1.getInterval(),
				142350d, 142350d, 142350d, 142350d, 120L);
		assertEquals(expected, series.get());
		assertEquals(time1.plusSeconds(3 * 60), series.getPOA());
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
				Tick.of(time1.plusSeconds(60 * 1), 144440d,     1),
				Tick.of(time1.plusSeconds(60 * 1), 143130d,    20),
				Tick.of(time1.plusSeconds(60 * 3), 143210d, 39600),
				
				// candle 2
				Tick.of(time2,				   143230d,     9),
				Tick.of(time2.plusSeconds(60 * 2), 143290d,    70),
				Tick.of(time2.plusSeconds(60 * 3), 143390d,   200),
				Tick.of(time2.plusSeconds(60 * 4), 143290d, 12000),
				
				// candle3
				Tick.of(time3.plusSeconds(60 * 1), 143280d,    90),
				Tick.of(time3.plusSeconds(60 * 2), 143110d,   900),
				Tick.of(time3.plusSeconds(60 * 3), 143320d,  1000),
				Tick.of(time3.plusSeconds(60 * 4), 143190d, 10000),
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
		Instant poa[] = {
				time1.plusSeconds(60 * 1),
				time1.plusSeconds(60 * 1),
				time1.plusSeconds(60 * 3),
				
				time2,
				time2.plusSeconds(60 * 2),
				time2.plusSeconds(60 * 3),
				time2.plusSeconds(60 * 4),
				
				time3.plusSeconds(60 * 1),
				time3.plusSeconds(60 * 2),
				time3.plusSeconds(60 * 3),
				time3.plusSeconds(60 * 4),
		};
		
		assertEquals(tick.length, candle.length);
		assertEquals(tick.length, poa.length);
		
		for ( int i = 0; i < tick.length; i ++ ) {
			String msg = "At #" + i;
			action.aggregate(tick[i]);
			assertEquals(msg, candle[i], series.get());
			assertEquals(msg, poa[i], series.getPOA());
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
		series.aggregate(Tick.of(time1.plusSeconds(60 * 2), 144440d, 1), false);
		series.aggregate(Tick.of(time1.plusSeconds(60 * 1), 144390d, 1), false);
	}
	
	@Test  (expected=OutOfDateException.class)
	public void testAggregateTick1_ThrowsOutOfDate() throws Exception {
		series.aggregate(Tick.of(time1.plusSeconds(60 * 2), 144440d, 1));
		series.aggregate(Tick.of(time1.plusSeconds(60 * 1), 144390d, 1));
	}
	
	@Test
	public void testAggregateTick2_Silent_SkipsIfOutOfDate() throws Exception {
		series.aggregate(Tick.of(time1.plusSeconds(60 * 2), 144440d, 1), false);
		
		series.aggregate(Tick.of(time1.plusSeconds(60 * 1), 144390d, 1), true);
		
		Candle exp = new Candle(int1, 144440d, 144440d, 144440d, 144440d, 1L);
		assertEquals(exp, series.get());
		assertEquals(time1.plusSeconds(60 * 2), series.getPOA());
	}
	
	/**
	 * Конструктор сделки.
	 * <p>
	 * @param time время
	 * @param price цена
	 * @param qty количество
	 * @return
	 */
	private Trade trade(Instant time, double price, long qty) {
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
				trade(time1.plusSeconds(60 * 1), 144440d,     1L),
				trade(time1.plusSeconds(60 * 1), 143130d,    20L),
				trade(time1.plusSeconds(60 * 3), 143210d, 39600L),
				
				// candle 2
				trade(time2,				143230d,     9L),
				trade(time2.plusSeconds(60 * 2), 143290d,    70L),
				trade(time2.plusSeconds(60 * 3), 143390d,   200L),
				trade(time2.plusSeconds(60 * 4), 143290d, 12000L),
				
				// candle3
				trade(time3.plusSeconds(60 * 1), 143280d,    90L),
				trade(time3.plusSeconds(60 * 2), 143110d,   900L),
				trade(time3.plusSeconds(60 * 3), 143320d,  1000L),
				trade(time3.plusSeconds(60 * 4), 143190d, 10000L),
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
		Instant poa[] = {
				time1.plusSeconds(60 * 1),
				time1.plusSeconds(60 * 1),
				time1.plusSeconds(60 * 3),
				
				time2,
				time2.plusSeconds(60 * 2),
				time2.plusSeconds(60 * 3),
				time2.plusSeconds(60 * 4),
				
				time3.plusSeconds(60 * 1),
				time3.plusSeconds(60 * 2),
				time3.plusSeconds(60 * 3),
				time3.plusSeconds(60 * 4),
		};
		
		assertEquals(trade.length, candle.length);
		assertEquals(trade.length, poa.length);
		
		for ( int i = 0; i < trade.length; i ++ ) {
			String msg = "At #" + i;
			action.aggregate(trade[i]);
			assertEquals(msg, candle[i], series.get());
			assertEquals(msg, poa[i], series.getPOA());
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
		series.aggregate(trade(time1.plusSeconds(60 * 2), 144440d, 1L), false);
		series.aggregate(trade(time1.plusSeconds(60 * 1), 144390d, 1L), false);
	}

	@Test  (expected=OutOfDateException.class)
	public void testAggregateTrade1_ThrowsOutOfDate() throws Exception {
		series.aggregate(trade(time1.plusSeconds(60 * 2), 144440d, 1L));
		series.aggregate(trade(time1.plusSeconds(60 * 1), 144390d, 1L));
	}

	@Test
	public void testAggregateTrade2_Silent_SkipsIfOutOfDate() throws Exception {
		series.aggregate(trade(time1.plusSeconds(60 * 2), 144440d, 1L), false);
		
		series.aggregate(trade(time1.plusSeconds(60 * 1), 144390d, 1L), true);
		
		Candle exp = new Candle(int1, 144440d, 144440d, 144440d, 144440d, 1L);
		assertEquals(exp, series.get());
		assertEquals(time1.plusSeconds(60 * 2), series.getPOA());
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
			new Candle(TimeFrame.M2.getInterval(time1.plusSeconds(60 * 1)),
					144440d, 144440d, 144390d, 144400d,     1L),
			new Candle(TimeFrame.M1.getInterval(time1.plusSeconds(60 * 2)),
					144410d, 144420d, 143130d, 143200d,    20L),
			new Candle(TimeFrame.M1.getInterval(time1.plusSeconds(60 * 4)),
					143190d, 144050d, 143280d, 143290d, 39600L),
			
			// candle 2
			new Candle(TimeFrame.M1.getInterval(time2),
					143230d, 143240d, 143230d, 143240d,     9L),
			new Candle(TimeFrame.M1.getInterval(time2.plusSeconds(60 * 2)),
					143240d, 143300d, 143240d, 143290d,    70L),
			new Candle(TimeFrame.M1.getInterval(time2.plusSeconds(60 * 3)),
					143300d, 143390d, 143290d, 143390d,   200L),
			new Candle(TimeFrame.M1.getInterval(time2.plusSeconds(60 * 4)),
					143320d, 143330d, 143240d, 143290d, 12000L),
			
			// candle3
			new Candle(TimeFrame.M1.getInterval(time3),
					143210d, 143280d, 143200d, 143280d,    90L),
			new Candle(TimeFrame.M1.getInterval(time3.plusSeconds(60 * 1)),
					143220d, 143260d, 143110d, 143110d,   900L),
			new Candle(TimeFrame.M1.getInterval(time3.plusSeconds(60 * 2)),
					143270d, 143320d, 143240d, 143320d,  1000L),
			new Candle(TimeFrame.M1.getInterval(time3.plusSeconds(60 * 3)),
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
		Instant poa[] = {
			// candle 1 POA changes
			time1.plusSeconds(60 * 2),
			time1.plusSeconds(60 * 3),
			time1.plusSeconds(60 * 5),
			
			// candle 2 POA changes
			time2.plusSeconds(60 * 1),
			time2.plusSeconds(60 * 3),
			time2.plusSeconds(60 * 4),
			time2.plusSeconds(60 * 5),
			
			// candle 3 POA changes
			time3.plusSeconds(60 * 1),
			time3.plusSeconds(60 * 2),
			time3.plusSeconds(60 * 3),
			time3.plusSeconds(60 * 4),
		};
		
		assertEquals(input.length, candle.length);
		assertEquals(input.length, poa.length);
		
		for ( int i = 0; i < input.length; i ++ ) {
			String msg = "At #" + i;
			action.aggregate(input[i]);
			assertEquals(msg, candle[i], series.get());
			assertEquals(msg, poa[i], series.getPOA());
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
		series.aggregate(new Candle(TimeFrame.M10.getInterval(time1),
				143200d, 143290d, 143190d, 143250d, 1L), false);
	}
	
	@Test (expected=OutOfIntervalException.class)
	public void testAggregateCandle2_Silent_ThrowsIfBiggerTF()
		throws Exception
	{
		series.aggregate(new Candle(TimeFrame.M10.getInterval(time1),
				143200d, 143290d, 143190d, 143250d, 1L), true);
	}
	
	@Test (expected=OutOfIntervalException.class)
	public void testAggregateCandle1_ThrowsIfBiggerTF()
		throws Exception
	{
		series.aggregate(new Candle(TimeFrame.M10.getInterval(time1),
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
		Instant time = Instant.parse("2013-10-09T00:00:00Z");
		Candle c1, c2;
		c1 = new Candle(TimeFrame.M3.getInterval(time),
				146110d, 146110d, 145910d, 145930d, 1L);
		c2 = new Candle(TimeFrame.M3.getInterval(c1.getEndTime()),
				145940d, 145960d, 145820d, 145910d, 1L);
		
		action.aggregate(c1);
		assertEquals(time.plusSeconds(60 * 3), series.getPOA());
		
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
		
		series.aggregate(Instant.parse("2013-10-07T11:13:00Z"));
		assertEquals(Instant.parse("2013-10-07T11:13:00Z"), series.getPOA());
		
		series.aggregate(Instant.parse("2013-10-07T11:14:00Z"), false);
		assertEquals(Instant.parse("2013-10-07T11:14:00Z"), series.getPOA());
		
		series.aggregate(Instant.parse("2013-10-07T11:16:00Z"), true);
		assertEquals(Instant.parse("2013-10-07T11:16:00Z"), series.getPOA());
	}
	
	@Test
	public void testAggregateTime2_Strict_ThrowsIfOutOfDate() throws Exception {
		series.add(candle1);
		assertEquals(Instant.parse("2013-10-07T11:05:00Z"), series.getPOA());
		
		try {
			series.aggregate(Instant.parse("2013-10-07T10:59:00Z"), false);
			fail("Expected: " + OutOfDateException.class.getSimpleName());
		} catch ( OutOfDateException e ) {
			
		}
	}
	
	@Test
	public void testAggregateTime1_ThrowsIfOutOfDate() throws Exception {
		series.add(candle1);
		
		try {
			series.aggregate(Instant.parse("2013-10-07T10:59:00Z"));
			fail("Expected: " + OutOfDateException.class.getSimpleName());
		} catch ( OutOfDateException e ) {
			
		}
	}
	
	@Test
	public void testAggregateTime2_Silent_SkipsIfOutOfDate() throws Exception {
		series.add(candle1);
		
		series.aggregate(Instant.parse("2013-10-07T10:59:00Z"), true);
	
		assertEquals(candle1.getEndTime(), series.getPOA());
	}
	
	@Test
	public void testAggregateTime2_Strict_FirstTime() throws Exception {
		series.aggregate(Instant.parse("2013-10-07T08:00:00Z"), false);
		assertEquals(Instant.parse("2013-10-07T08:00:00Z"), series.getPOA());
	}
	
	@Test
	public void testAggregateTime2_Silent_FirstTime() throws Exception {
		series.aggregate(Instant.parse("2013-10-07T08:00:00Z"), true);
		assertEquals(Instant.parse("2013-10-07T08:00:00Z"), series.getPOA());
	}
	
	@Test
	public void testAggregateTime1_FirstTime() throws Exception {
		series.aggregate(Instant.parse("2013-10-07T08:00:00Z"));
		assertEquals(Instant.parse("2013-10-07T08:00:00Z"), series.getPOA());
	}

	@Test
	public void testFindFirstIntradayCandle() throws Exception {
		assertNull(series.findFirstIntradayCandle());
		
		Instant prev = LocalDateTime.ofInstant(candle1.getStartTime(),
				ZoneOffset.UTC).minusDays(1).toInstant(ZoneOffset.UTC);
		TimeFrame t = TimeFrame.M5;
		Candle candle0 = new Candle(t.getInterval(prev), 0d, 0d, 0d, 0d, 0L);
		series.add(candle0);
		
		assertEquals(candle0, series.findFirstIntradayCandle());
		
		series.add(candle1);
		series.add(candle2);
		series.add(candle3);

		assertEquals(candle1, series.findFirstIntradayCandle());
	}

}
