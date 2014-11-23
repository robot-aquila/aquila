package ru.prolib.aquila.probe.internal;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.junit.*;

import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.probe.timeline.TLEvent;
import ru.prolib.aquila.probe.timeline.TLException;

public class TickDataDispatcherTest {
	private IMocksControl control;
	private Aqiterator<Tick> it1, it2;
	private TickHandler tasks, tasks2;
	private TickDataDispatcher dispatcher;
	
	private static final SimpleDateFormat f =
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
	
	/**
	 * Parse string "yyyy-MM-dd HH:mm:ss" datetime format into joda-time.
	 * <p> 
	 * @param time
	 * @return
	 * @throws Exception
	 */
	public static DateTime time(String time) throws Exception {
		return new DateTime(f.parse(time));
	}

	
	/**
	 * Вспомогательный класс задачи в связи с тиком данных. 
	 */
	static class TaskCheck implements Runnable {
		private final Tick tick;
		TaskCheck(Tick tick) {
			this.tick = tick;
		}
		
		@Override public boolean equals(Object other) {
			if ( other == null || other.getClass() != TaskCheck.class ) {
				return false;
			}
			if ( other == this ) {
				return true;
			}
			TaskCheck o = (TaskCheck) other;
			return tick.equals(o.tick);
		}

		@Override public void run() {
			
		}
		
	}
	
	private static List<Tick> getExpectedAsList() throws Exception {
		List<Tick> ticks = new LinkedList<Tick>();
		ticks.add(new Tick(time("2014-06-18 09:59:59"), 144.79, 250.0));
		ticks.add(new Tick(time("2014-06-18 18:34:20"), 148.79,   5.0));
		ticks.add(new Tick(time("2014-06-18 18:44:15"), 141.79,  10.0));
		ticks.add(new Tick(time("2014-06-19 10:00:00"), 154.98,   1.54));
		ticks.add(new Tick(time("2014-06-19 15:00:00"), 154.80, 500.0));
		ticks.add(new Tick(time("2014-06-25 10:00:01"), 148.70, 300.0));
		ticks.add(new Tick(time("2014-06-25 10:00:02"), 147.70,   1.4));
		return ticks;
	}
	
	private static Aqiterator<Tick> createTestReader() throws Exception {
		return new SimpleIterator<Tick>(getExpectedAsList());
	}

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		it1 = control.createMock(Aqiterator.class);
		it2 = control.createMock(Aqiterator.class);
		tasks = control.createMock(TickHandler.class);
		tasks2 = control.createMock(TickHandler.class);
	}
	
	@Test
	public void testWorkflow() throws Exception {
		Vector<Tick> ticks = new Vector<Tick>(getExpectedAsList());

		Tick tick = ticks.get(0);
		tasks.doInitialTask(eq(tick));
		tasks.doDailyTask(eq((Tick)null), eq(tick));
		expect(tasks.createTask(eq(tick))).andReturn(new TaskCheck(tick));
		
		tick = ticks.get(1);
		expect(tasks.createTask(eq(tick))).andReturn(new TaskCheck(tick));
		
		tick = ticks.get(2);
		expect(tasks.createTask(eq(tick))).andReturn(new TaskCheck(tick));
		
		tick = ticks.get(3);
		tasks.doDailyTask(ticks.get(2), tick);
		expect(tasks.createTask(eq(tick))).andReturn(new TaskCheck(tick));
		
		tick = ticks.get(4);
		expect(tasks.createTask(eq(tick))).andReturn(new TaskCheck(tick));
		
		tick = ticks.get(5);
		tasks.doDailyTask(ticks.get(4), tick);
		expect(tasks.createTask(eq(tick))).andReturn(new TaskCheck(tick));
		
		tick = ticks.get(6);
		expect(tasks.createTask(eq(tick))).andReturn(new TaskCheck(tick));
		
		tasks.doFinalTask(eq(tick));
		control.replay();
		
		dispatcher = new TickDataDispatcher(createTestReader(), tasks);
		
		Vector<TaskCheck> actual = new Vector<TaskCheck>();
		TLEvent event = null;
		while ( (event = dispatcher.pullEvent()) != null ) {
			actual.add((TaskCheck)event.getProcedure());
		}
		
		control.verify();
		Vector<TaskCheck> expected = new Vector<TaskCheck>();
		for ( int i = 0; i < 7; i ++ ) {
			expected.add(new TaskCheck(ticks.get(i)));
		}
		assertEquals(expected, actual);
	}

	@SuppressWarnings("unchecked")
	@Test (expected=TLException.class)
	public void testPullEvent_ThrowsIfReaderThrows() throws Exception {
		Aqiterator<Tick> reader = control.createMock(Aqiterator.class);
		expect(reader.next()).andThrow(new DataException("test error"));
		control.replay();
		
		dispatcher = new TickDataDispatcher(reader, tasks);
		dispatcher.pullEvent();
	}
	
	@Test
	public void testWorkflow_IfClosed() throws Exception {
		control.replay();
		
		dispatcher = new TickDataDispatcher(createTestReader(), tasks);
		assertFalse(dispatcher.closed());
		dispatcher.close();
		assertTrue(dispatcher.closed());
		
		Vector<TaskCheck> actual = new Vector<TaskCheck>();
		TLEvent event = null;
		while ( (event = dispatcher.pullEvent()) != null ) {
			actual.add((TaskCheck)event.getProcedure());
		}
		
		control.verify();
		Vector<TaskCheck> expected = new Vector<TaskCheck>();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testEquals() throws Exception {
		dispatcher = new TickDataDispatcher(it1, tasks);
		TickDataDispatcher d1 = new TickDataDispatcher(it1, tasks),
				d2 = new TickDataDispatcher(it2, tasks),
				d3 = new TickDataDispatcher(it1, tasks2),
				d4 = new TickDataDispatcher(it2, tasks2);
		assertTrue(dispatcher.equals(dispatcher));
		assertTrue(dispatcher.equals(d1));
		assertFalse(dispatcher.equals(null));
		assertFalse(dispatcher.equals(this));
		assertFalse(dispatcher.equals(d2));
		assertFalse(dispatcher.equals(d3));
		assertFalse(dispatcher.equals(d4));
	}

}