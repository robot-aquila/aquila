package ru.prolib.aquila.data;

import static org.junit.Assert.*;
import java.util.*;
import org.junit.*;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.ta.*;
import ru.prolib.aquila.util.*;

/**
 * 2012-04-20
 * $Id: CandleTest.java 218 2012-05-20 12:15:33Z whirlwind $
 */
public class CandleTest {
	private Date date1 = new Date();
	private Date date2 = new Date();
	private Candle c1,c2;
	private Deal deal;
	
	@Before
	public void setUp() throws Exception {

	}
	
	@Test
	public void testConstruct7() throws Exception {
		c1 = new Candle(123, date1, 120.05d, 130.00d, 90.55d, 125.15d, 10000L);
		assertSame(date1, c1.getTime());
		assertEquals(120.05d, c1.getOpen(), 0.001d);
		assertEquals(130.00d, c1.getHigh(), 0.001d);
		assertEquals( 90.55d, c1.getLow(),  0.001d);
		assertEquals(125.15d, c1.getClose(),0.001d);
		assertEquals(10000L,  c1.getVolume());
		assertEquals(123, c1.getId());
	}
	
	@Test
	public void testConstruct6() throws Exception {
		c1 = new Candle(date1, 120.05d, 130.00d, 90.55d, 125.15d, 10000L);
		assertSame(date1, c1.getTime());
		assertEquals(120.05d, c1.getOpen(), 0.001d);
		assertEquals(130.00d, c1.getHigh(), 0.001d);
		assertEquals( 90.55d, c1.getLow(),  0.001d);
		assertEquals(125.15d, c1.getClose(),0.001d);
		assertEquals(10000L,  c1.getVolume());
		assertEquals(0, c1.getId());
	}
	
	@Test
	public void testConstruct1() throws Exception {
		c2 = new Candle(date1, 120.05d, 130.00d, 90.55d, 125.15d, 10000L);
		c1 = new Candle(c2);
		assertSame(date1, c1.getTime());
		assertEquals(120.05d, c1.getOpen(), 0.001d);
		assertEquals(130.00d, c1.getHigh(), 0.001d);
		assertEquals( 90.55d, c1.getLow(),  0.001d);
		assertEquals(125.15d, c1.getClose(),0.001d);
		assertEquals(10000L,  c1.getVolume());
		assertEquals(0, c1.getId());
	}
	
	@Test
	public void testConstruct2FromBar() throws Exception {
		c2 = new Candle(876, date1, 120.05d, 130.00d, 90.55d, 125.15d, 10000L);
		c1 = new Candle(111, c2);
		assertSame(date1, c1.getTime());
		assertEquals(120.05d, c1.getOpen(), 0.001d);
		assertEquals(130.00d, c1.getHigh(), 0.001d);
		assertEquals( 90.55d, c1.getLow(),  0.001d);
		assertEquals(125.15d, c1.getClose(),0.001d);
		assertEquals(10000L,  c1.getVolume());
		assertEquals(111, c1.getId());
	}
	
	@Test
	public void testConstruct2FromDeal() throws Exception {
		deal = new DealImpl(date1, 125.35d, 100L);
		c1 = new Candle(date2, deal);
		assertSame(date2, c1.getTime());
		assertEquals(125.35d, c1.getOpen(), 0.001d);
		assertEquals(125.35d, c1.getHigh(), 0.001d);
		assertEquals(125.35d, c1.getLow(), 0.001d);
		assertEquals(125.35d, c1.getClose(), 0.001d);
		assertEquals(0, c1.getId());
	}
	
	@Test
	public void testConstruct3() throws Exception {
		deal = new DealImpl(date1, 125.35d, 100L);
		c1 = new Candle(765, date2, deal);
		assertSame(date2, c1.getTime());
		assertEquals(125.35d, c1.getOpen(), 0.001d);
		assertEquals(125.35d, c1.getHigh(), 0.001d);
		assertEquals(125.35d, c1.getLow(), 0.001d);
		assertEquals(125.35d, c1.getClose(), 0.001d);
		assertEquals(765, c1.getId());
	}

