package ru.prolib.aquila.core.sm;

import java.util.List;
import java.util.Map;

import ru.prolib.aquila.core.utils.KW;

/**
 * Автомат.
 * <p>
 */
public class SMStateMachine {
	private final Map<KW<SMExit>, SMState> transitions;
	private final SMState initialState;
	private SMState currentState;
	private SMTriggerRegistry triggers;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param initialState начальное состояние
	 * @param transitions список переходов
	 */
	public SMStateMachine(SMState initialState, Map<KW<SMExit>,
			SMState> transitions)
	{
		super();
		this.initialState = initialState;
		this.transitions = transitions;
	}
	
	/**
	 * Получить текущее состояние.
	 * <p>
	 * @return текущее состояние автомата
	 */
	public synchronized SMState getCurrentState() {
		return currentState;
	}
	
	/**
	 * Обработать входные данные.
	 * <p>
	 * @param input идентификатор входа текущего состояния
	 * @param data данные
	 * @throws SMStateMachineNotStartedException
	 * @throws SMBadInputException
	 * @throws SMTransitionNotExistsException
	 */
	public synchronized void input(SMInput input, Object data) throws
		SMStateMachineNotStartedException,
		SMBadInputException,
		SMTransitionNotExistsException 
	{
		if ( ! started() ) {
			throw new SMStateMachineNotStartedException();
		}
		if ( input.getState() != currentState ) {
			throw new SMBadInputException();
		}
		_(input.input(data));
	}
	
	/**
	 * Обработать входные данные.
	 * <p>
	 * Данный метод используется для состояний, у которых имеется только один
	 * вход для подачи данных.
	 * <p>
	 * @param data данные
	 * @throws SMStateMachineNotStartedException
	 * @throws SMStateHasNoInputException
	 * @throws SMAmbiguousInputException
	 * @throws SMTransitionNotExistsException
	 */
	public synchronized void input(Object data) throws
		SMStateMachineNotStartedException,
		SMStateHasNoInputException,
		SMAmbiguousInputException,
		SMTransitionNotExistsException
	{
		if ( ! started() ) {
			throw new SMStateMachineNotStartedException();
		}
		List<SMInput> list = currentState.getInputs();
		if ( list.size() == 0 ) {
			throw new SMStateHasNoInputException();
		}
		if ( list.size() > 1 ) {
			throw new SMAmbiguousInputException();
		}
		_(list.get(0).input(data));
	}
	
	/**
	 * Запустить автомат в работу.
	 * <p>
	 * @throws SMStateMachineAlreadyStartedException 
	 * @throws SMTransitionNotExistsException 
	 */
	public synchronized void start() throws
		SMStateMachineAlreadyStartedException,
		SMTransitionNotExistsException
	{
		if ( started() ) {
			throw new SMStateMachineAlreadyStartedException();
		}
		currentState = initialState;
		SMEnterAction action = currentState.getEnterAction();
		createTriggers();
		if ( action != null ) {
			_(action.enter(triggers));
		}
	}
	
	/**
	 * Автомат в работе?
	 * <p>
	 * @return true - если автомат в работе, false - если нет
	 */
	public synchronized boolean started() {
		return currentState != null;
	}
	
	/**
	 * Автомат в финальном состоянии?
	 * <p>
	 * @return true - если автомат в финальном состоянии, false - если нет
	 */
	public synchronized boolean finished() {
		return currentState == SMState.FINAL;
	}
	
	/**
	 * Осуществить выход.
	 * <p>
	 * Если выход не определен, то ничего не делает. Если выход определен,
	 * то определяет целевое состояние и осуществляет процедуру перехода из
	 * текущего в целевое состояние. Если в результате отработки процедуры входа
	 * в целевое состояние будет получен дескриптор выхода, то осуществляется
	 * переход в следующее состояние. Цикл повторяется до тех пор, пока не будет
	 * достигнуто финальное состояние или пока вход в целевое состояние не
	 * завершится сохранением автомата в этом состоянии.
	 * <p>
	 * @param exit выход или null, если не нужно осуществлять переход
	 * @throws SMTransitionNotExistsException
	 */
	private void _(SMExit exit) throws SMTransitionNotExistsException {
		if ( exit == null ) {
			return;
		}
		do {
			triggers.deactivateAndRemoveAll();
			SMExitAction exitAction = currentState.getExitAction(); 
			if ( exitAction != null ) {
				exitAction.exit();
			}
			currentState = transitions.get(new KW<SMExit>(exit));
			if ( finished() ) {
				return;
			}
			if ( currentState == null ) {
				throw new SMTransitionNotExistsException();
			}
			SMEnterAction enterAction = currentState.getEnterAction();
			createTriggers();
			exit = enterAction == null ? null : enterAction.enter(triggers);
		} while ( exit != null );
	}
	
	/**
	 * Конструктор реестра триггеров.
	 * <p>
	 * Создает новый реестр для текущего состояния и устанавливает его в
	 * качестве текущего. Служебный метод.
	 * <p>
	 * @return текущий реестр триггеров
	 */
	private SMTriggerRegistry createTriggers() {
		return triggers = new SMTriggerRegistry(this, currentState);
	}

}
