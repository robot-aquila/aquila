package ru.prolib.aquila.core.sm;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.utils.KW;

/**
 * Автомат.
 * <p>
 */
public class SMStateMachine {
	private static final Logger logger;
	private static int lastId = 0;
	
	static {
		logger = LoggerFactory.getLogger(SMStateMachine.class);
	}

	private String id;
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
		id = getNextId();
		this.initialState = initialState;
		this.transitions = transitions;
	}
	
	/**
	 * Установить идентификатор.
	 * <p>
	 * Идентификатор автомата используется исключительно в отладочных целях
	 * как отличительный признак конкретного автомата в журнале. По-умолчанию в
	 * качестве идентификатора используется автоматически-сгенерированный
	 * идентификатор, который определяется в момент инстанцирования.
	 */
	public synchronized void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Получить идентификатор.
	 * <p>
	 * @return идентификатор
	 */
	public synchronized String getId() {
		return id;
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
		if ( logger.isDebugEnabled() ) {
			Object args[] = { id, currentState };
			logger.debug("{}: started from: {}", args);
		}
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
			triggers.removeAll();
			SMExitAction exitAction = currentState.getExitAction(); 
			if ( exitAction != null ) {
				exitAction.exit();
			}
			SMState pstate = currentState;
			currentState = transitions.get(new KW<SMExit>(exit));
			if ( logger.isDebugEnabled() ) {
				Object args[] = { id, pstate, exit, currentState };
				logger.debug("{}: transition: {}.{} -> {}", args);
			}
			if ( finished() ) {
				logger.debug("{}: finished", id);
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

	/**
	 * Генерировать очередной идентификатор объекта.
	 * <p>
	 * Служебный метод. 
	 * <p>
	 * @return идентификатор
	 */
	private static synchronized String getNextId() {
		return SMStateMachine.class.getSimpleName() + (lastId++);
	}

}
