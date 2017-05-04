package ru.prolib.aquila.core.sm;

import java.util.HashMap;
import java.util.Map;

import ru.prolib.aquila.core.utils.KW;

public class SMBuilder {
	private final Map<String, SMState> id2state;
	private final Map<KW<SMExit>, SMState> transitions;
	private String initialStateID;
	
	public SMBuilder() {
		id2state = new HashMap<>();
		transitions = new HashMap<>();
	}
	
	/**
	 * Add state.
	 * <p>
	 * @param state - state instance
	 * @param id - string ID of the state
	 * @return this
	 */
	public SMBuilder addState(SMState state, String id) {
		if ( id2state.containsKey(id) ) {
			throw new IllegalArgumentException("State already exists: " + id);
		}
		id2state.put(id, state);
		return this;
	}
	
	/**
	 * Add transition.
	 * <p>
	 * @param srcState - identifier of the state to transition from
	 * @param srcExit - exit ID of state from
	 * @param tgtState - identifier of the state to transition to
	 * @return this
	 */
	public SMBuilder addTrans(String srcState, String srcExit, String tgtState) {
		transitions.put(new KW<SMExit>(getState(srcState).getExit(srcExit)), getState(tgtState));
		return this;
	}
	
	/**
	 * Add transition to final state.
	 * <p>
	 * @param srcState - identifier of the state to transition from
	 * @param srcExit - exit ID of state from
	 * @return this
	 */
	public SMBuilder addTransFinal(String srcState, String srcExit) {
		transitions.put(new KW<SMExit>(getState(srcState).getExit(srcExit)), SMState.FINAL);
		return this;
	}

	/**
	 * Set initial state.
	 * <p>
	 * @param id - string ID of the state
	 * @return this
	 */
	public SMBuilder setInitialState(String id) {
		getState(id);
		initialStateID = id;
		return this;
	}
	
	/**
	 * Build state machine.
	 * <p>
	 * @return state machine
	 */
	public SMStateMachine build() {
		return new SMStateMachine(getState(initialStateID), transitions);
	}
	
	private SMState getState(String id) {
		if ( id == null ) {
			throw new NullPointerException("State ID cannot be null");
		}
		SMState state = id2state.get(id);
		if ( state == null ) {
			throw new IllegalArgumentException("State not exists: " + id);
		}
		return state;
	}

}
