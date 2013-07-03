package ru.prolib.aquila.core.data.filler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Date;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.Timer;
import ru.prolib.aquila.core.utils.Variant;

public class CandleFlusherTaskTest {
	private IMocksControl control;
	private CandleAggregator aggregator;
	private Timer timer;
	private CandleFlusherTask task;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		aggregator = control.createMock(CandleAggregator.class);
		timer = control.createMock(Timer.class);
		task = new CandleFlusherTask(aggregator, timer);
	}
	
	@Test
	public void testRun() throws Exception {
		Date time = new Date();
		expect(timer.getCurrentTime()).andReturn(time);
		expect(aggregator.add(same(time))).andReturn(false);
		control.replay();
		
		task.run();
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(task.equals(task));
		assertFalse(task.equals(null));
		assertFalse(task.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<CandleAggregator> vAggr = new Variant<CandleAggregator>()
			.add(aggregator)
			.add(control.createMock(CandleAggregator.class));
		Variant<Timer> vTmr = new Variant<Timer>(vAggr)
			.add(timer)
			.add(control.createMock(Timer.class));
		Variant<?> iterator = vTmr;
		int foundCnt = 0;
		CandleFlusherTask x, found = null;
		do {
			x = new CandleFlusherTask(vAggr.get(), vTmr.get());
			if ( task.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(aggregator, found.getAggregator());
		assertSame(timer, found.getTimer());
	}

}
