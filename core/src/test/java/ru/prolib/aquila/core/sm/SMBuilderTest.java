package ru.prolib.aquila.core.sm;

import org.junit.Before;
import org.junit.Test;

public class SMBuilderTest {
	private static String S_STATE1 = "STATE1";
	private static String S_STATE2 = "STATE2";
	private static String E_EXIT1 = "EXIT1";
	private static String E_EXIT2 = "EXIT2";
	private static String E_EXIT3 = "EXIT3";
	
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

}
