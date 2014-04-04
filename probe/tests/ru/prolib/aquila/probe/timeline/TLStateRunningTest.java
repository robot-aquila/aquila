package ru.prolib.aquila.probe.timeline;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.probe.timeline.TLCommand;

/**
 * Протестированы не все варианты ветвления алгоритма. 
 * Независимо от того, была ли эта первая команда после входа в состояние или же
 * это последующая команда с более поздним временем отсечки, процесс симуляции
 * шага выполняется одинаково. Что бы подсократить, тесты логики симуляции
 * шага используют первую команду или последующие команды в смешенном режиме.   
 */
public class TLStateRunningTest {
	private IMocksControl control;
	private TLSimulationFacade facade;
	private DateTime poa;
	private TLStateRunning state;
	private TLCommand c, c2;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		facade = control.createMock(TLSimulationFacade.class);
		poa = new DateTime(1997, 7, 12, 20, 15, 0, 999);
		state = new TLStateRunning(facade);
	}
	
	@Test
	public void testPrepare() throws Exception {
		state.setCurrentCommand(TLCommand.FINISH);
		facade.fireRunning();
		control.replay();
		
		state.prepare();
		
		control.verify();
		assertNull(state.getCurrentCommand());
	}
	
	@Test
	public void testPass_Interrupted() throws Exception {
		expect(facade.pullb()).andThrow(new InterruptedException("test error"));
		control.replay();
		
		assertSame(state.onFinish, state.pass());
		
		control.verify();
	}
	
	/**
	 * Тест этой ветви развития событий использует новую команду.
	 * <p>
	 * @throws Exception
	 */
	@Test
	public void testPass_Continue() throws Exception {
		c = new TLCommand(new DateTime(1997, 7, 12, 0, 0, 0, 0));
		state.setCurrentCommand(c);
		c2 = new TLCommand(new DateTime(1997, 7, 12, 1, 0, 0, 0));
		expect(facade.pull()).andReturn(c2);
		expect(facade.getPOA()).andReturn(poa);
		expect(facade.executeSimulation()).andReturn(true);
		facade.fireStepping();
		expect(facade.simulationFinished()).andReturn(false);
		control.replay();
		
		assertNull(state.pass());
		
		control.verify();
	}
	
	/**
	 * Тест этой ветви развития событий использует команду, полученную ранее.
	 * <p>
	 * @throws Exception
	 */
	@Test
	public void testPass_Finish() throws Exception {
		c = new TLCommand(new DateTime(1997, 7, 12, 0, 0, 0, 0));
		state.setCurrentCommand(c);
		expect(facade.pull()).andReturn(null);
		expect(facade.getPOA()).andReturn(poa);
		expect(facade.executeSimulation()).andReturn(false);
		expect(facade.simulationFinished()).andReturn(true);
		control.replay();
		
		assertSame(state.onFinish, state.pass());
		
		control.verify();
	}
	
	/**
	 * Тест этой ветви развития событий использует новую команду.
	 * <p>
	 * @throws Exception
	 */
	@Test
	public void testPass_AftPOA_Pause() throws Exception {
		c = new TLCommand(new DateTime(1997, 7, 12, 0, 0, 0, 0));
		state.setCurrentCommand(c);
		c2 = new TLCommand(new DateTime(1997, 7, 13, 0, 0, 0, 0));
		expect(facade.pull()).andReturn(c2);
		expect(facade.getPOA()).andReturn(poa);
		expect(facade.simulationFinished()).andReturn(false);
		control.replay();
		
		assertSame(state.onPause, state.pass());
		
		control.verify();
	}
	
	/**
	 * Тест этой ветви развития событий использует команду, полученную ранее.
	 * <p>
	 * @throws Exception
	 */
	@Test
	public void testPass_AftPOA_Finish() throws Exception {
		c = new TLCommand(new DateTime(1997, 7, 13, 0, 0, 0, 0));
		state.setCurrentCommand(c);
		expect(facade.pull()).andReturn(null);
		expect(facade.getPOA()).andReturn(poa);
		expect(facade.simulationFinished()).andReturn(true);
		control.replay();
		
		assertSame(state.onFinish, state.pass());
		
		control.verify();
	}

	@Test
	public void testPass_CmdPause() throws Exception {
		c = new TLCommand(new DateTime(1997, 7, 12, 0, 0, 0, 0));
		state.setCurrentCommand(c);
		expect(facade.pull()).andReturn(TLCommand.PAUSE);
		control.replay();
		
		assertSame(state.onPause, state.pass());
		
		control.verify();
	}
	
	@Test
	public void testPass_CmdFinish() throws Exception {
		c = new TLCommand(new DateTime(1997, 7, 12, 0, 0, 0, 0));
		state.setCurrentCommand(c);
		expect(facade.pull()).andReturn(TLCommand.FINISH);
		control.replay();
		
		assertSame(state.onFinish, state.pass());
		
		control.verify();
	}

}
