package ru.prolib.aquila.probe.timeline;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;

public class TLEventTest {
	private IMocksControl control;
	private Runnable procedure;
	private TLEvent e;
	
	/**
	 * Для проверки метода сравнения, где должны сравниваться экземпляры
	 * процедур, а не их эквивалентность. Так же для тестирования метода
	 * конвертации в строку.
	 */
	static class MyRunnable implements Runnable {
		private final String id;
		MyRunnable() { id = null; }
		MyRunnable(String id) { this.id = id; }
		@Override public void run() { }
		@Override public boolean equals(Object other) { return true; }
		@Override public String toString() { return id; }
	}
	
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		procedure = control.createMock(Runnable.class);
		e = new TLEvent(new DateTime(2013, 11, 29, 13, 10, 52, 0), procedure);
	}

	@Test
	public void testGetters() throws Exception {
		assertEquals(new DateTime(2013, 11, 29, 13, 10, 52, 0), e.getTime());
		assertSame(procedure, e.getProcedure());
	}
	
	@Test
	public void testExecute() throws Exception {
		procedure.run();
		control.replay();
		
		e.execute();
		
		control.verify();
		assertTrue(e.executed());
	}
	
	@Test
	public void testExecute_RunsOnce() throws Exception {
		procedure.run();
		control.replay();
		
		e.execute();
		e.execute();
		
		control.verify();
	}
	
	@Test
	public void testToString() throws Exception {
		e = new TLEvent(e.getTime(), new MyRunnable("abc"));
		String expected = "TLEvent[" + e.getTime() + " for abc]";
		assertEquals(expected, e.toString());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(e.equals(e));
		assertFalse(e.equals(null));
		assertFalse(e.equals(this));
	}

	@Test
	public void testEquals() throws Exception {
		procedure = new MyRunnable();
		e = new TLEvent(new DateTime(2013, 11, 29, 13, 10, 52, 0), procedure);
		Variant<DateTime> vTime = new Variant<DateTime>()
			.add(new DateTime(2013, 11, 29, 13, 10, 52, 0))
			.add(new DateTime());
		Variant<Runnable> vProc = new Variant<Runnable>(vTime)
			.add(procedure)
			.add(new MyRunnable());
		Variant<Boolean> vExec = new Variant<Boolean>(vProc)
			.add(true)
			.add(false);
		Variant<?> iterator = vExec;
		int foundCnt = 0;
		TLEvent x, found = null;
		do {
			x = new TLEvent(vTime.get(), vProc.get());
			if ( vExec.get() ) {
				x.execute();
			}
			if ( e.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(procedure, found.procedure);
		assertEquals(new DateTime(2013, 11, 29, 13, 10, 52, 0), found.time);
	}

}
