package ru.prolib.aquila.core.sm;

import java.time.Instant;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.Scheduler;

public class SMStateHandlerEx extends SMStateHandler implements
	SMEnterAction,
	OnInterruptAction.Handler
{
	public static final String E_ERROR = "ERROR";
	public static final String E_INTERRUPT = "INTERRUPT";
	
	protected final SMInput inInterrupt;
	
	public SMStateHandlerEx() {
		super();
		setEnterAction(this);
		registerExit(E_ERROR);
		registerExit(E_INTERRUPT);
		inInterrupt = registerInput(new OnInterruptAction(this));
	}

	@Override
	public SMExit onInterrupt(Object data) {
		return getExit(E_INTERRUPT);
	}
	
	/**
	 * Get input of interruption signal.
	 * <p>
	 * @return input
	 */
	public SMInput getInterrupt() {
		return inInterrupt;
	}
	
	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		return null;
	}
	
	protected SMTrigger newTriggerOnEvent(EventType type, SMInput input) {
		return new SMTriggerOnEvent(type, input);
	}
	
	protected SMTrigger newExitOnEvent(EventType type, String exitID) {
		return newTriggerOnEvent(type, registerInput(new SMInputStub(getExit(exitID))));
	}
	
	/**
	 * This method has wrong name.
	 * Use {@link #newTriggerOnTimer(Scheduler, Instant, SMInput)} instead.
	 * <p>
	 * @param scheduler - scheduler
	 * @param time - time to run
	 * @param input - input to pass data in
	 * @return trigger instance
	 */
	@Deprecated
	protected SMTrigger newExitOnTimer(Scheduler scheduler, Instant time, SMInput input) {
		return newTriggerOnTimer(scheduler, time, input);
	}
	
	protected SMTrigger newTriggerOnTimer(Scheduler scheduler, Instant time, SMInput input) {
		return new SMTriggerOnTimer(scheduler, time, input);
	}
	
	protected SMTrigger newExitOnTimer(Scheduler scheduler, Instant time, String exitID) {
		return newExitOnTimer(scheduler, time, registerInput(new SMInputStub(getExit(exitID))));
	}

}
