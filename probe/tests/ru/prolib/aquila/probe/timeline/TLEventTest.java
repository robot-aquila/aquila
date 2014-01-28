package ru.prolib.aquila.probe.timeline;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.junit.*;

public class TLEventTest {
	private IMocksControl control;
	private Runnable procedure;
	private TLEvent e;
	
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

}
