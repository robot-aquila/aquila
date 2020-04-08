package ru.prolib.aquila.core.sm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ru.prolib.aquila.core.utils.KW;

public class SMBuilder {
	private final Map<String, SMStateHandler> id2state;
	private final Map<KW<SMExit>, SMStateHandler> transitions;
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
	public SMBuilder addState(SMStateHandler state, String id) {
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
	 * @throws IllegalArgumentException - transition data types mismatch
	 */
	public SMBuilder addTrans(String srcState, String srcExit, String tgtState) {
		SMStateHandler src = getState(srcState), tgt = getState(tgtState);
		Class<?> src_r_type = src.getResultDataType(), tgt_i_type = tgt.getIncomingDataType();
		if ( tgt_i_type != Void.class && ! tgt_i_type.isAssignableFrom(src_r_type) ) {
			throw new IllegalArgumentException(new StringBuilder()
					.append("Transition data types mismatch.")
					.append(" Transition: ").append(srcState).append(".").append(srcExit)
					.append(" -> ").append(tgtState)
					.append(" Expected: ").append(tgt_i_type)
					.append(" Actual: ").append(src_r_type)
					.toString());
		}
		transitions.put(new KW<SMExit>(src.getExit(srcExit)), tgt);
		return this;
	}
	
	/**
	 * Add transition to final state.
	 * <p>
	 * @param srcState - identifier of the state to transition from
	 * @param srcExit - exit ID of state from
	 * @return this
	 */
	public SMBuilder addFinal(String srcState, String srcExit) {
		transitions.put(new KW<SMExit>(getState(srcState).getExit(srcExit)), SMStateHandler.FINAL);
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
		Iterator<Map.Entry<String, SMStateHandler>> it = id2state.entrySet().iterator();
		while ( it.hasNext() ) {
			Map.Entry<String, SMStateHandler> entry = it.next();
			for ( SMExit exit : entry.getValue().getExits() ) {
				if ( ! transitions.containsKey(new KW<SMExit>(exit)) ) {
					throw new IllegalStateException(new StringBuilder()
							.append("No transition defined: ")
							.append(entry.getKey())
							.append(".")
							.append(exit.getId())
							.toString());
				}
			}
		}
		
		return new SMStateMachine(getState(initialStateID), transitions);
	}
	
	private SMStateHandler getState(String id) {
		if ( id == null ) {
			throw new NullPointerException("State ID cannot be null");
		}
		SMStateHandler state = id2state.get(id);
		if ( state == null ) {
			throw new IllegalArgumentException("State not exists: " + id);
		}
		return state;
	}

}
