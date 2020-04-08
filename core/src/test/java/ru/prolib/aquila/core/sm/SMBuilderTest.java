package ru.prolib.aquila.core.sm;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SMBuilderTest {
	@Rule
	public ExpectedException eex = ExpectedException.none();
	private static String S_STATE1 = "STATE1",S_STATE2 = "STATE2",S_STATE3 = "STATE3",S_STATE4 = "STATE4";
	private static String E_EXIT1 = "EXIT1",E_EXIT2 = "EXIT2",E_EXIT3 = "EXIT3",E_EXIT4 = "EXIT4",E_EXIT5="EXIT5";
	
	static class StateHandler1 extends SMStateHandler {
		
		public StateHandler1() {
			registerExit(E_EXIT1);
			registerExit(E_EXIT2);
		}
		
	}
	
	static class StateHandler2 extends SMStateHandler {
		
		public StateHandler2() {
			registerExit(E_EXIT2);
			registerExit(E_EXIT3);
		}
		
	}
	
	static class StateHandler3 extends SMStateHandler {
		
		public StateHandler3() {
			registerExit(E_EXIT4);
			setResultDataType(Integer.class);
		}
		
	}
	
	static class StateHandler4 extends SMStateHandler {
		
		public StateHandler4() {
			registerExit(E_EXIT5);
			setIncomingDataType(String.class);
		}
		
	}

	private SMBuilder service;
	
	@Before
	public void setUp() throws Exception {
		service = new SMBuilder();
	}

	@Test (expected=IllegalStateException.class)
	public void testBuild_ThrowsIfNotAllExitsAreTerminated() {
		service.addState(new StateHandler1(), S_STATE1)
			.addState(new StateHandler2(), S_STATE2)
			.setInitialState(S_STATE1)
			
			.addTrans(S_STATE1, E_EXIT1, S_STATE2)
			
			.addTrans(S_STATE2, E_EXIT2, S_STATE1)
			.addFinal(S_STATE2, E_EXIT3)
			
			.build();
	}
	
	@Test
	public void testAddTrans_ThrowsIfDataTypesMismatch() throws Exception {
		eex.expect(IllegalArgumentException.class);
		eex.expectMessage(new StringBuilder()
				.append("Transition data types mismatch. Transition: STATE3.EXIT4 -> STATE4 Expected: ")
				.append(String.class).append(" Actual: ").append(Integer.class)
				.toString());
		
		service.addState(new StateHandler3(), S_STATE3)
			.addState(new StateHandler4(), S_STATE4)
			.setInitialState(S_STATE3);
		
		service.addTrans(S_STATE3, E_EXIT4, S_STATE4);
	}

}
