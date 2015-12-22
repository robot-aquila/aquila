package ru.prolib.aquila.probe.timeline;

import ru.prolib.aquila.core.*;

/**
 * Диспетчер событий эмулятора хронологии.
 */
public class TLSEventDispatcher {
	private static int lastId = 0;
	private final EventDispatcher dispatcher;
	private final EventType onRun, onPause, onFinish;
	
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
	public TLSEventDispatcher(EventSystem es) {
		super();
		dispatcher = es.createEventDispatcher(getNextId());
		onRun = dispatcher.createType("Run");
		onPause = dispatcher.createType("Pause");
		onFinish = dispatcher.createType("Finish");
	}
	
	/**
	 * Получить тип события: эмуляция продолжена.
	 * <p>
	 * @return тип события
	 */
	public EventType OnRun() {
		return onRun;
	}

	/**
	 * Получить тип события: эмуляция приостановлена.
	 * <p>
	 * @return тип события
	 */
	public EventType OnPause() {
		return onPause;
	}
	
	/**
	 * Получить тип события: эмуляция завершена.
	 * <p>
	 * @return тип события
	 */
	public EventType OnFinish() {
		return onFinish;
	}
	
	/**
	 * Генерировать событие: эмуляция продолжена.
	 */
	public void fireRun() {
		dispatcher.dispatch(new EventImpl(onRun));
	}
	
	/**
	 * Генерировать событие: эмуляция приостановлена.
	 */
	public void firePause() {
		dispatcher.dispatch(new EventImpl(onPause));
	}
	
	/**
	 * Генерировать событие: эмуляция завершена.
	 */
	public void fireFinish() {
		dispatcher.dispatch(new EventImpl(onFinish));
	}


}
