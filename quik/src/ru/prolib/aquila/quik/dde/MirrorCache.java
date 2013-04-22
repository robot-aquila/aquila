package ru.prolib.aquila.quik.dde;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.EventType;

/**
 * Базовый кэш таблицы полного отражения.
 */
public class MirrorCache {
	protected final EventDispatcher dispatcher;
	protected final EventType onUpdate;

	public MirrorCache(EventDispatcher dispatcher, EventType onUpdate) {
		super();
		this.dispatcher = dispatcher;
		this.onUpdate = onUpdate;
	}

	/**
	 * Получить диспетчер событий.
	 * <p>
	 * @return диспетчер событий
	 */
	public EventDispatcher getEventDispatcher() {
		return dispatcher;
	}

	/**
	 * Получить тип события: при обновлении кэша.
	 * <p>
	 * @return тип события
	 */
	public EventType OnCacheUpdate() {
		return onUpdate;
	}

	/**
	 * Генерировать событие об обновлении кэша.
	 */
	public void fireUpdateCache() {
		dispatcher.dispatch(new EventImpl(onUpdate));
	}

}