package ru.prolib.aquila.core.sm;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.utils.KW;

/**
 * Автомат.
 */
public class SMStateMachine {
	private static final Logger logger;
	private static int lastId = 0;
	private boolean debug = false;
	private final CountDownLatch finished;
	
	static {
		logger = LoggerFactory.getLogger(SMStateMachine.class);
	}

	private String id;
	private final Map<KW<SMExit>, SMStateHandler> transitions;
	private final SMStateHandler initialState;
	private SMStateHandler currentState;
	private SMTriggerRegistry triggers;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param initialState начальное состояние
	 * @param transitions список переходов
	 */
	public SMStateMachine(SMStateHandler initialState, Map<KW<SMExit>,
			SMStateHandler> transitions)
	{
		super();
		id = getNextId();
		this.initialState = initialState;
		this.transitions = transitions;
		this.finished = new CountDownLatch(1);
	}
	
	/**
	 * Включить отладочные сообщения.
	 * <p>
	 * @param enabled true - включить, false - отключить
	 */
	public synchronized void setDebug(boolean enabled) {
		this.debug = enabled;
	}
	
	/**
	 * Установить идентификатор.
	 * <p>
	 * Идентификатор автомата используется исключительно в отладочных целях
	 * как отличительный признак конкретного автомата в журнале. По-умолчанию в
	 * качестве идентификатора используется автоматически-сгенерированный
	 * идентификатор, который определяется в момент инстанцирования.
	 * <p>
	 * @param id - identifier of the automat.
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
	public synchronized SMStateHandler getCurrentState() {
		return currentState;
	}
	
	/**
	 * Обработать входные данные.
	 * <p>
	 * @param input идентификатор входа текущего состояния
	 * @param data данные
	 * @throws SMStateMachineNotStartedException - TODO:
	 * @throws SMBadInputException - TODO:
	 * @throws SMTransitionNotExistsException - TODO:
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
		//if ( debug && logger.isDebugEnabled() ) {
		//	Object args[] = { id, currentState };
		//	logger.debug("{}: input for: {}", args); 
		//}
		doExit(input.input(data));
		//if ( debug && logger.isDebugEnabled() ) {
		//	Object args[] = { id, currentState };
		//	logger.debug("{}: input finished at: {}", args); 
		//}
	}
	
	/**
	 * Обработать входные данные.
	 * <p>
	 * Данный метод используется для состояний, у которых имеется только один
	 * вход для подачи данных.
	 * <p>
	 * @param data данные
	 * @throws SMStateMachineNotStartedException - TODO:
	 * @throws SMStateHasNoInputException - TODO:
	 * @throws SMAmbiguousInputException - TODO: 
	 * @throws SMTransitionNotExistsException - TODO:
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
		//if ( debug && logger.isDebugEnabled() ) {
		//	Object args[] = { id, currentState };
		//	logger.debug("{}: input (default) for: {}", args); 
		//}
		List<SMInput> list = currentState.getInputs();
		if ( list.size() == 0 ) {
			throw new SMStateHasNoInputException();
		}
		if ( list.size() > 1 ) {
			throw new SMAmbiguousInputException();
		}
		doExit(list.get(0).input(data));
		//if ( debug && logger.isDebugEnabled() ) {
		//	Object args[] = { id, currentState };
		//	logger.debug("{}: input (default) finished at: {}", args); 
		//}
	}
	
	/**
	 * Запустить автомат в работу.
	 * <p>
	 * @throws SMStateMachineAlreadyStartedException - TODO:
	 * @throws SMTransitionNotExistsException - TODO:
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
		if ( debug && logger.isDebugEnabled() ) {
			Object args[] = { id, currentState };
			logger.debug("{}: started from: {}", args);
		}
		if ( action != null ) {
			dbgEnterAction();
			doExit(action.enter(triggers));
		}
		if ( debug && logger.isDebugEnabled() ) {
			Object args[] = { id, currentState };
			logger.debug("{}: start procedure finished at: {}", args);
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
		return currentState == SMStateHandler.FINAL;
	}
	
	public void waitForFinish(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
		if ( ! finished.await(timeout, unit) ) {
			throw new TimeoutException();
		}
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
	 * @throws SMTransitionNotExistsException - TODO:
	 */
	private void doExit(SMExit exit) throws SMTransitionNotExistsException {
		if ( exit == null ) {
			return;
		}
		do {
			triggers.close();
			SMExitAction exitAction = currentState.getExitAction(); 
			if ( exitAction != null ) {
				dbgExitAction();
				exitAction.exit();
			}
			SMStateHandler pstate = currentState;
			currentState = transitions.get(new KW<SMExit>(exit));
			if ( debug && logger.isDebugEnabled() ) {
				Object args[] = { id, pstate, exit, currentState };
				logger.debug("{}: transition: {}.{} -> {}", args);
			}
			if ( finished() ) {
				if ( debug && logger.isDebugEnabled() ) {
					logger.debug("{}: finished", id);
				}
				finished.countDown();
				return;
			}
			if ( currentState == null ) {
				throw new SMTransitionNotExistsException();
			}
			
			Class<?> i_type = currentState.getIncomingDataType(), r_type = pstate.getResultDataType();
			if ( i_type != Void.class ) {
				if ( ! i_type.isAssignableFrom(r_type) ) {
					throw new SMRuntimeException(new StringBuilder()
						.append("Data types mismatch while passing data between states.")
						.append(" Machine ID: ").append(id)
						.append(" Transition: ").append(pstate).append(".").append(exit)
						.append(" -> ").append(currentState)
						.append(" Expected: ").append(i_type)
						.append(" Actual: ").append(r_type)
						.toString());
				}
				currentState.setIncomingData(pstate.getResultData());
			}
			
			SMEnterAction enterAction = currentState.getEnterAction();
			createTriggers();
			exit = null;
			if ( enterAction != null ) {
				dbgEnterAction();
				exit = enterAction.enter(triggers);
			}
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
	
	private void dbgEnterAction() {
		//if ( debug && logger.isDebugEnabled() ) {
		//	Object args[] = { id, currentState };
		//	logger.debug("{}: enter action: {}", args);
		//}
	}
	
	private void dbgExitAction() {
		//if ( debug && logger.isDebugEnabled() ) {
		//	Object args[] = { id, currentState };
		//	logger.debug("{}: exit action: {}", args);
		//}
	}

}
