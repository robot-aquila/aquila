package ru.prolib.aquila.core.sm;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.KW;

/**
 * Тест исключительных ситуаций.
 * Тесты функционала смотри в файлах по маске SMStateMachine_*ExampleTest. 
 */
public class SMStateMachineTest {
	private IMocksControl control; 
	private Map<KW<SMExit>, SMState> transitions;
	private SMState initState, state2;
	private SMInput in1, in2;
	private SMExit exit1, exit2;
	private SMStateMachine automat;
	
	@Before
	public void setUp() throws Exception {
		exit1 = exit2 = null;
		in1 = in2 = null;
		control = createStrictControl();
		initState = new SMState();
		state2 = new SMState();
		transitions = new HashMap<KW<SMExit>, SMState>();
		automat = new SMStateMachine(initState, transitions);
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

}
