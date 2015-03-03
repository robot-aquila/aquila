package ru.prolib.aquila.core.data.filler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.TimerTask;

import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.utils.Variant;

public class CandleFlusherTest {
	private static SimpleDateFormat df;
	private EventSystem es;
	private IMocksControl control;
	private EditableCandleSeries candles;
	private java.util.Timer scheduler;
	private Scheduler timer;
	private CandleFlusher flusher;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}

	@Before
	public void setUp() throws Exception {
		es = new EventSystemImpl();
		es.getEventQueue().start();
		control = createStrictControl();
		candles = new CandleSeriesImpl(es, Timeframe.M5);
		scheduler = control.createMock(java.util.Timer.class);
		timer = control.createMock(Scheduler.class);
		flusher = new CandleFlusher(candles, timer, scheduler);
		expect(timer.getCurrentTime())
			.andStubReturn(new DateTime(2008, 12, 8, 0, 1, 12));
	}
	
	@After
	public void tearDown() throws Exception {
		es.getEventQueue().stop();
	}
	
	@Test
	public void testStart() throws Exception {
		TimerTask expected = new CandleFlusherTask(candles, timer);
		scheduler.scheduleAtFixedRate(eq(expected),
				eq(df.parse("2008-12-08 00:05:00")), eq(300000L));
		control.replay();
		
		flusher.start();
		
		control.verify();
		assertEquals(expected, flusher.getTask());
	}
	
	@Test
	public void testStart_SkipStarted() throws Exception {
		TimerTask t = control.createMock(TimerTask.class);
		flusher.setTask(t);
		control.replay();
		
		flusher.start();
		
		control.verify();
		assertSame(t, flusher.getTask());
	}
	
	@Test
	public void testStop() throws Exception {
		TimerTask t = control.createMock(TimerTask.class);
		flusher.setTask(t);
		expect(t.cancel()).andReturn(true);
		control.replay();
		
		flusher.stop();
		
		control.verify();
		assertNull(flusher.getTask());
	}
	
	@Test
	public void testStop_SkipNotStarted() throws Exception {
		control.replay();
		
		flusher.stop();
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(flusher.equals(flusher));
		assertFalse(flusher.equals(null));
		assertFalse(flusher.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<EditableCandleSeries> vCndl =
				new Variant<EditableCandleSeries>()
			.add(candles)
			.add(new CandleSeriesImpl(es, Timeframe.M15));
		Variant<Scheduler> vTmr = new Variant<Scheduler>(vCndl)
			.add(timer)
			.add(new SchedulerLocal());
		Variant<?> iterator = vTmr;
		int foundCnt = 0;
		CandleFlusher x, found = null;
		do {
			x = new CandleFlusher(vCndl.get(), vTmr.get(), scheduler);
			if ( flusher.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(candles, found.getCandles());
		assertSame(scheduler, found.getScheduler());
		assertSame(timer, found.getTimeSource());
	}
	
	@Test
	public void testConstruct2() throws Exception {
		flusher = new CandleFlusher(candles, timer);
		java.util.Timer expected = flusher.getScheduler();
		assertNotNull(expected);
		assertSame(candles, flusher.getCandles());
		assertSame(timer, flusher.getTimeSource());
	}

}
