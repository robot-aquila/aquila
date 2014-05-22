package ru.prolib.aquila.probe.timeline;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.probe.timeline.TLCmd;

/**
 * Протестированы не все варианты ветвления алгоритма. 
 * Независимо от того, была ли эта первая команда после входа в состояние или же
 * это последующая команда с более поздним временем отсечки, процесс симуляции
 * шага выполняется одинаково. Что бы подсократить, тесты логики симуляции
 * шага используют первую команду или последующие команды в смешенном режиме.   
 */
public class TLASRunTest {
	private IMocksControl control;
	private TLSTimeline facade;
	private DateTime poa;
	private TLASRun state;
	private TLCmd c, c2;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		facade = control.createMock(TLSTimeline.class);
		poa = new DateTime(1997, 7, 12, 20, 15, 0, 999);
		state = new TLASRun(facade);
	}
	
	@Test
	public void testPrepare() throws Exception {
		state.setCurrentCommand(TLCmd.FINISH);
		facade.fireRun();
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
		c = new TLCmd(new DateTime(1997, 7, 12, 0, 0, 0, 0));
		state.setCurrentCommand(c);
		c2 = new TLCmd(new DateTime(1997, 7, 12, 1, 0, 0, 0));
		expect(facade.pull()).andReturn(c2);
		expect(facade.getPOA()).andReturn(poa);
		expect(facade.execute()).andReturn(true);
		facade.fireStep();
		expect(facade.finished()).andReturn(false);
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
		c = new TLCmd(new DateTime(1997, 7, 12, 0, 0, 0, 0));
		state.setCurrentCommand(c);
		expect(facade.pull()).andReturn(null);
		expect(facade.getPOA()).andReturn(poa);
		expect(facade.execute()).andReturn(false);
		expect(facade.finished()).andReturn(true);
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
		c = new TLCmd(new DateTime(1997, 7, 12, 0, 0, 0, 0));
		state.setCurrentCommand(c);
		c2 = new TLCmd(new DateTime(1997, 7, 13, 0, 0, 0, 0));
		expect(facade.pull()).andReturn(c2);
		expect(facade.getPOA()).andReturn(poa);
		expect(facade.finished()).andReturn(false);
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
		c = new TLCmd(new DateTime(1997, 7, 13, 0, 0, 0, 0));
		state.setCurrentCommand(c);
		expect(facade.pull()).andReturn(null);
		expect(facade.getPOA()).andReturn(poa);
		expect(facade.finished()).andReturn(true);
		control.replay();
		
		assertSame(state.onFinish, state.pass());
		
		control.verify();
	}

	@Test
	public void testPass_CmdPause() throws Exception {
		c = new TLCmd(new DateTime(1997, 7, 12, 0, 0, 0, 0));
		state.setCurrentCommand(c);
		expect(facade.pull()).andReturn(TLCmd.PAUSE);
		control.replay();
		
		assertSame(state.onPause, state.pass());
		
		control.verify();
	}
	
	@Test
	public void testPass_CmdFinish() throws Exception {
		c = new TLCmd(new DateTime(1997, 7, 12, 0, 0, 0, 0));
		state.setCurrentCommand(c);
		expect(facade.pull()).andReturn(TLCmd.FINISH);
		control.replay();
		
		assertSame(state.onFinish, state.pass());
		
		control.verify();
	}

}
