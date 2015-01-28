package ru.prolib.aquila.core.fsm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.utils.KW;

/**
 * Конечный автомат.
 */
public class FSMAutomat implements EventListener {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(FSMAutomat.class);
	}
	
	private final Set<KW<FSMStateActor>> actors;
	private final Map<KW<FSMEventType>, FSMStateActor> transitions;
	private FSMStateActor current;
	
	/**
	 * Конструктор.
	 */
	public FSMAutomat() {
		super();
		actors = new HashSet<KW<FSMStateActor>>();
		transitions = new HashMap<KW<FSMEventType>, FSMStateActor>();
	}
	
	/**
	 * Зарегистрировать переход в состояние.
	 * <p>
	 * @param type тип выходного события
	 * @param actor актор целевого состояния
	 * @throws FSMTransitionExistsException для этого события переход уже есть
	 */
	public synchronized
		void transit(FSMEventType type, FSMStateActor actor)
			throws FSMTransitionExistsException
	{
		if ( isTransitionRegistered(type) ) {
			throw new FSMTransitionExistsException(type);
		}
		transitions.put(new KW<FSMEventType>(type), actor);
		registerActor(actor);
		registerActor(type.getOwner());
	}
	
	/**
	 * Зарегистрировать переход в финальное состояние.
	 * <p>
	 * @param type тип выходного события
	 * @throws FSMTransitionExistsException для этого события переход уже есть
	 */
	public synchronized void transitExit(FSMEventType type)
		throws FSMTransitionExistsException
	{
		if ( isTransitionRegistered(type) ) {
			throw new FSMTransitionExistsException(type);
		}
		transitions.put(new KW<FSMEventType>(type), null);
		registerActor(type.getOwner());
	}
	
	/**
	 * Получить текущей актор состояния.
	 * <p>
	 * @return текущее состояние
	 */
	public synchronized FSMStateActor getCurrentState() {
		return current;
	}
	
	/**
	 * Начать выполнение конечного автомата.
	 * <p>
	 * Конечный автомат может быть запущен только из начального (финального)
	 * состояния. Запуск выполняется с любого зарегистрированного актора:
	 * акторы, которые фигурировали в качестве владельца выходного события
	 * или актора целевого состояния при регистрации перехода.
	 * <p> 
	 * В момент запуска выполняется валидация графа переходов. Для всех
	 * выходных событий всех задействованных акторов должны быть описаны
	 * переходы. Если хотя бы для одного выходного события нет перехода, то
	 * возбуждается исключение.
	 * <p>
	 * @param actor стартовый актор
	 * @throws IllegalStateException КА уже запущен или неполный граф переходов
	 * @throws IllegalArgumentException старт с незарегистрированного актора
	 */
	public synchronized void start(FSMStateActor actor) {
		if ( current != null ) {
			throw new IllegalStateException("FSM already started");
		}
		if ( ! isActorRegistered(actor) ) {
			throw new IllegalArgumentException("Unknown actor");
		}
		for ( KW<FSMStateActor> kActor : actors ) {
			validateExits(kActor.instance());
		}
		current = actor;
		actor.startListenExitEvents(this);
		actor.enter();
		logger.debug("Start {}", current);
	}
	
	/**
	 * Проверить наличие актора состояния.
	 * <p>
	 * Проверяет был ли задействован указанный актор в качестве владельца
	 * выходного события или актора целевого состояния при регистрации перехода.
	 * <p>
	 * @param actor актор состояния
	 * @return true актор присутствует в графе переходов, false отсутствует
	 */
	public synchronized boolean isActorRegistered(FSMStateActor actor) {
		return actors.contains(new KW<FSMStateActor>(actor));
	}
	
	/**
	 * Проверить наличие перехода для выходного события.
	 * <p>
	 * Проверяет был ли описан переход по выходному событию. 
	 * <p>
	 * @param type тип выходного события
	 * @return true переход для данного события существует, false не существует
	 */
	public synchronized boolean isTransitionRegistered(FSMEventType type) {
		return transitions.containsKey(new KW<FSMEventType>(type));
	}

	@Override
	public synchronized void onEvent(Event event) {
		if ( current == null ) {
			throw new IllegalStateException("FSM not started");
		}
		FSMEventType exit = (FSMEventType) event.getType();
		if ( ! isTransitionRegistered(exit) ) {
			throw new IllegalStateException("No transition for: " + exit);
		}
		current.stopListenExitEvents(this);
		current.exit();
		current = transitions.get(new KW<FSMEventType>(exit));
		Object args[] = { event.getType(), current };
		logger.debug("Transition {} => {}", args);
		if ( current != null ) {
			current.startListenExitEvents(this);
			current.enter();
		}
	}
	
	/**
	 * Зарегистрировать актор состояния.
	 * <p>
	 * @param actor актор (может быть null)
	 */
	private void registerActor(FSMStateActor actor) {
		if ( actor != null ) {
			actors.add(new KW<FSMStateActor>(actor));
		}
	}
	
	/**
	 * Проверить наличие переходов всех выходных событий актора состояния.
	 * <p>
	 * @param actor актор состояния
	 * @throws IllegalStateException неполный список переходов для актора
	 */
	private void validateExits(FSMStateActor actor) {
		for ( FSMEventType type : actor.getExitEvents() ) {
			if ( ! isTransitionRegistered(type) ) {
				throw new IllegalStateException("No transition for: " + type);
			}
		}
	}

}
