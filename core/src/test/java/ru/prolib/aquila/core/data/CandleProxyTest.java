package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import org.easymock.IMocksControl;
import org.joda.time.*;
import org.junit.*;

import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-04-26<br>
 * $Id: CandleProxyTest.java 565 2013-03-10 19:32:12Z whirlwind $
 */
public class CandleProxyTest {
	private static Interval interval1, interval2, interval3;
	private IMocksControl control;
	private EditableSeries<Candle> candles;
	private CandleProxy<Double> proxy;
	private GCandlePart<Double> getter;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Minutes period = Minutes.minutes(5);
		interval1 = new Interval(new DateTime(2013, 10, 6, 10, 0, 0), period);
		interval2 = new Interval(interval1.getEnd(), period);
		interval3 = new Interval(interval2.getEnd(), period);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		getter = new GCandleOpen();
		candles = new SeriesImpl<Candle>();
		candles.add(new Candle(interval1, 12.34d, 0d, 0d, 0d, 0l));
		candles.add(new Candle(interval2, 15.84d, 0d, 0d, 0d, 0l));
		candles.add(new Candle(interval3, 11.92d, 0d, 0d, 0d, 0l));
		proxy = new CandleProxy<Double>("foobar", candles, getter);
	}
	
	@Test
	public void testConstruct_Ok() throws Exception {
		assertEquals("foobar", proxy.getId());
		assertSame(candles, proxy.getCandles());
		assertSame(getter, proxy.getGetter());
	}
	
	@Test
	public void testGet0() throws Exception {
		assertEquals(11.92d, proxy.get(), 0.01d);
	}
	
	@Test
	public void testGet1() throws Exception {
		assertEquals(15.84d, proxy.get(-1), 0.01d);
		assertEquals(11.92d, proxy.get( 2), 0.01d);
	}
	
	@Test
	public void testGetLength() throws Exception {
		assertEquals(3, proxy.getLength());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(proxy.equals(proxy));
		assertFalse(proxy.equals(null));
		assertFalse(proxy.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<String> vId = new Variant<String>()
			.add("foobar")
			.add("kappa");
		Variant<Series<Candle>> vSrc = new Variant<Series<Candle>>(vId)
			.add(candles)
			.add(new SeriesImpl<Candle>());
		Variant<GCandlePart<Double>> vGtr =
				new Variant<GCandlePart<Double>>(vSrc)
			.add(getter)
			.add(new GCandleClose());
		Variant<?> iterator = vGtr;
		int foundCnt = 0;
		CandleProxy<Double> found = null, x = null;
		do {
			x = new CandleProxy<Double>(vId.get(), vSrc.get(), vGtr.get());
			if ( proxy.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("foobar", found.getId());
		assertSame(candles, found.getCandles());
		assertSame(getter, found.getGetter());
	}
	
	@Test
	public void testOnAdded() throws Exception {
		EventListener listener = control.createMock(EventListener.class);
		listener.onEvent(eq(new ValueEvent<Double>(proxy.OnAdded(), 19.12d, 3)));
		listener.onEvent(eq(new ValueEvent<Double>(proxy.OnAdded(), 19.85d, 4)));
		control.replay();
		
		proxy.OnAdded().addListener(listener);
		candles.add(new Candle(interval1, 19.12d, 0d, 0d, 0d, 0l));
		candles.add(new Candle(interval2, 19.85d, 0d, 0d, 0d, 0l));
		
		control.verify();
	}
	
	@Test
	public void testOnUpdated() throws Exception {
		EventListener listener = control.createMock(EventListener.class);
		listener.onEvent(new ValueEvent<Double>(proxy.OnUpdated(),11.92d,15.29d,2));
		listener.onEvent(new ValueEvent<Double>(proxy.OnUpdated(),15.29d,16.24d,2));
		
		control.replay();
		
		proxy.OnUpdated().addListener(listener);
		candles.set(new Candle(interval3, 15.29, 0d, 0d, 0d, 0l));
		candles.set(new Candle(interval3, 16.24, 0d, 0d, 0d, 0l));
		
		
		control.verify();
	}

}
