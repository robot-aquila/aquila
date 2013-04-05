package ru.prolib.aquila.ta.ds;


import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.ta.TestValue;
import ru.prolib.aquila.ta.Value;
import ru.prolib.aquila.ta.ValueException;
import ru.prolib.aquila.ta.ValueList;
import ru.prolib.aquila.ta.ValueListImpl;
import ru.prolib.aquila.ta.indicator.BollingerBands;
import ru.prolib.aquila.ta.math.Alligator;
import ru.prolib.aquila.ta.math.BollingerBand;
import ru.prolib.aquila.ta.math.Cross;
import ru.prolib.aquila.ta.math.Ema;
import ru.prolib.aquila.ta.math.Max;
import ru.prolib.aquila.ta.math.Median;
import ru.prolib.aquila.ta.math.Min;
import ru.prolib.aquila.ta.math.QuikSmma;
import ru.prolib.aquila.ta.math.Shift;
import ru.prolib.aquila.ta.math.Sma;
import ru.prolib.aquila.ta.math.Smma;
import ru.prolib.aquila.ta.math.Stdev;
import ru.prolib.aquila.ta.math.Sub;
import ru.prolib.aquila.ta.math.WilliamsZones;

public class MarketDataCommonTest {
	IMocksControl control;
	ValueList values;
	MarketDataCommonT md,mdr;
	
