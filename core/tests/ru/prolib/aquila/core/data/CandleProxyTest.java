package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import java.util.Date;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-04-26<br>
 * $Id: CandleProxyTest.java 565 2013-03-10 19:32:12Z whirlwind $
 */
public class CandleProxyTest {
	private IMocksControl control;
	private EditableSeries<Candle> candles;
	private CandleProxy<Double> proxy;
	private G<Double> getter;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		getter = new GCandleOpen();
		candles = new SeriesImpl<Candle>();
		candles.add(new Candle(new Date(), 12.34d, 0d, 0d, 0d, 0l));
		candles.add(new Candle(new Date(), 15.84d, 0d, 0d, 0d, 0l));
		candles.add(new Candle(new Date(), 11.92d, 0d, 0d, 0d, 0l));
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
	
	@SuppressWarnings("unchecked")
	@Test
	public void testEquals() throws Exception {
		Variant<String> vId = new Variant<String>()
			.add("foobar")
			.add("kappa");
		Variant<Series<Candle>> vSrc = new Variant<Series<Candle>>(vId)
			.add(candles)
			.add(new SeriesImpl<Candle>());
		Variant<G<Double>> vGtr = new Variant<G<Double>>(vSrc)
			.add(getter)
			.add(control.createMock(G.class));
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
	public void testOnAdd() throws Exception {
		EventListener listener = control.createMock(EventListener.class);
		listener.onEvent(eq(new ValueEvent<Double>(proxy.OnAdd(), 19.12d, 3)));
		listener.onEvent(eq(new ValueEvent<Double>(proxy.OnAdd(), 19.85d, 4)));
		control.replay();
		
		proxy.OnAdd().addListener(listener);
		candles.add(new Candle(new Date(), 19.12d, 0d, 0d, 0d, 0l));
		candles.add(new Candle(new Date(), 19.85d, 0d, 0d, 0d, 0l));
		
		control.verify();
	}
	
	@Test
	public void testOnUpd() throws Exception {
		EventListener listener = control.createMock(EventListener.class);
		listener.onEvent(new ValueEvent<Double>(proxy.OnUpd(),12.34d,11.20d,0));
		listener.onEvent(new ValueEvent<Double>(proxy.OnUpd(),11.92d,15.29d,2));
		control.replay();
		
		proxy.OnUpd().addListener(listener);
		candles.set(new Candle(new Date(),11.20,0d,0d,0d,0l), 0);
		candles.set(new Candle(new Date(),15.29,0d,0d,0d,0l));
		
		control.verify();
	}

}
