package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.time.Instant;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class SPRunnableTaskHandlerTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private IMocksControl control;
	private Scheduler schedulerMock;
	private SPRunnable runnableMock;
	private SPRunnableTaskHandler handler;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		schedulerMock = control.createMock(Scheduler.class);
		runnableMock = control.createMock(SPRunnable.class);
		handler = new SPRunnableTaskHandler(schedulerMock, runnableMock);
	}
	
	@Test
	public void testRun_Cancelled() {
		handler.cancel();
		control.replay();
		
		handler.run();
		
		control.verify();
		assertTrue(handler.isCancelled());
	}
	
	@Test
	public void testRun() {
		runnableMock.run();
		expect(schedulerMock.getCurrentTime()).andReturn(T("2016-12-01T15:47:00Z"));
		expect(runnableMock.getNextExecutionTime(T("2016-12-01T15:47:00Z")))
			.andReturn(T("2016-12-01T15:47:01Z"));
		expect(schedulerMock.schedule(handler, T("2016-12-01T15:47:01Z"))).andReturn(null);
		control.replay();
		
		handler.run();
		
		control.verify();
		assertFalse(handler.isCancelled());
	}
	
	@Test
	public void testRun_Cancel() {
		runnableMock.run();
		expect(schedulerMock.getCurrentTime()).andReturn(T("2016-12-01T15:47:00Z"));
		expect(runnableMock.getNextExecutionTime(T("2016-12-01T15:47:00Z")))
			.andReturn(null);
		control.replay();
		
		handler.run();
		
		control.verify();
		assertTrue(handler.isCancelled());		
	}
	
	@Test
	public void testReschedule_Cancelled() {
		handler.cancel();
		control.replay();
		
		handler.reschedule();
		
		control.verify();
		assertTrue(handler.isCancelled());
	}
	
	@Test
	public void testReschedule_Cancel() {
		expect(schedulerMock.getCurrentTime()).andReturn(T("2016-12-01T15:47:00Z"));
		expect(runnableMock.getNextExecutionTime(T("2016-12-01T15:47:00Z")))
			.andReturn(null);
		control.replay();
		
		handler.reschedule();
		
		control.verify();
		assertTrue(handler.isCancelled());
	}
	
	@Test
	public void testReschedule() {
		expect(schedulerMock.getCurrentTime()).andReturn(T("2016-12-01T15:47:00Z"));
		expect(runnableMock.getNextExecutionTime(T("2016-12-01T15:47:00Z")))
			.andReturn(T("2016-12-01T15:47:01Z"));
		expect(schedulerMock.schedule(handler, T("2016-12-01T15:47:01Z"))).andReturn(null);
		control.replay();
		
		handler.reschedule();
		
		control.verify();
		assertFalse(handler.isCancelled());
	}

	@Test
	public void testEquals_SpecialCases() {
		assertTrue(handler.equals(handler));
		assertFalse(handler.equals(null));
		assertFalse(handler.equals(this));
	}
	
	@Test
	public void testEquals() {
		Variant<Scheduler> vScheduler = new Variant<Scheduler>()
				.add(schedulerMock)
				.add(control.createMock(Scheduler.class));
		Variant<SPRunnable> vRunnable = new Variant<SPRunnable>(vScheduler)
				.add(runnableMock)
				.add(control.createMock(SPRunnable.class));
		Variant<Boolean> vCancelled = new Variant<Boolean>(vRunnable)
				.add(false)
				.add(true);
		Variant<?> iterator = vCancelled;
		int foundCnt = 0;
		SPRunnableTaskHandler found = null, x;
		do {
			x = new SPRunnableTaskHandler(vScheduler.get(), vRunnable.get());
			if ( vCancelled.get() ) {
				x.cancel();
			}
			if ( handler.equals(x) ) {
				found = x;
				foundCnt ++;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(schedulerMock, found.getScheduler());
		assertSame(runnableMock, found.getRunnable());
		assertFalse(found.isCancelled());
	}
	
	@Test
	public void testToString() {
		String expected = "SPRunnableTaskHandler[" + runnableMock + "]";
		assertEquals(expected, handler.toString());
	}

}
