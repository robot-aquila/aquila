package ru.prolib.aquila.ta.ds;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.ta.TestValue;
import ru.prolib.aquila.ta.ValueException;

public class MarketDataGrouperTest {
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	IMocksControl control;
	MarketData srcDataMock;
	MarketDataImpl srcData;
	MarketDataGrouper grouper;
	Observer observer;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		observer = control.createMock(Observer.class);
		srcDataMock = control.createMock(MarketData.class);
		srcData = new MarketDataImpl(new MarketDataReaderFake(1));
		grouper = new MarketDataGrouper(srcData, 5);
	}
	
	private void prepareSrcData() throws Exception {
		Date d[] = {
				df.parse("2012-01-09 19:51:00"),
				df.parse("2012-01-09 19:52:14"),
				df.parse("2012-01-09 19:54:23"),
				df.parse("2012-01-09 20:01:15"),
				df.parse("2012-01-09 20:03:45"),
				df.parse("2012-01-09 28:01:00"),
		};
		Double o[] = { 10d, 15d, 13d, 11d, 10d, 18d };
		Double c[] = { 15d, 13d, 11d, 10d, 18d, 20d };
		Double h[] = { 18d, 16d, 14d, 12d, 18d, 21d };
		Double l[] = { 09d, 13d, 08d, 09d, 10d, 17d };
		Double v[] = {  1d,  5d,  1d,  4d,  1d,  1d };
		srcData.addValue(new TestValue<Date>(MarketData.TIME, d));
		srcData.addValue(new TestValue<Double>(MarketData.OPEN, o));
		srcData.addValue(new TestValue<Double>(MarketData.HIGH, h));
		srcData.addValue(new TestValue<Double>(MarketData.LOW, l));
		srcData.addValue(new TestValue<Double>(MarketData.CLOSE, c));
		srcData.addValue(new TestValue<Double>(MarketData.VOL, v));
	}
	
	@Test
	public void testInterface() throws Exception {
		assertTrue(grouper instanceof MarketDataCommon);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertEquals(1, srcData.countObservers());
		assertNotNull(grouper.getValue(MarketData.TIME));
		assertNotNull(grouper.getValue(MarketData.OPEN));
		assertNotNull(grouper.getValue(MarketData.HIGH));
		assertNotNull(grouper.getValue(MarketData.LOW));
		assertNotNull(grouper.getValue(MarketData.CLOSE));
		assertNotNull(grouper.getValue(MarketData.VOL));
		assertNotNull(grouper.getValue(MarketData.MEDIAN));
	}
	
	@Test
	public void testGetLevel() throws Exception {
		srcDataMock.addObserver(isA(MarketDataGrouper.class));
		expect(srcDataMock.getLevel()).andReturn(10);
		control.replay();
		
		grouper = new MarketDataGrouper(srcDataMock, 5);
		assertEquals(11, grouper.getLevel());
		
		control.verify();
	}
	
	@Test
	public void testGetSource() throws Exception {
		control.replay();
		
		assertSame(srcData, grouper.getSource());
		
		control.verify();
	}
	
	@Test
	public void testPrepare() throws Exception {
		srcDataMock.addObserver(isA(MarketDataGrouper.class));
		srcDataMock.prepare();
		control.replay();
		
		grouper = new MarketDataGrouper(srcDataMock, 5);
		grouper.prepare();
		
		control.verify();
	}
	
	@Test
	public void testUpdate0_BreakIfNoNewDataInSource() throws Exception {
		srcDataMock.addObserver(isA(MarketDataGrouper.class));
		expect(srcDataMock.getLength()).andReturn(0);
		srcDataMock.update();
		expect(srcDataMock.getLength()).andReturn(1); // новый бар в источнике
		srcDataMock.update();
		expect(srcDataMock.getLength()).andReturn(2); // новый бар -> дальше
		srcDataMock.update();
		expect(srcDataMock.getLength()).andReturn(2); // нету нового бара
		control.replay();
		
		grouper = new MarketDataGrouper(srcDataMock, 5);
		grouper.update();
		
		control.verify();
	}
	
	@Test
	public void testUpdate0_BreakIfNewBarAdded() throws Exception {
		prepareSrcData();
		grouper.addObserver(observer);
		observer.update(same(grouper), eq(null));
		expectLastCall().andDelegateTo(
		new CheckBar(df.parse("2012-01-09 19:50:00"), 10d, 18d, 08d, 11d, 7d));

		observer.update(same(grouper), eq(null));
		expectLastCall().andDelegateTo(
		new CheckBar(df.parse("2012-01-09 20:00:00"), 11d, 18d, 09d, 18d, 5d));
		
		control.replay();

		grouper.update();
		grouper.update();
		
		control.verify();
	}
	
	@Test
	public void testUpdate2_Ok() throws Exception {
		prepareSrcData();
		grouper.addObserver(observer);
		observer.update(same(grouper), eq(null));
		expectLastCall().andDelegateTo(
		new CheckBar(df.parse("2012-01-09 19:50:00"), 10d, 18d, 08d, 11d, 7d));
		
		observer.update(same(grouper), eq(null));
		expectLastCall().andDelegateTo(
		new CheckBar(df.parse("2012-01-09 20:00:00"), 11d, 18d, 09d, 18d, 5d));
		
		control.replay();
		
		srcData.update();
		srcData.update();
		srcData.update();
		srcData.update();
		srcData.update();
		srcData.update();
		
		control.verify();
	}
	
	public static class CheckBar implements Observer {
		private double o,h,l,c,v;
		private Date d;
		
		public CheckBar(Date d, double o, double h, double l,
							    double c, double v)
		{
			super();
			this.d = d;
			this.o = o;
			this.h = h;
			this.l = l;
			this.c = c;
			this.v = v;
		}

		@Override
		public void update(Observable observable, Object arg) {
			MarketData data = (MarketData)observable;
			try {
				assertEquals(d, data.getTime().get());
				assertEquals(o, data.getOpen().get(), 0.001d);
				assertEquals(h, data.getHigh().get(), 0.001d);
				assertEquals(l, data.getLow().get(),  0.001d);
				assertEquals(c, data.getClose().get(),0.001d);
				assertEquals(v, data.getVolume().get(), 0.001d);
			} catch ( ValueException e ) {
				fail("Unhandled exception: " + e);
			}
		}
		
	}

}