	@Test
	public void testAddDeal1() throws Exception {
		deal = new DealImpl(date2, 85.35d, 100L);
		c1 = new Candle(date1, 120.05d, 130.00d, 90.55d, 125.15d, 10000L);
		c1.addDeal(deal);
		assertSame(date1, c1.getTime());
		assertEquals(120.05d, c1.getOpen(), 0.001d);
		assertEquals(130.00d, c1.getHigh(), 0.001d);
		assertEquals( 85.35d, c1.getLow(),  0.001d);
		assertEquals( 85.35d, c1.getClose(),0.001d);
		assertEquals(10100L,  c1.getVolume());
	}
	
	@Test
	public void testAddDeal2() throws Exception {
		c1 = new Candle(date1, 120.05d, 130.00d, 90.55d, 125.15d, 10000L);
		c1.addDeal(132.25d, 1L);
		assertSame(date1, c1.getTime());
		assertEquals(120.05d, c1.getOpen(), 0.001d);
		assertEquals(132.25d, c1.getHigh(), 0.001d);
		assertEquals( 90.55d, c1.getLow(),  0.001d);
		assertEquals(132.25d, c1.getClose(),0.001d);
		assertEquals(10001L,  c1.getVolume());
	}
	
	@Test
	public void testAddCandle() throws Exception {
		c1 = new Candle(date1, 120.05d, 130.00d, 90.55d, 125.15d, 10000L);
		c2 = new Candle(date2, 140.05d, 150.00d, 80.55d, 128.00d,   100L);
		c1.addCandle(c2);
		assertSame(date1, c1.getTime());
		assertEquals(120.05d, c1.getOpen(), 0.001d);
		assertEquals(150.00d, c1.getHigh(), 0.001d);
		assertEquals( 80.55d, c1.getLow(),  0.001d);
		assertEquals(128.00d, c1.getClose(),0.001d);
		assertEquals(10100L,  c1.getVolume());
	}
	
	@Test
	public void testEquals() throws Exception {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, 1997);
		Candle expected = new Candle(8, c.getTime(),
				100.0d, 250.0d, 90.0d, 235.0d, 20L);
		
		Variant<Long> volume = new Variant<Long>(new Long[] {10L, 20L});
		Variant<Double> open = new Variant<Double>(new Double[]{ 100.0d, 200.0d }, volume);
		Variant<Double> high = new Variant<Double>(new Double[]{ 110.0d, 250.0d }, open);
		Variant<Double> low = new Variant<Double>(new Double[]{  90.0d,  85.0d }, high);
		Variant<Double> close = new Variant<Double>(new Double[]{ 115.0d, 235.0d }, low);
		Variant<Date> time = new Variant<Date>(new Date[]{c.getTime(), date1, date2 }, close);
		Variant<Integer> id = new Variant<Integer>(new Integer[] {1, 2, 8}, time);
		Variant<?> root = id;
		Candle found = null;
		int numfound = 0;
		do {
			Candle current = new Candle(id.get(), time.get(), open.get(), high.get(),
								  low.get(), close.get(), volume.get());
			if ( current.equals(expected) ) {
				numfound ++;
				found = current;
			}
			
		} while ( root.next() );
		assertEquals(1, numfound);
		assertEquals(8, found.getId());
		assertEquals(c.getTime(), found.getTime());
		assertEquals(100.0d, found.getOpen(), 0.001d);
		assertEquals(250.0d, found.getHigh(), 0.001d);
		assertEquals( 90.0d, found.getLow(),  0.001d);
		assertEquals(235.0d, found.getClose(),0.001d);
		assertEquals(20L, found.getVolume());
		
		assertFalse(expected.equals(this));
	}
	
	@Test
	public void testEquals_IfDifferentClass() throws Exception {
		Candle bar = new Candle(8, date1, 100.0d, 250.0d, 90.0d, 235.0d, 20L);
		assertFalse(bar.equals(this));
	}
	
	@Test
	public void testEquals_IfNull() throws Exception {
		Candle bar = new Candle(8, date1, 100.0d, 250.0d, 90.0d, 235.0d, 20L);
		assertFalse(bar.equals(null));
	}

}
