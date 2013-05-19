package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.utils.TerminalController;
import ru.prolib.aquila.core.BusinessEntities.utils.TerminalControllerHelper;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2013-02-10<br>
 * $Id$
 */
public class TerminalControllerTest {
	private IMocksControl control;
	private TerminalControllerHelper helper;
	private EditableTerminal terminal;
	private CountDownLatch started;
	private Thread thread;
	private Runnable runnable;
	private TerminalController controller;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		helper = control.createMock(TerminalControllerHelper.class);
		terminal = control.createMock(EditableTerminal.class);
		started = control.createMock(CountDownLatch.class);
		thread = control.createMock(Thread.class);
		runnable = control.createMock(Runnable.class);
		controller = new TerminalController(helper);
	}
	
	@Test
	public void testConstruct1() throws Exception {
		assertSame(helper, controller.getHelper());
	}
	
	@Test
	public void testConstruct0() throws Exception {
		controller = new TerminalController();
		assertEquals(new TerminalControllerHelper(), controller.getHelper());
	}
	
	@Test
	public void testRunStartSequence_Ok() throws Exception {
		expect(helper.createStartedSignal()).andReturn(started);
		expect(helper.createStartSequence(same(terminal), same(started)))
			.andReturn(runnable);
		expect(helper.createThread(same(runnable))).andReturn(thread);
		thread.start();
		expect(started.await(1000, TimeUnit.MILLISECONDS)).andReturn(true);
		control.replay();
		
		controller.runStartSequence(terminal);
		
		control.verify();
	}
	
	@Test
	public void testRunStartSequence_StartTimeout() throws Exception {
		expect(helper.createStartedSignal()).andReturn(started);
		expect(helper.createStartSequence(same(terminal), same(started)))
			.andReturn(runnable);
		expect(helper.createThread(same(runnable))).andReturn(thread);
		thread.start();
		expect(started.await(1000, TimeUnit.MILLISECONDS)).andReturn(false);
		control.replay();
		
		controller.runStartSequence(terminal);
		
		control.verify();
	}
	
	@Test (expected=RuntimeException.class)
	public void testRunStartSequence_Interrupted() throws Exception {
		expect(helper.createStartedSignal()).andReturn(started);
		expect(helper.createStartSequence(same(terminal), same(started)))
			.andReturn(runnable);
		expect(helper.createThread(same(runnable))).andReturn(thread);
		thread.start();
		expect(started.await(1000, TimeUnit.MILLISECONDS))
			.andThrow(new InterruptedException("Test exception"));
		control.replay();
		
		controller.runStartSequence(terminal);
	}

	@Test
	public void testRunStopSequence_Ok() throws Exception {
		expect(helper.createStartedSignal()).andReturn(started);
		expect(helper.createStopSequence(same(terminal), same(started)))
			.andReturn(runnable);
		expect(helper.createThread(same(runnable))).andReturn(thread);
		thread.start();
		expect(started.await(1000, TimeUnit.MILLISECONDS)).andReturn(true);
		control.replay();
		
		controller.runStopSequence(terminal);
		
		control.verify();
	}
	
	@Test
	public void testRunStopSequence_StartTimeout() throws Exception {
		expect(helper.createStartedSignal()).andReturn(started);
		expect(helper.createStopSequence(same(terminal), same(started)))
			.andReturn(runnable);
		expect(helper.createThread(same(runnable))).andReturn(thread);
		thread.start();
		expect(started.await(1000, TimeUnit.MILLISECONDS)).andReturn(false);
		control.replay();
		
		controller.runStopSequence(terminal);
		
		control.verify();
	}
	
	@Test (expected=RuntimeException.class)
	public void testRunStopSequence_Interrupted() throws Exception {
		expect(helper.createStartedSignal()).andReturn(started);
		expect(helper.createStopSequence(same(terminal), same(started)))
			.andReturn(runnable);
		expect(helper.createThread(same(runnable))).andReturn(thread);
		thread.start();
		expect(started.await(1000, TimeUnit.MILLISECONDS))
			.andThrow(new InterruptedException("Test exception"));
		control.replay();
		
		controller.runStopSequence(terminal);
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(controller.equals(controller));
		assertFalse(controller.equals(null));
		assertFalse(controller.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<TerminalControllerHelper> vHlpr =
				new Variant<TerminalControllerHelper>()
			.add(helper)
			.add(control.createMock(TerminalControllerHelper.class));
		Variant<?> iterator = vHlpr;
		int foundCnt = 0;
		TerminalController x = null, found = null;
		do {
			x = new TerminalController(vHlpr.get());
			if ( controller.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(helper, found.getHelper());
	}

}
