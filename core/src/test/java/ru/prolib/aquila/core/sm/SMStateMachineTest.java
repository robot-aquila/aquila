package ru.prolib.aquila.core.sm;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.utils.KW;

/**
 * Тест исключительных ситуаций.
 * Тесты функционала смотри в файлах по маске SMStateMachine_*ExampleTest. 
 */
public class SMStateMachineTest {
	static final Logger logger = LoggerFactory.getLogger(SMStateMachineTest.class);
	
	@Rule
	public ExpectedException eex = ExpectedException.none();
	private IMocksControl control; 
	private Map<KW<SMExit>, SMStateHandler> transitions;
	private SMStateHandler initState, state2;
	private SMInput in1, in2;
	private SMExit exit1, exit2;
	private SMStateMachine automat;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}
	
	@Before
	public void setUp() throws Exception {
		exit1 = exit2 = null;
		in1 = in2 = null;
		control = createStrictControl();
		initState = new SMStateHandler();
		state2 = new SMStateHandler();
		transitions = new HashMap<KW<SMExit>, SMStateHandler>();
		automat = new SMStateMachine(initState, transitions);
		automat.setDebug(true);
	}

	@Test (expected=SMStateMachineNotStartedException.class)
	public void testInput2_ThrowsIfNotStarted() throws Exception {
		automat.input(null, null);
	}
	
	@Test (expected=SMBadInputException.class)
	public void testInput2_ThrowsIfInputOfDifferentState() throws Exception {
		exit2 = state2.registerExit("booka");
		in2 = state2.registerInput(new SMInputStub(exit2));
		
		automat.start();
		automat.input(in2, null);
	}
	
	@Test (expected=SMTransitionNotExistsException.class)
	public void testInput2_ThrowsIfTransitionNotExists() throws Exception {
		exit1 = initState.registerExit("xena");
		in1 = initState.registerInput(new SMInputStub(exit1));
		
		automat.start();
		automat.input(in1, null);
	}

	@Test (expected=SMStateMachineNotStartedException.class)
	public void testInput1_ThrowsIfNotStarted() throws Exception {
		automat.input(null);
	}
	
	@Test (expected=SMStateHasNoInputException.class)
	public void testInput1_ThrowsIfStateHasNoInput() throws Exception {
		automat.start();
		automat.input(null);
	}
	
	@Test (expected=SMAmbiguousInputException.class)
	public void testInput1_ThrowsIfAmbiguousInput() throws Exception {
		exit1 = initState.registerExit("foo");
		in1 = initState.registerInput(new SMInputStub(exit1));
		in2 = initState.registerInput(new SMInputStub(exit1));
		automat.start();
		automat.input(null);
	}
	
	@Test (expected=SMTransitionNotExistsException.class)
	public void testInput1_ThrowsIfTransitionNotExists() throws Exception {
		exit1 = initState.registerExit("gabba");
		in1 = initState.registerInput(new SMInputStub(exit1));
		
		automat.start();
		automat.input(null);
	}
	
	@Test (expected=SMStateMachineAlreadyStartedException.class)
	public void testStart_ThrowsIfStarted() throws Exception {
		automat.start();
		automat.start();
	}
	
	@Test
	public void testStart_ThrowsIfTransitionNotExists() throws Exception {
		SMEnterAction enterAction = control.createMock(SMEnterAction.class);
		SMExitAction exitAction = control.createMock(SMExitAction.class);
		exit1 = initState.registerExit("achiless");
		initState.setEnterAction(enterAction);
		initState.setExitAction(exitAction); // exit actions should work too
		expect(enterAction.enter((SMTriggerRegistry) anyObject()))
			.andReturn(exit1);
		exitAction.exit();
		control.replay();
		
		try {
			automat.start();
			fail("Expected: SMTransitionNotExistsException");
		} catch ( SMTransitionNotExistsException e ) {
			control.verify();
		}
	}
	
	static class TestHandler1 extends SMStateHandler implements SMEnterAction, SMExitAction {
		
		public TestHandler1() {
			this.setEnterAction(this);
			this.setExitAction(this);
			this.registerExit("MOVE");
			this.setResultDataType(Integer.class);
		}
		
		@Override
		public SMExit enter(SMTriggerRegistry triggers) {
			return getExit("MOVE");
		}

		@Override
		public void exit() {
			setResultData(Integer.valueOf(256));
		}

	}
	
	static class TestHandler2 extends SMStateHandler implements SMEnterAction, SMExitAction {
		
		public TestHandler2() {
			this.setEnterAction(this);
			this.setExitAction(this);
			this.registerExit("PASS");
			this.setIncomingDataType(Integer.class);
			this.setResultDataType(String.class);
		}

		@Override
		public SMExit enter(SMTriggerRegistry triggers) {
			return getExit("PASS");
		}
		
		@Override
		public void exit() {
			Integer input = getIncomingData();
			setResultData("Received int: " + input);
		}
		
	}
	
	static class TestHandler3 extends SMStateHandler implements SMEnterAction, SMExitAction {
		final CountDownLatch finished;
		final StringBuilder sb;
		
		public TestHandler3(CountDownLatch finished, StringBuilder sb) {
			this.finished = finished;
			this.sb = sb;
			this.setEnterAction(this);
			this.setExitAction(this);
			this.registerExit("DONE");
			this.setIncomingDataType(String.class);
		}

		@Override
		public SMExit enter(SMTriggerRegistry triggers) {
			return getExit("DONE");
		}
		
		@Override
		public void exit() {
			String result = getIncomingData();
			sb.append(result);
			finished.countDown();
		}
		
	}
	
	static class TestHandler4 extends SMStateHandler implements SMEnterAction, SMExitAction {
		
		public TestHandler4() {
			this.setEnterAction(this);
			this.setExitAction(this);
			this.registerExit("BOOM");
			this.setResultDataType(String.class);
		}

		@Override
		public SMExit enter(SMTriggerRegistry triggers) {
			return getExit("BOOM");
		}
		
		@Override
		public void exit() {
			setResultData("Bobbie W. Draper");
		}
		
	}
	
	static class TestHandler5 extends SMStateHandler implements SMEnterAction, SMInputAction {

		public TestHandler5() {
			this.setEnterAction(this);
			this.registerInput(this);
			this.registerExit("XENA");
		}

		@Override
		public SMExit enter(SMTriggerRegistry triggers) {
			return null;
		}
		
		@Override
		public SMExit input(Object data) {
			return getExit("XENA");
		}
		
	}
	
	@Test
	public void testInterstateDataTransfer() throws Exception {
		CountDownLatch finished = new CountDownLatch(1);
		StringBuilder sb = new StringBuilder().append("Test result: ");
		SMStateHandler state1 = new TestHandler1(), state2 = new TestHandler2(), state3 = new TestHandler3(finished, sb);
		transitions.put(new KW<>(state1.getExit("MOVE")), state2);
		transitions.put(new KW<>(state2.getExit("PASS")), state3);
		transitions.put(new KW<>(state3.getExit("DONE")), SMStateHandler.FINAL);
		automat = new SMStateMachine(state1, transitions);
		automat.setDebug(true);
		
		automat.start();
		
		assertTrue(finished.await(1, TimeUnit.SECONDS));
		assertEquals("Test result: Received int: 256", sb.toString());
	}
	
	@Test
	public void testInterstateDataTransfer_DataTypeMismatch() throws Exception {
		CountDownLatch finished = new CountDownLatch(1);
		StringBuilder sb = new StringBuilder().append("nothing shall be added");
		SMStateHandler state1 = new TestHandler1(), state2 = new TestHandler3(finished, sb);
		transitions.put(new KW<>(state1.getExit("MOVE")), state2);
		transitions.put(new KW<>(state2.getExit("DONE")), SMStateHandler.FINAL);
		automat = new SMStateMachine(state1, transitions);
		automat.setId("BOBBY");
		automat.setDebug(true);
		eex.expect(SMRuntimeException.class);
		eex.expectMessage(new StringBuilder()
			.append("Data types mismatch while passing data between states. Machine ID: BOBBY ")
			.append("Transition: TestHandler1.MOVE -> TestHandler3 Expected: class java.lang.String ")
			.append("Actual: class java.lang.Integer")
			.toString());
		
		automat.start();
	}

	@Test
	public void testInterstateDataTransfer_DataLossAtVoidIncomingType() throws Exception {
		CountDownLatch finished = new CountDownLatch(1);
		StringBuilder sb = new StringBuilder().append("Test result: ");
		SMStateHandler state1 = new TestHandler1(), state2 = new TestHandler2(),
				state3 = new TestHandler3(finished, sb), state4 = new TestHandler4();
		transitions.put(new KW<>(state1.getExit("MOVE")), state2);
		transitions.put(new KW<>(state2.getExit("PASS")), state4);
		transitions.put(new KW<>(state4.getExit("BOOM")), state3);
		transitions.put(new KW<>(state3.getExit("DONE")), SMStateHandler.FINAL);
		automat = new SMStateMachine(state1, transitions);
		automat.setDebug(true);
		
		automat.start();
		
		assertTrue(finished.await(1, TimeUnit.SECONDS));
		assertEquals("Test result: Bobbie W. Draper", sb.toString());
	}
	
	@Test
	public void testWaitForFinish() throws Exception {
		SMStateHandler state1 = new TestHandler5(), state2 = new TestHandler1();
		transitions.put(new KW<>(state1.getExit("XENA")), state2);
		transitions.put(new KW<>(state2.getExit("MOVE")), SMStateHandler.FINAL);
		automat = new SMStateMachine(state1, transitions);
		automat.setDebug(true);
		CountDownLatch finished = new CountDownLatch(1);
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					automat.waitForFinish(1, TimeUnit.SECONDS);
					finished.countDown();
				} catch ( Exception e ) {
					logger.error("Unexpected exception: ", e);
				}
			}
		};
		thread.start();

		automat.start();
		assertFalse(automat.finished());
		automat.input(null);
		assertTrue(finished.await(1, TimeUnit.SECONDS));
		assertTrue(automat.finished());
	}
	
	@Test
	public void testWaitForFinish_ThrowsIfTimeout() throws Exception {
		SMStateHandler state1 = new TestHandler5(), state2 = new TestHandler1();
		transitions.put(new KW<>(state1.getExit("XENA")), state2);
		transitions.put(new KW<>(state2.getExit("MOVE")), SMStateHandler.FINAL);
		automat = new SMStateMachine(state1, transitions);
		automat.setDebug(true);
		automat.start();
		eex.expect(TimeoutException.class);
		
		automat.waitForFinish(100L, TimeUnit.MILLISECONDS);
	}

}