	static class MarketDataCommonT extends MarketDataCommon {
		public MarketDataCommonT(ValueList values) {
			super(values);
		}
		@Override public int getLevel() { return 0; }
		@Override public MarketData getSource()
			throws MarketDataException { return null; }
		@Override public void prepare() throws ValueException { }
		@Override public void update() throws ValueException { }
	}
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		values = control.createMock(ValueList.class);
		md  = new MarketDataCommonT(values);
		mdr = new MarketDataCommonT(new ValueListImpl());
	}
	
	@Test
	public void testGetBar0() throws Exception {
		Date time = new Date();
		Candle expected = new Candle(0, time, 100d, 120d, 90d, 110d, 10);
		mdr.addValue(new TestValue<Date>(MarketData.TIME, (Date)time));
		mdr.addValue(new TestValue<Double>(MarketData.OPEN, 100d));
		mdr.addValue(new TestValue<Double>(MarketData.HIGH, 120d));
		mdr.addValue(new TestValue<Double>(MarketData.LOW, 90d));
		mdr.addValue(new TestValue<Double>(MarketData.CLOSE, 110d));
		mdr.addValue(new TestValue<Double>(MarketData.VOL, 10d));
		
		Candle actual = mdr.getBar();
		assertEquals(expected, actual);
		assertEquals(0, actual.getId());
	}

	@Test
	public void testGetBar1() throws Exception {
		TestValue<Date>   t = new TestValue<Date>(MarketData.TIME);
		TestValue<Double> o = new TestValue<Double>(MarketData.OPEN);
		TestValue<Double> h = new TestValue<Double>(MarketData.HIGH);
		TestValue<Double> l = new TestValue<Double>(MarketData.LOW);
		TestValue<Double> c = new TestValue<Double>(MarketData.CLOSE);
		TestValue<Double> v = new TestValue<Double>(MarketData.VOL);
		mdr.addValue(t);
		mdr.addValue(o);
		mdr.addValue(h);
		mdr.addValue(l);
		mdr.addValue(c);
		mdr.addValue(v);
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 0);
	
		// bar0
		cal.set(2009, 1, 28, 12, 15, 35);
		t.addToStackAndUpdate(cal.getTime());
		o.addToStackAndUpdate(100d);
		h.addToStackAndUpdate(120d);
		l.addToStackAndUpdate(90d);
		c.addToStackAndUpdate(110d);
		v.addToStackAndUpdate(10d);
		Candle expected0 = new Candle(0, cal.getTime(), 100d, 120d, 90d, 110d, 10);
		
		// bar1
		cal.set(2010, 12, 31, 23, 59, 59);
		t.addToStackAndUpdate(cal.getTime());
		o.addToStackAndUpdate(150d);
		h.addToStackAndUpdate(155d);
		l.addToStackAndUpdate(115d);
		c.addToStackAndUpdate(125d);
		v.addToStackAndUpdate(100d);
		Candle expected1 = new Candle(1, cal.getTime(), 150d, 155d, 115d, 125d, 100);
		
		// bar2
		cal.set(2011, 5, 12, 15, 30, 15);
		t.addToStackAndUpdate(cal.getTime());
		o.addToStackAndUpdate(210d);
		h.addToStackAndUpdate(230d);
		l.addToStackAndUpdate(205d);
		c.addToStackAndUpdate(220d);
		v.addToStackAndUpdate(1000d);
		Candle expected2 = new Candle(2, cal.getTime(), 210d, 230d, 205d, 220d, 1000);
		
		Candle actual;
		assertEquals(expected0, actual = mdr.getBar(0));
		assertEquals(0, actual.getId());
		assertEquals(expected0, actual = mdr.getBar(-2));
		assertEquals(0, actual.getId());
		
		assertEquals(expected1, actual = mdr.getBar(1));
		assertEquals(1, actual.getId());
		assertEquals(expected1, actual = mdr.getBar(-1));
		assertEquals(1, actual.getId());
		
		assertEquals(expected2, actual = mdr.getBar(2));
		assertEquals(2, actual.getId());
		assertEquals(expected2, actual = mdr.getBar());
		assertEquals(2, actual.getId());
	}
	
	@Test
	public void testGetOpen() throws Exception {
		TestValue<Double> val = new TestValue<Double>();
		expect(values.getValue(MarketData.OPEN)).andReturn(val);
		control.replay();
		
		assertSame(val, md.getOpen());
		
		control.verify();
	}

	@Test
	public void testGetClose() throws Exception {
		TestValue<Double> val = new TestValue<Double>();
		expect(values.getValue(MarketData.CLOSE)).andReturn(val);
		control.replay();
		
		assertSame(val, md.getClose());
		
		control.verify();
	}
	
	@Test
	public void testGetHigh() throws Exception {
		TestValue<Double> val = new TestValue<Double>();
		expect(values.getValue(MarketData.HIGH)).andReturn(val);
		control.replay();
		
		assertSame(val, md.getHigh());
		
		control.verify();
	}
	
	@Test
	public void testGetLow() throws Exception {
		TestValue<Double> val = new TestValue<Double>();
		expect(values.getValue(MarketData.LOW)).andReturn(val);
		control.replay();
		
		assertSame(val, md.getLow());
		
		control.verify();
	}

	@Test
	public void testGetVolume() throws Exception {
		TestValue<Double> val = new TestValue<Double>();
		expect(values.getValue(MarketData.VOL)).andReturn(val);
		control.replay();
		
		assertSame(val, md.getVolume());
		
		control.verify();
	}

	@Test
	public void testGetTime() throws Exception {
		TestValue<Date> val = new TestValue<Date>();
		expect(values.getValue(MarketData.TIME)).andReturn(val);
		control.replay();
		
		assertSame(val, md.getTime());
		
		control.verify();
	}

	@Test
	public void testGetMedian() throws Exception {
		Median val = new Median(new TestValue<Double>(),
							    new TestValue<Double>());
		expect(values.getValue(MarketData.MEDIAN)).andReturn(val);
		control.replay();
		
		assertSame(val, md.getMedian());
		
		control.verify();
	}
	
	@Test
	public void testAddValue() throws Exception {
		TestValue<Double> val = new TestValue<Double>();
		values.addValue(val);
		control.replay();
		
		md.addValue(val);
		
		control.verify();
	}
	
	@Test
	public void testGetValue() throws Exception {
		TestValue<Double> val = new TestValue<Double>();
		expect(values.getValue("foobar")).andReturn(val);
		control.replay();
		
		assertSame(val, md.getValue("foobar"));
		
		control.verify();
	}

	@Test
	public void testAddCross() throws Exception {
		Value<Double> src1 = new TestValue<Double>("foo");
		Value<Double> src2 = new TestValue<Double>("bar");
		mdr.addValue(src1);
		mdr.addValue(src2);
		Cross cross = mdr.addCross("foo", "bar", "cross");
		assertSame(src1, cross.getSource1());
		assertSame(src2, cross.getSource2());
		assertEquals("cross", cross.getId());
		assertSame(cross, mdr.getValue("cross"));
	}

	@Test
	public void testAddEma() throws Exception {
		Value<Double> src = new TestValue<Double>("foo");
		mdr.addValue(src);
		Ema ema = mdr.addEma("foo", 5, "bar");
		assertEquals(5, ema.getPeriods());
		assertSame(src, ema.getSourceValue());
		assertEquals("bar", ema.getId());
		assertSame(ema,mdr.getValue("bar"));
	}
	
	@Test
	public void testAddSma() throws Exception {
		Value<Double> src = new TestValue<Double>("zulu");
		mdr.addValue(src);
		Sma sma = mdr.addSma("zulu", 10, "foobar");
		assertEquals(10, sma.getPeriods());
		assertSame(src, sma.getSourceValue());
		assertEquals("foobar", sma.getId());
		assertSame(sma, mdr.getValue("foobar"));
	}

	@Test
	public void testAddSmma() throws Exception {
		Value<Double> src = new TestValue<Double>("aloha");
		mdr.addValue(src);
		Smma ma = mdr.addSmma("aloha", 10, "cubana");
		assertEquals(10, ma.getPeriods());
		assertSame(src, ma.getSourceValue());
		assertEquals("cubana", ma.getId());
		assertSame(ma, mdr.getValue("cubana"));
	}

	@Test
	public void testAddQuikSmma() throws Exception {
		Value<Double> src = new TestValue<Double>("gaha");
		mdr.addValue(src);
		QuikSmma ma = mdr.addQuikSmma("gaha", 10, "boha");
		assertEquals(10, ma.getPeriods());
		assertSame(src, ma.getSourceValue());
		assertEquals("boha", ma.getId());
		assertSame(ma, mdr.getValue("boha"));
	}

	@Test
	public void testAddMedian() throws Exception {
		Value<Double> hi = new TestValue<Double>("hi");
		Value<Double> lo = new TestValue<Double>("lo");
		mdr.addValue(hi);
		mdr.addValue(lo);
		Median v = mdr.addMedian("hi", "lo", "med");
		assertSame(hi, v.getSourceValue1());
		assertSame(lo, v.getSourceValue2());
		assertEquals("med", v.getId());
		assertSame(v, mdr.getValue("med"));
	}
	
	@Test
	public void testAddMax() throws Exception {
		Value<Double> src = new TestValue<Double>("hi");
		mdr.addValue(src);
		Max v = mdr.addMax("hi", 15, "hi.max15");
		assertSame(src, v.getSourceValue());
		assertEquals(15, v.getPeriods());
		assertEquals("hi.max15", v.getId());
	}
	
	@Test
	public void testAddMin() throws Exception {
		Value<Double> src = new TestValue<Double>("lo");
		mdr.addValue(src);
		Min v = mdr.addMin("lo", 10, "lo.min10");
		assertSame(src, v.getSourceValue());
		assertEquals(10, v.getPeriods());
		assertEquals("lo.min10", v.getId());
	}

	@Test
	public void testAddShift() throws Exception {
		Value<Double> src = new TestValue<Double>("val");
		mdr.addValue(src);
		Shift<Double> v = mdr.addShift("val", 10, "shift");
		assertEquals(10, v.getPeriods());
		assertSame(src, v.getSourceValue());
		assertEquals("shift", v.getId());
		assertSame(v, mdr.getValue("shift"));
	}

	@Test
	public void testAddSub() throws Exception {
		Value<Double> one = new TestValue<Double>("1");
		Value<Double> two = new TestValue<Double>("2");
		mdr.addValue(one);
		mdr.addValue(two);
		Sub v = mdr.addSub("1", "2", "sub");
		assertSame(one, v.getSourceValue1());
		assertSame(two, v.getSourceValue2());
		assertEquals("sub", v.getId());
		assertSame(v, mdr.getValue("sub"));
	}

	@Test
	public void testAddWilliamsZones() throws Exception {
		Value<Double> ao = new TestValue<Double>("ao");
		Value<Double> ac = new TestValue<Double>("ac");
		mdr.addValue(ao);
		mdr.addValue(ac);
		WilliamsZones v = mdr.addWilliamsZones("ao", "ac", "wz");
		assertSame(ao, v.getOscillator1());
		assertSame(ac, v.getOscillator2());
		assertEquals("wz", v.getId());
		assertSame(v, mdr.getValue("wz"));
	}

	@Test
	public void testAddAwesomeOscillator() throws Exception {
		// RIZ 15min 2011-10-28
		Double fixture[][] = {
			// hi, lo, expected
			{162495.00000,160300.00000, null},
			{160825.00000,159450.00000, null},
			{159795.00000,159300.00000, null},
			{160045.00000,159075.00000, null},
			{159730.00000,158805.00000, null},
			{159600.00000,158725.00000, null},
			{160560.00000,159345.00000, null},
			{161170.00000,160405.00000, null},
			{162445.00000,161075.00000, null},
			{162820.00000,161850.00000, null},
			{162920.00000,161640.00000, null},
			{162390.00000,161575.00000, null},
			{162485.00000,161375.00000, null},
			{161735.00000,160725.00000, null},
			{161265.00000,160450.00000, null},
			{161450.00000,160860.00000, null},
			{160970.00000,159930.00000, null},
			{160440.00000,159255.00000, null},
			{159915.00000,159075.00000, null},
			{159775.00000,158875.00000, null},
			{160485.00000,159665.00000, null},
			{160235.00000,158610.00000, null},
			{159175.00000,158005.00000, null},
			{159115.00000,158350.00000, null},
			{159445.00000,158625.00000, null},
			{159615.00000,158720.00000, null},
			{159500.00000,158690.00000, null},
			{159400.00000,158655.00000, null},
			{159165.00000,157715.00000, null},
			{158600.00000,157655.00000, null},
			{159450.00000,157860.00000, null},
			{159280.00000,158000.00000, null},
			{159270.00000,158320.00000, null},
			{159865.00000,158500.00000, null},
			{160895.00000,159310.00000, -811.7647},
			{161615.00000,160670.00000, -343.8235}, 
			{161280.00000,160440.00000,   61.5735},
			{161100.00000,160155.00000,  396.6764},
			{160400.00000,160030.00000,  575.3088},
			{160810.00000,160100.00000,  607.7941},
			{160660.00000,159880.00000,  423.9558},
			{160475.00000,159960.00000,  312.2205},
			{160710.00000,160270.00000,  322.0735},
			{160930.00000,160460.00000,  466.3088},
			{161095.00000,160510.00000,  579.2647},
			{161045.00000,160505.00000,  715.7794},
			{160750.00000,160410.00000,  827.9852},
			{160740.00000,160380.00000,  861.6911},
			{160750.00000,160375.00000,  843.8676},
			{160755.00000,160495.00000,  823.9558},
			{160875.00000,160420.00000,  792.6470},
			{160865.00000,160525.00000,  790.7205},
			{160775.00000,160575.00000,  779.0147},
			{160920.00000,160535.00000,  770.7647},
			{160935.00000,160760.00000,  792.5441},
		};
		
		TestValue<Double> high = new TestValue<Double>(MarketData.HIGH);
		TestValue<Double> low = new TestValue<Double>(MarketData.LOW);
		mdr.addValue(high);
		mdr.addValue(low);
		Value<Double> median =
			mdr.addMedian(MarketData.HIGH, MarketData.LOW, MarketData.MEDIAN);
		
		Sub ao = mdr.addAwesomeOscillator("ao");
		for ( int i = 0; i < fixture.length; i ++ ) {
			high.addToStackAndUpdate(fixture[i][0]);
			low.addToStackAndUpdate(fixture[i][1]);
			median.update();
			ao.getSourceValue1().update();
			ao.getSourceValue2().update();
			ao.update();
			
			if ( fixture[i][2] != null ) {
				assertEquals("At sequence #"+i,
						(double)fixture[i][2], (double)ao.get(), 0.0001d);
			}
		}
		assertEquals("ao", ao.getId());
		assertSame(ao, mdr.getValue("ao"));
		
		assertSame(ao.getSourceValue1(), mdr.getValue("ao.ma5"));
		assertSame(ao.getSourceValue2(), mdr.getValue("ao.ma34"));
	}
	
	@Test
	public void testAddAccelerationOscillator() throws Exception {
		TestValue<Double> ao = new TestValue<Double>("ao");
		mdr.addValue(ao);
		
		Sub ac = mdr.addAccelerationOscillator("ao", "ac");
		assertNotNull(ac);
		assertSame(ao, ac.getSourceValue1());
		Sma ma = (Sma) ac.getSourceValue2();
		assertSame(ao, ma.getSourceValue());
		assertEquals(5, ma.getPeriods());
		assertEquals("ac.ma5", ma.getId());
		assertEquals("ac", ac.getId());
		assertSame(ac, mdr.getValue("ac"));
		assertSame(ma, mdr.getValue("ac.ma5"));
	}

	@Test
	public void testAddAlligator() throws Exception {
		TestValue<Double> src = new TestValue<Double>("src");
		mdr.addValue(src);
		mdr.addMedian("src", "src", MarketData.MEDIAN);
		Alligator a = mdr.addAlligator("allig");
		Shift<Double> shift = null;
		QuikSmma ma = null;
		
		// lips
		shift = (Shift<Double>) a.lips;
		assertEquals(3, shift.getPeriods());
		assertEquals("allig.lips", shift.getId());
		assertSame(shift, mdr.getValue("allig.lips"));
		ma = (QuikSmma) shift.getSourceValue();
		assertEquals(5, ma.getPeriods());
		assertEquals("allig.ma5", ma.getId());
		assertSame(ma, mdr.getValue("allig.ma5"));
		assertSame(mdr.getMedian(), ma.getSourceValue());
		
		// teeths
		shift = (Shift<Double>) a.teeth;
		assertEquals(5, shift.getPeriods());
		assertEquals("allig.teeth", shift.getId());
		assertSame(shift, mdr.getValue("allig.teeth"));
		ma = (QuikSmma) shift.getSourceValue();
		assertEquals(8, ma.getPeriods());
		assertEquals("allig.ma8", ma.getId());
		assertSame(ma, mdr.getValue("allig.ma8"));
		assertSame(mdr.getMedian(), ma.getSourceValue());
		
		// jaw
		shift = (Shift<Double>) a.jaw;
		assertEquals(8, shift.getPeriods());
		assertEquals("allig.jaw", shift.getId());
		assertSame(shift, mdr.getValue("allig.jaw"));
		ma = (QuikSmma) shift.getSourceValue();
		assertEquals(13, ma.getPeriods());
		assertEquals("allig.ma13", ma.getId());
		assertSame(ma, mdr.getValue("allig.ma13"));
		assertSame(mdr.getMedian(), ma.getSourceValue());
	}
	
	@Test
	public void testAddBollingerBands() throws Exception {
		TestValue<Double> price = new TestValue<Double>("price");
		mdr.addValue(price);
		
		BollingerBands bb = mdr.addBollingerBands("price", 20, 2d, "bb");
		assertNotNull(bb);
		Sma central = (Sma)mdr.getValue("bb.central");
		Stdev stdev = (Stdev)mdr.getValue("bb.stdev");
		BollingerBand upper = (BollingerBand) mdr.getValue("bb.upper");
		BollingerBand lower = (BollingerBand) mdr.getValue("bb.lower");
		
		assertSame(price, stdev.getSourceValue());
		assertEquals(20, stdev.getPeriods());
		
		assertSame(central, bb.getCentralLine());
		assertSame(price, central.getSourceValue());
		assertEquals(20, central.getPeriods());
		
		assertSame(upper, bb.getUpperBand());
		assertSame(central, upper.getCentralLine());
		assertSame(stdev, upper.getStandardDeviation());
		assertEquals(2d, upper.getFactor(), 0.1d);
		
		assertSame(lower, bb.getLowerBand());
		assertSame(central, lower.getCentralLine());
		assertSame(stdev, lower.getStandardDeviation());
		assertEquals(-2d, lower.getFactor(), 0.1d);
	}

}
