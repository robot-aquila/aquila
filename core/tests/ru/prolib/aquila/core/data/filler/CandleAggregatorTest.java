package ru.prolib.aquila.core.data.filler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.Trade;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.data.filler.CandleAggregator;
import ru.prolib.aquila.core.utils.*;

public class CandleAggregatorTest {
	private IMocksControl control;
	private EditableCandleSeries candles;
	private AlignTime aligner;
	private CandleAggregator aggr1, aggr2;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		candles = control.createMock(EditableCandleSeries.class);
		aligner = control.createMock(AlignTime.class);
		aggr1 = new CandleAggregator(5);
		aggr2 = new CandleAggregator(candles, aligner);
	}
	
	/**
	 * Создать тестовую сделку.
	 * <p>
	 * @param time время
	 * @param price цена
	 * @param qty количество
	 * @return сделка
	 * @throws Exception
	 */
	private Trade newTrade(String time, Double price, int qty)
		throws Exception
	{
		Trade t = new Trade(null);
		t.setTime(newTime(time));
		t.setPrice(price);
		t.setQty(new Long(qty));
		return t;
	}
	
	/**
	 * Создать тестовую свечу.
	 * <p>
	 * @param time время
	 * @param open цена открытия
	 * @param hi максимальная цена
	 * @param lo минимальная цена
	 * @param close цена закрытия
	 * @param qty количество
	 * @return свеча
	 * @throws ParseException
	 */
	private Candle newCandle(String time, Double open, Double hi, Double lo,
			Double close, int qty) throws ParseException
	{
		return new Candle(newTime(time), open, hi, lo, close, qty);
	}
	
	/**
	 * Создать тестовый тик.
	 * <p>
	 * @param time время
	 * @param price цена
	 * @param qty количество
	 * @return сделка
	 */
	private Tick newTick(String time, Double price, int qty) throws Exception {
		return new Tick(newTime(time), price, (double) qty);
	}
	
	/**
	 * Создать тестовую временную метку.
	 * <p>
	 * @param time время
	 * @return временная метка
	 * @throws ParseException 
	 */
	private Date newTime(String time) throws ParseException {
		final SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
		return df.parse(time);
	}
	
	@Test
	public void testConstruct1() throws Exception {
		CandleSeriesImpl series = (CandleSeriesImpl) aggr1.getCandles();
		assertEquals(SeriesImpl.STORAGE_NOT_LIMITED, series.getStorageLimit());
		assertEquals(Series.DEFAULT_ID, series.getId());
		AlignMinute align = (AlignMinute) aggr1.getTimeAligner();
		assertEquals(5, align.getPeriod());
	}
	
	@Test
	public void testGetOpen() throws Exception {
		assertSame(aggr1.getCandles().getOpen(), aggr1.getOpen());
	}
	
	@Test
	public void testGetHigh() throws Exception {
		assertSame(aggr1.getCandles().getHigh(), aggr1.getHigh());
	}
	
	@Test
	public void testGetLow() throws Exception {
		assertSame(aggr1.getCandles().getLow(), aggr1.getLow());
	}
	
	@Test
	public void testGetClose() throws Exception {
		assertSame(aggr1.getCandles().getClose(), aggr1.getClose());
	}
	
	@Test
	public void testGetTime() throws Exception {
		assertSame(aggr1.getCandles().getTime(), aggr1.getTime());
	}
	
	@Test
	public void testGetVolume() throws Exception {
		assertSame(aggr1.getCandles().getVolume(), aggr1.getVolume());
	}
	
	@Test
	public void testGetId() throws Exception {
		expect(candles.getId()).andReturn("some ID");
		control.replay();
		
		assertEquals("some ID", aggr2.getId());
		
		control.verify();
	}
	
	@Test
	public void testGet0() throws Exception {
		Candle c = new Candle(new Date(), 10d, 0l);
		expect(candles.get()).andReturn(c);
		control.replay();
		
		assertSame(c, aggr2.get());
		
		control.verify();
	}
	
	@Test
	public void testGet1() throws Exception {
		Candle c = new Candle(new Date(), 20d, 10l);
		expect(candles.get(eq(18))).andReturn(c);
		control.replay();
		
		assertSame(c, aggr2.get(18));
		
		control.verify();
	}
	
	@Test
	public void testGetLength() throws Exception {
		expect(candles.getLength()).andReturn(124);
		control.replay();
		
		assertEquals(124, aggr2.getLength());
		
		control.verify();
	}
	
	@Test
	public void testOnAdd() throws Exception {
		assertSame(aggr1.getCandles().OnAdd(), aggr1.OnAdd());
	}
	
	@Test
	public void testOnUpd() throws Exception {
		assertSame(aggr1.getCandles().OnUpd(), aggr1.OnUpd());
	}

	@Test
	public void testAdd_Tick() throws Exception {
		final class FR {
			final Tick tick;
			final boolean expected;
			FR(Tick tick, boolean expected) {
				this.tick = tick;
				this.expected = expected;
			}
		}
		FR fix[] = {
			new FR(newTick("13:00:00.000", 2d, 5), false),
			new FR(newTick("13:00:10.000", 4d, 0), false),
			new FR(newTick("13:04:15.000", 1d, 1), false),
			new FR(newTick("13:04:59.000", 3d, 3), false),
			
			new FR(newTick("13:05:00.000", 8d, 1), true),
			new FR(newTick("13:08:15.000", 2d, 1), false),
			
			new FR(newTick("14:01:24.000", 1d, 1), true),
			
			new FR(newTick("14:09:15.000", 5d, 2), true),
			new FR(newTick("14:09:28.000", 3d, 1), false),
			new FR(newTick("14:09:59.000", 1d, 1), false),
			
			new FR(newTick("15:00:00.000", 0d, 0), true),
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At#" + i;
			assertEquals(msg, fix[i].expected, aggr1.add(fix[i].tick));
			assertEquals(msg, fix[i].tick.getTime(), aggr1.getActualityPoint());
		}
		Candle expected[] = {
			newCandle("13:00:00.000", 2d, 4d, 1d, 3d, 9),
			newCandle("13:05:00.000", 8d, 8d, 2d, 2d, 2),
			newCandle("14:00:00.000", 1d, 1d, 1d, 1d, 1),
			newCandle("14:05:00.000", 5d, 5d, 1d, 1d, 4),
		};
		assertEquals(expected.length, aggr1.getLength());
		for ( int i = 0; i < expected.length; i ++ ) {
			String msg = "At#" + i;
			assertEquals(msg, expected[i], aggr1.get(i));
		}
	}
	
	@Test
	public void testAdd_Tick_SkipEqualsToLast() throws Exception {
		assertFalse(aggr1.add(newTick("13:00:00.000", 2d, 5)));
		assertFalse(aggr1.add(newTick("13:00:00.000", 2d, 5)));
		assertFalse(aggr1.add(newTick("13:00:00.000", 2d, 5)));
		assertTrue(aggr1.add(newTick("13:05:00.000", 1d, 1)));
		assertEquals(newCandle("13:00:00.000", 2d, 2d, 2d, 2d, 5), aggr1.get());
	}
	
	@Test
	public void testAdd_Tick_SkipNullTick() throws Exception {
		assertFalse(aggr1.add(newTick("13:00:00.000", 2d, 5)));
		assertFalse(aggr1.add((Tick) null));
		assertFalse(aggr1.add(newTick("13:01:00.000", 3d, 1)));
		assertTrue(aggr1.add(newTick("13:05:00.000", 0d, 0)));
		assertEquals(newCandle("13:00:00.000", 2d, 3d, 2d, 3d, 6), aggr1.get());
	}
	
	@Test
	public void testAdd_Time() throws Exception {
		assertFalse(aggr1.add(newTick("13:00:00.000", 2d, 5)));
		assertFalse(aggr1.add(newTime("10:00:00.000")));
		assertFalse(aggr1.add(newTime("13:04:59.000")));
		assertTrue(aggr1.add(newTime("13:05:00.000")));
		assertEquals(newCandle("13:00:00.000", 2d, 2d, 2d, 2d, 5), aggr1.get());
	}
	
	@Test
	public void testAdd_Trade() throws Exception {
		final class FR {
			final Trade trade;
			final boolean expected;
			FR(Trade trade, boolean expected) {
				this.trade = trade;
				this.expected = expected;
			}
		}
		FR fix[] = {
			new FR(newTrade("13:00:00.000", 2d, 5), false),
			new FR(newTrade("13:00:10.000", 4d, 0), false),
			new FR(newTrade("13:04:15.000", 1d, 1), false),
			new FR(newTrade("13:04:59.000", 3d, 3), false),
	
			new FR(newTrade("13:05:00.000", 8d, 1), true),
			new FR(newTrade("13:08:15.000", 2d, 1), false),
	
			new FR(newTrade("14:01:24.000", 1d, 1), true),
			
			new FR(newTrade("14:09:15.000", 5d, 2), true),
			new FR(newTrade("14:09:28.000", 3d, 1), false),
			new FR(newTrade("14:09:59.000", 1d, 1), false),
			
			new FR(newTrade("15:00:00.000", 0d, 0), true),
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At#" + i;
			assertEquals(msg, fix[i].expected, aggr1.add(fix[i].trade));
			assertEquals(msg, fix[i].trade.getTime(),aggr1.getActualityPoint());
		}
		Candle expected[] = {
			newCandle("13:00:00.000", 2d, 4d, 1d, 3d, 9),
			newCandle("13:05:00.000", 8d, 8d, 2d, 2d, 2),
			newCandle("14:00:00.000", 1d, 1d, 1d, 1d, 1),
			newCandle("14:05:00.000", 5d, 5d, 1d, 1d, 4),
		};
		assertEquals(expected.length, aggr1.getLength());
		for ( int i = 0; i < expected.length; i ++ ) {
			String msg = "At#" + i;
			assertEquals(msg, expected[i], aggr1.get(i));
		}
	}
	
	@Test (expected=UnsupportedOperationException.class)
	public void testAdd_Candle() throws Exception {
		aggr1.add(newCandle("13:00:00.000", 8d, 4d, 2d, 3d, 1));
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(aggr1.equals(aggr1));
		assertFalse(aggr1.equals(null));
		assertFalse(aggr1.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<EditableCandleSeries> vSrs = new Variant<EditableCandleSeries>()
			.add(candles)
			.add(control.createMock(EditableCandleSeries.class));
		Variant<AlignTime> vAlgn = new Variant<AlignTime>(vSrs)
			.add(aligner)
			.add(control.createMock(AlignTime.class));
		Variant<?> iterator = vAlgn;
		int foundCnt = 0;
		CandleAggregator x, found = null;
		do {
			x = new CandleAggregator(vSrs.get(), vAlgn.get());
			if ( aggr2.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(candles, found.getCandles());
		assertSame(aligner, found.getTimeAligner());
	}
	
	@Test
	public void testAdd_Mixed() throws Exception {
		final class FR {
			final Object object;
			final boolean expected;
			final Date ap;
			FR(Object object, boolean expected, String expectedAP)
				throws ParseException
			{
				this.object = object;
				this.expected = expected;
				this.ap = newTime(expectedAP);
			}
		}
		FR fix[] = {
			new FR(newTrade("13:00:00.000", 2d, 5),	false,	"13:00:00.000"),
			new FR(newTick("13:00:10.000",	4d, 0),	false,	"13:00:10.000"),
			new FR(newTrade("13:04:15.000", 1d, 1),	false,	"13:04:15.000"),
			new FR(newTrade("13:04:59.000", 3d, 3),	false,	"13:04:59.000"),
			
			new FR(newTime("13:05:00.001"),			true,	"13:05:00.001"),
			// should skip following data
			new FR(newTick("13:04:59.050",	6d, 3),	false,	"13:05:00.001"),
			// should skip following data
			new FR(newTrade("13:04:59.070",	1d,	2),	false,	"13:05:00.001"),
			new FR(newTick("13:05:01.000",	2d, 4),	false,	"13:05:01.000"),
			new FR(newTrade("13:05:16.000",	2d,	2),	false,	"13:05:16.000"),
			
			new FR(newTime("13:12:14.001"),			true,	"13:12:14.001"),
			// should skip following data
			new FR(newTrade("13:07:24.019", 8d, 1), false,	"13:12:14.001"),
			new FR(newTick("13:13:08.000",	1d, 1),	false,	"13:13:08.000"),
			
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At#" + i;
			Object o = fix[i].object;
			boolean result = fix[i].expected;
			if ( o instanceof Date ) {
				assertEquals(msg, result, aggr1.add((Date) o));
			} else if ( o instanceof Trade ) {
				assertEquals(msg, result, aggr1.add((Trade) o));
			} else if ( o instanceof Tick ) {
				assertEquals(msg, result, aggr1.add((Tick) o));
			} else {
				fail("Unsupported data type: " + o);
			}
			assertEquals(msg, fix[i].ap, aggr1.getActualityPoint());
		}
		Candle expected[] = {
			newCandle("13:00:00.000", 2d, 4d, 1d, 3d, 9),
			newCandle("13:05:00.000", 2d, 2d, 2d, 2d, 6),
		};
		assertEquals(expected.length, aggr1.getLength());
		for ( int i = 0; i < expected.length; i ++ ) {
			String msg = "At#" + i;
			assertEquals(msg, expected[i], aggr1.get(i));
		}
	}

}
