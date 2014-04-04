package ru.prolib.aquila.probe.timeline;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.sm.SMExit;

public class TLStateTest {
	private IMocksControl control;
	private TLSimulationFacade facade;
	private TestInterface iface;
	private TestState state;

	static interface TestInterface {
		public void prepare();
		public SMExit pass();
		public void cleanup();
	}

	static class TestState extends TLState {
		private TestInterface iface;
		public final SMExit ex1;
		public TestState(TestInterface iface, TLSimulationFacade facade) {
			super(facade);
			this.iface = iface;
			ex1 = registerExit();
		}
		@Override public SMExit pass() { return iface.pass(); }
		@Override public void prepare() { iface.prepare(); }
		@Override public void cleanup() { iface.cleanup(); }
	}
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		facade = control.createMock(TLSimulationFacade.class);
		iface = control.createMock(TestInterface.class);
		state = new TestState(iface, facade);
	}

	@Test
	public void test_() throws Exception {
		iface.prepare();
		expect(iface.pass()).andReturn(null);
		expect(iface.pass()).andReturn(null);
		expect(iface.pass()).andReturn(null);
		expect(iface.pass()).andReturn(state.ex1);
		iface.cleanup();
		control.replay();
		
		assertSame(state.ex1, state.enter());
		
		control.verify();
	}

}
