package ru.prolib.aquila.probe.internal;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.easymock.IMocksControl;
import org.joda.time.*;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.TaskHandler;
import ru.prolib.aquila.core.BusinessEntities.TaskHandlerImpl;
import ru.prolib.aquila.probe.timeline.*;

public class SchedulerTest {
	private Interval interval;
	private IMocksControl control;
	private TLSTimeline timeline;
	private SchedulerImpl scheduler;
	private Runnable r1,r2;
	private DateTime someTime;
	private Finisher finisher;
	private CollectPOA collectPOA;

	/**
	 * Индикатор завершения эмуляции.
	 */
	static class Finisher {
		final CountDownLatch finished = new CountDownLatch(1);
		Finisher(SimulationController timeline) {
			new ListenOnce(timeline.OnFinish(), new EventListener() {
				@Override public void onEvent(Event arg0) {
					finished.countDown();
				}
			}).start();
		}
		
		boolean await(long ms) throws InterruptedException {
			return finished.await(ms, TimeUnit.MILLISECONDS);
		}
	}
	
	/**
	 * Тестовая задача - собирает значение ТА в момент срабатывания.
	 */
	static class CollectPOA implements Runnable {
		final Vector<DateTime> actual = new Vector<DateTime>();
		final TLSTimeline timeline;
		CollectPOA(TLSTimeline timeline) {
			this.timeline = timeline;
		}
		
		@Override public void run() {
			actual.add(timeline.getPOA());
		}
		
	}
	
	/**
	 * Кейс теста периодической задачи.
	 * <p>
	 * Задача {@link #collectPOA} должна быть запланирована начиная с
	 * {@link #someTime} с периодом повторения 10 минут.
	 * <p>
	 * @param hActual дескриптор добавленной задачи
	 * @throws Exception
	 */
	public void
		testSchedule_Repeated_StartSomeTime_10minPeriod(TaskHandler hActual)
			throws Exception
	{
		
		// interval= 2014-10-12 15:17:45.000 - 2014-10-12 20:10:10.000,
		// someTime= 2014-10-12 18:00:00.000
		long period = 1000 * 60 * 10; // 10 min
		Vector<DateTime> expected = new Vector<DateTime>();
		for ( int i = 0; i < 14; i ++ ) {
			expected.add(someTime.plus(period * i + 1));
		}
		
		TaskHandler hExpected = new TaskHandlerImpl(collectPOA, scheduler);
		
		assertEquals(hExpected, hActual);
		assertTrue(hActual.scheduled());
		
		timeline.run();
		
		assertTrue(finisher.await(200));
		assertEquals(expected, collectPOA.actual);
	}

	@Before
	public void setUp() throws Exception {
		// interval= 2014-10-12 15:17:45.000 - 2014-10-12 20:10:10.000,
		// someTime= 2014-10-12 18:00:00.000
		interval = new Interval(new DateTime(2014, 10, 12, 15, 17, 45, 0),
				new DateTime(2014, 10, 12, 20, 10, 10, 0));
		someTime = new DateTime(2014, 10, 12, 18, 0, 0, 0);
		control = createStrictControl();
		timeline = new TLSTimelineFactory(
				new EventSystemImpl(new SimpleEventQueue())).produce(interval);
		finisher = new Finisher(timeline);
		scheduler = new SchedulerImpl(timeline);
		r1 = control.createMock(Runnable.class);
		r2 = control.createMock(Runnable.class);
		collectPOA = new CollectPOA(timeline);
	}
	
	@Test
	public void testScheduled() throws Exception {
		scheduler.schedule(r1, someTime);
		
		assertTrue(scheduler.scheduled(r1));
		assertFalse(scheduler.scheduled(r2));
	}
	
	@Test (expected=NullPointerException.class)
	public void testScheduled_ThrowsIfNullTask() throws Exception {
		scheduler.scheduled(null);
	}
	
	@Test
	public void testCancel() throws Exception {
		scheduler.schedule(collectPOA, someTime);

		scheduler.cancel(collectPOA);
		assertFalse(scheduler.scheduled(collectPOA));
		
		timeline.run();
		
		finisher.await(200);
		assertEquals(0, collectPOA.actual.size());
	}
	
