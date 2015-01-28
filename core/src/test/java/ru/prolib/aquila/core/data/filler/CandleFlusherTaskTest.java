package ru.prolib.aquila.core.data.filler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.junit.*;
import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.data.EditableCandleSeries;
import ru.prolib.aquila.core.utils.Variant;

public class CandleFlusherTaskTest {
	private IMocksControl control;
	private EditableCandleSeries aggregator;
	private Scheduler timer;
	private CandleFlusherTask task;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		aggregator = control.createMock(EditableCandleSeries.class);
		timer = control.createMock(Scheduler.class);
		task = new CandleFlusherTask(aggregator, timer);
	}
	
	@Test
	public void testRun() throws Exception {
		DateTime time = new DateTime();
		expect(timer.getCurrentTime()).andReturn(time);
		aggregator.aggregate(same(time), eq(true));
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
		Variant<EditableCandleSeries> vAggr =
				new Variant<EditableCandleSeries>()
			.add(aggregator)
			.add(control.createMock(EditableCandleSeries.class));
		Variant<Scheduler> vTmr = new Variant<Scheduler>(vAggr)
			.add(timer)
			.add(control.createMock(Scheduler.class));
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
		assertSame(aggregator, found.getCandles());
		assertSame(timer, found.getTimer());
	}

}
