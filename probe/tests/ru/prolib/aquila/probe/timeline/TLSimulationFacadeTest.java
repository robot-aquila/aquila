package ru.prolib.aquila.probe.timeline;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.probe.timeline.TLCommand;
import ru.prolib.aquila.probe.timeline.TLCommandQueue;
import ru.prolib.aquila.probe.timeline.TLSimulationEventDispatcher;
import ru.prolib.aquila.probe.timeline.TLSimulationFacade;
import ru.prolib.aquila.probe.timeline.TLSimulationStrategy;

public class TLSimulationFacadeTest {
	private IMocksControl control;
	private TLCommandQueue commandQueue;
	private TLEventQueue eventQueue;
	private TLSimulationStrategy simulation;
	private TLSimulationEventDispatcher dispatcher;
	private TLSimulationFacade facade;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		commandQueue = control.createMock(TLCommandQueue.class);
		eventQueue = control.createMock(TLEventQueue.class);
		simulation = control.createMock(TLSimulationStrategy.class);
		dispatcher = control.createMock(TLSimulationEventDispatcher.class);
		facade = new TLSimulationFacade(commandQueue,
				eventQueue, simulation, dispatcher);
	}
	
	@Test
	public void testExecuteSimulation() throws Exception {
		expect(simulation.execute()).andReturn(false);
		expect(simulation.execute()).andReturn(true);
		control.replay();
		
		assertFalse(facade.executeSimulation());
		assertTrue(facade.executeSimulation());
		
		control.verify();
	}
	
	@Test
	public void testGetPOA() throws Exception {
		DateTime time = new DateTime();
		expect(eventQueue.getPOA()).andReturn(time);
		control.replay();
		
		assertSame(time, facade.getPOA());
		
		control.verify();
	}
	
	@Test
	public void testFireRunning() throws Exception {
		dispatcher.fireRunning();
		control.replay();
		
		facade.fireRunning();
		
		control.verify();
	}
	
	@Test
	public void testFirePaused() throws Exception {
		dispatcher.firePaused();
		control.replay();
		
		facade.firePaused();
		
		control.verify();
	}

	@Test
	public void testFireFinished() throws Exception {
		dispatcher.fireFinished();
		control.replay();
		
		facade.fireFinished();
		
		control.verify();
	}

	@Test
	public void testFireStepping() throws Exception {
		dispatcher.fireStepping();
		control.replay();
		
		facade.fireStepping();
		
		control.verify();
	}

	@Test
	public void testTell() throws Exception {
		expect(commandQueue.tell()).andReturn(TLCommand.PAUSE);
		control.replay();
		
		assertSame(TLCommand.PAUSE, facade.tell());
		
		control.verify();
	}
	
	@Test
	public void testTellb() throws Exception {
		expect(commandQueue.tellb()).andReturn(TLCommand.PAUSE);
		control.replay();
		
		assertSame(TLCommand.PAUSE, facade.tellb());
		
		control.verify();
	}

	@Test
	public void testPull() throws Exception {
		expect(commandQueue.pull()).andReturn(TLCommand.PAUSE);
		control.replay();
		
		assertSame(TLCommand.PAUSE, facade.pull());
		
		control.verify();
	}
	
	@Test
	public void testPullb() throws Exception {
		expect(commandQueue.pullb()).andReturn(TLCommand.FINISH);
		control.replay();
		
		assertSame(TLCommand.FINISH, facade.pullb());
		
		control.verify();
	}
	
	@Test
	public void testSimulationFinished() throws Exception {
		expect(eventQueue.finished()).andReturn(true);
		expect(eventQueue.finished()).andReturn(false);
		control.replay();
		
		assertTrue(facade.simulationFinished());
		assertFalse(facade.simulationFinished());
		
		control.verify();
	}
	
	@Test
	public void testClearCommands() throws Exception {
		commandQueue.clear();
		control.replay();
		
		facade.clearCommands();
		
		control.verify();
	}

}