	@Test
	public void testGetTaskHandler() throws Exception {
		scheduler.schedule(r1, someTime);
		
		TaskHandler expected = new TaskHandlerImpl(r1, scheduler),
				actual = scheduler.getTaskHandler(r1);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetTaskHandler_NoTask() throws Exception {
		assertEquals(new TaskHandlerImpl(r1, scheduler),
				scheduler.getTaskHandler(r1));
	}
	
	@Test
	public void testSchedule_TD() throws Exception {
		Vector<DateTime> expected = new Vector<DateTime>();
		expected.add(someTime.plus(1));	// POA сместится на 1мс после
		   								// извлечения стека событий
		
		TaskHandler hExpected = new TaskHandlerImpl(collectPOA, scheduler),
				hActual = scheduler.schedule(collectPOA, someTime);
		
		assertEquals(hExpected, hActual);
		assertTrue(hActual.scheduled());
		
		timeline.run();
		
		assertTrue(finisher.await(200));
		assertEquals(expected, collectPOA.actual);
	}
	
	@Test (expected=NullPointerException.class)
	public void testSchedule_TD_ThrowsIfTaskIsNull() throws Exception {
		scheduler.schedule(null, someTime);
	}
	
	@Test (expected=NullPointerException.class)
	public void testSchedule_TD_ThrowsIfTimeIsNull() throws Exception {
		scheduler.schedule(r1, null);
	}
	
	@Test
	public void testSchedule_TD_IfAfterPeriodEnd() throws Exception {
		TaskHandler actual = scheduler.schedule(collectPOA, interval.getEnd());
		
		assertFalse(actual.scheduled());
		
		timeline.run();
		
		assertTrue(finisher.await(200));
		assertEquals(0, collectPOA.actual.size());
	}
	
	@Test
	public void testSchedule_TD_IfBeforePeriodStart() throws Exception {
		Vector<DateTime> expected = new Vector<DateTime>();
		expected.add(interval.getStart().plus(1)); // POA сместится на 1мс после
												   // извлечения стека событий
		scheduler.schedule(collectPOA, interval.getStart().minus(200));
		
		timeline.run();
		
		assertTrue(finisher.await(200));
		assertEquals(expected, collectPOA.actual);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testSchedule_TD_ThrowsIfDuplicateTask() throws Exception {
		scheduler.schedule(r1, someTime);
		scheduler.schedule(r1, someTime);
	}
	
	@Test
	public void testGetCurrentTime() throws Exception {
		assertEquals(interval.getStart(), scheduler.getCurrentTime());
		
		scheduler.schedule(collectPOA, someTime);
		
		timeline.run();
		
		assertTrue(finisher.await(200));
		assertEquals(someTime.plus(1), collectPOA.actual.get(0));
		assertEquals(interval.getEnd(), scheduler.getCurrentTime());
	}
	
	@Test
	public void testSchedule_TDL() throws Exception {
		long period = 1000 * 60 * 10;
		TaskHandler hActual = scheduler.schedule(collectPOA, someTime, period); 
		testSchedule_Repeated_StartSomeTime_10minPeriod(hActual);
	}
	
	@Test
	public void testSchedule_TDL_ForMinimalPeriod1Ms() throws Exception {
		Vector<DateTime> expected = new Vector<DateTime>();
		DateTime from = new DateTime(2014, 10, 12, 20, 10, 9, 990);
		for ( int i = 0; i < 10; i ++ ) {
			expected.add(from.plus(i + 1));
		}
		scheduler.schedule(collectPOA, from, 1);
		
		timeline.run();
		
		assertTrue(finisher.await(200));
		assertEquals(expected, collectPOA.actual);
	}
	
	@Test
	public void testSchedule_TDL_IfAfterPeriodEnd() throws Exception {
		TaskHandler actual =
				scheduler.schedule(collectPOA, interval.getEnd(), 1000);
		
		assertFalse(actual.scheduled());
		
		timeline.run();
		
		assertTrue(finisher.await(200));
		assertEquals(0, collectPOA.actual.size());
	}
	
	@Test
	public void testSchedule_TDL_IfBeforePeriodStart() throws Exception {
		// interval= 2014-10-12 15:17:45.000 - 2014-10-12 20:10:10.000,
		long period = 1000 * 60 * 60; // 60 min
		Vector<DateTime> expected = new Vector<DateTime>();
		for ( int i = 0; i < 5; i ++ ) {
			expected.add(interval.getStart().plus(period * i + 1));
		}
		
		scheduler.schedule(collectPOA, interval.getStart().minus(2501), period);
		
		timeline.run();
		
		assertTrue(finisher.await(200));
		assertEquals(expected, collectPOA.actual);
	}

	@Test (expected=IllegalStateException.class)
	public void testSchedule_TDL_ThrowsIfDuplicateTask() throws Exception {
		scheduler.schedule(collectPOA, someTime, 256);
		scheduler.schedule(collectPOA, someTime, 386);
	}
	
	@Test (expected=NullPointerException.class)
	public void testSchedule_TDL_ThrowsIfTaskIsNull() throws Exception {
		scheduler.schedule(null, someTime, 1000);
	}

	@Test (expected=NullPointerException.class)
	public void testSchedule_TDL_ThrowsIfTimeIsNull() throws Exception {
		scheduler.schedule(collectPOA, null, 1000);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSchedule_TDL_ThrowsIfPeriodEqZero() throws Exception {
		scheduler.schedule(collectPOA, someTime, 0);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSchedule_TDL_ThrowsIfPeriodLessThanZero() throws Exception {
		scheduler.schedule(collectPOA, someTime, -1);
	}
	
	@Test
	public void testSchedule_TL() throws Exception {
		Vector<DateTime> expected = new Vector<DateTime>();
		expected.add(interval.getStart().plus(2000 + 1));
		TaskHandler hExpected = new TaskHandlerImpl(collectPOA, scheduler),
				hActual = scheduler.schedule(collectPOA, 2000);
		
		assertEquals(hExpected, hActual);
		assertTrue(hActual.scheduled());
		
		timeline.run();
		
		assertTrue(finisher.await(200));
		assertEquals(expected, collectPOA.actual);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testSchedule_TL_ThrowsIfDuplicateTask() throws Exception {
		scheduler.schedule(collectPOA, 100);
		scheduler.schedule(collectPOA, 100);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSchedule_TL_ThrowsIfDelayLessThanZero() throws Exception {
		scheduler.schedule(collectPOA, -100);
	}
	
	@Test (expected=NullPointerException.class)
	public void testSchedule_TL_ThrowsIfTaskIsNull() throws Exception {
		scheduler.schedule(null, 200);
	}
	
	@Test
	public void testSchedule_TLL() throws Exception {
		long delay = someTime.getMillis() - interval.getStartMillis();
		long period = 1000 * 60 * 10;
		TaskHandler hActual = scheduler.schedule(collectPOA, delay, period); 
		testSchedule_Repeated_StartSomeTime_10minPeriod(hActual);
	}
	
	@Test
	public void testSchedule_TLL_IfAfterPeriodEnd() throws Exception {
		TaskHandler actual = scheduler.schedule(collectPOA,
				interval.toDurationMillis(), 1000);
		
		assertFalse(actual.scheduled());
		
		timeline.run();
		
		assertTrue(finisher.await(200));
		assertEquals(0, collectPOA.actual.size());
	}
	
	@Test (expected=IllegalStateException.class)
	public void testSchedule_TLL_ThrowsIfDuplicateTask() throws Exception {
		scheduler.schedule(collectPOA, 0, 2000);
		scheduler.schedule(collectPOA, 100, 400);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSchedule_TLL_ThrowsIfDelayLessThanZero() throws Exception {
		scheduler.schedule(collectPOA, -150, 2000);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSchedule_TLL_ThrowsIfPeriodEqZero() throws Exception {
		scheduler.schedule(collectPOA, 150, 0);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSchedule_TLL_ThrowsIfPeriodLessThanZero() throws Exception {
		scheduler.schedule(collectPOA, 150, -1024);
	}
	
	@Test (expected=NullPointerException.class)
	public void testSchedule_TLL_ThrowsIfTaskIsNull() throws Exception {
		scheduler.schedule(null, 1500, 2000);
	}
	
	@Test
	public void testScheduleAtFixedRate_TDL() throws Exception {
		long period = 1000 * 60 * 10;
		TaskHandler hActual =
				scheduler.scheduleAtFixedRate(collectPOA, someTime, period); 
		testSchedule_Repeated_StartSomeTime_10minPeriod(hActual);
	}
	
	@Test
	public void testScheduleAtFixedRate_TDL_IsAfterPeriodEnd()
			throws Exception
	{
		TaskHandler actual = scheduler.scheduleAtFixedRate(collectPOA,
				interval.getEnd(), 1000);
		
		assertFalse(actual.scheduled());
		
		timeline.run();
		
		assertTrue(finisher.await(200));
		assertEquals(0, collectPOA.actual.size());
	}
	
	@Test
	public void testScheduleAtFixedRate_TDL_IsBeforePeriodStart()
			throws Exception
	{
		long period = 1000 * 60 * 60; // 60 min
		Vector<DateTime> expected = new Vector<DateTime>();
		for ( int i = 0; i < 5; i ++ ) {
			expected.add(interval.getStart().plus(period * i + 1));
		}
		
		scheduler.scheduleAtFixedRate(collectPOA,
				interval.getStart().minus(100500), period);
		
		timeline.run();
		
		assertTrue(finisher.await(200));
		assertEquals(expected, collectPOA.actual);
	}

	@Test (expected=IllegalStateException.class)
	public void testScheduleAtFixedRate_TDL_ThrowsIfDuplicateTask()
			throws Exception
	{
		scheduler.scheduleAtFixedRate(collectPOA, someTime, 1000);
		scheduler.scheduleAtFixedRate(collectPOA, someTime, 1000);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testScheduleAtFixedRate_TDL_ThrowsIfPeriodEqZero()
			throws Exception
	{
		scheduler.scheduleAtFixedRate(collectPOA, someTime, 0);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testScheduleAtFixedRate_TDL_ThrowsIfPeriodLessThanZero()
			throws Exception
	{
		scheduler.scheduleAtFixedRate(collectPOA, someTime, -1);
	}
	
	@Test (expected=NullPointerException.class)
	public void testScheduleAtFixedRate_TDL_ThrowsIfTaskIsNull()
			throws Exception
	{
		scheduler.scheduleAtFixedRate(null, someTime, 1000);
	}
	
	@Test (expected=NullPointerException.class)
	public void testScheduleAtFixedRate_TDL_ThrowsIfTimeIsNull()
			throws Exception
	{
		scheduler.scheduleAtFixedRate(collectPOA, null, 1000);
	}

	@Test
	public void testScheduleAtFixedRate_TLL() throws Exception {
		long delay = someTime.getMillis() - interval.getStartMillis();
		long period = 1000 * 60 * 10;
		TaskHandler hActual =
				scheduler.scheduleAtFixedRate(collectPOA, delay, period); 
		testSchedule_Repeated_StartSomeTime_10minPeriod(hActual);
	}
	
	@Test
	public void testScheduleAtFixedRate_TLL_IfAfterPeriodEnd()
			throws Exception
	{
		TaskHandler actual = scheduler.scheduleAtFixedRate(collectPOA,
				interval.toDurationMillis(), 1000);
		
		assertFalse(actual.scheduled());
		
		timeline.run();
		
		assertTrue(finisher.await(200));
		assertEquals(0, collectPOA.actual.size());
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testScheduleAtFixedRate_TLL_ThrowsIfDelayLessThanZero()
			throws Exception
	{
		scheduler.scheduleAtFixedRate(collectPOA, -1, 1000);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testScheduleAtFixedRate_TLL_ThrowsIfDuplicateTask()
			throws Exception
	{
		scheduler.scheduleAtFixedRate(collectPOA, 0, 1000);
		scheduler.scheduleAtFixedRate(collectPOA, 0, 1000);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testScheduleAtFixedRate_TLL_ThrowsIfPeriodEqZero()
			throws Exception
	{
		scheduler.scheduleAtFixedRate(collectPOA, 0, 0);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testScheduleAtFixedRate_TLL_ThrowsIfPeriodLessThanZero()
			throws Exception
	{
		scheduler.scheduleAtFixedRate(collectPOA, 0, -100);
	}
	
	@Test (expected=NullPointerException.class)
	public void testScheduleAtFixedRate_TLL_ThrowsIfTaskIsNull()
			throws Exception
	{
		scheduler.scheduleAtFixedRate(null, 0, 1000);
	}

}
