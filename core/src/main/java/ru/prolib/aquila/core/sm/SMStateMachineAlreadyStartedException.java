package ru.prolib.aquila.core.sm;

/**
 * Автомат уже запущен в работу.
 */
public class SMStateMachineAlreadyStartedException extends SMException {
	private static final long serialVersionUID = -6649442488090899335L;
	
	public SMStateMachineAlreadyStartedException() {
		super();
	}

}
