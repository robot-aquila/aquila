package ru.prolib.aquila.probe.timeline;

import ru.prolib.aquila.core.*;

/**
 * Диспетчер событий эмулятора хронологии.
 */
public class TLSimulationEventDispatcher {
	private static int lastId = 0;
	private final EventDispatcher dispatcher;
	private final EventType onRunning, onPaused, onFinished, onStepping;
	
	/**
	 * Генерировать идентификатор очередного объекта.
	 * <p>
	 * @return идентификатор
	 */
	static synchronized String getNextId() {
		return "Timeline" + (++ lastId);
	}
	
	/**
	 * Получить порядковый номер последнего объекта.
	 * <p>
	 * @return порядковый номер
	 */
	static synchronized int getLastId() {
		return lastId;
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param es система событий
	 */
	public TLSimulationEventDispatcher(EventSystem es) {
		super();
		dispatcher = es.createEventDispatcher(getNextId());
		onRunning = dispatcher.createType("Running");
		onPaused = dispatcher.createType("Paused");
		onFinished = dispatcher.createType("Finished");
		onStepping = dispatcher.createType("Stepping");
	}
	
	/**
	 * Получить тип события: симуляция в работе.
	 * <p>
	 * @return тип события
	 */
	public EventType OnRunning() {
		return onRunning;
	}

	/**
	 * Получить тип события: симуляция приостановлена.
	 * <p>
	 * @return тип события
	 */
	public EventType OnPaused() {
		return onPaused;
	}
	
	/**
	 * Получить тип события: симуляция завершена.
	 * <p>
	 * @return тип события
	 */
	public EventType OnFinished() {
		return onFinished;
	}
	
	/**
	 * Получить тип события: шаг симуляции выполнен.
	 * <p>
	 * @return тип события
	 */
	public EventType OnStepping() {
		return onStepping;
	}
	
	/**
	 * Генерировать событие: симуляция в работе.
	 */
	public void fireRunning() {
		dispatcher.dispatch(new EventImpl(onRunning));
	}
	
	/**
	 * Генерировать событие: симуляция приостановлена.
	 */
	public void firePaused() {
		dispatcher.dispatch(new EventImpl(onPaused));
	}
	
	/**
	 * Генерировать событие: симуляция завершена.
	 */
	public void fireFinished() {
		dispatcher.dispatch(new EventImpl(onFinished));
	}
	
	/**
	 * Генерировать событие: шаг cимуляции выполнен.
	 */
	public void fireStepping() {
		dispatcher.dispatch(new EventImpl(onStepping));
	}

}
