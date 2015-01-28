package ru.prolib.aquila.core.fsm;

/**
 * Исключение КА при регистрации дубликата перехода.
 * <p>
 * Данное исключение возникает при попытке зарегистрировать в качестве условия
 * перехода тип выходного события, для которого переход уже зарегистрирован.
 */
public class FSMTransitionExistsException extends FSMException {
	private static final long serialVersionUID = -322743314198259576L;
	
	public FSMTransitionExistsException(FSMEventType exitEvent) {
		super("Transition already exists: " + exitEvent);
	}

}
