package ru.prolib.aquila.core;

import java.util.Map;

/**
 * Интерфейс композитного собьытия.
 * <p>
 * 2012-04-28
 * $Id: CompositeEventType.java 219 2012-05-20 12:16:45Z whirlwind $
 */
@Deprecated
public interface CompositeEventType extends EventType {

	/**
	 * Получить копию текущего состояния.
	 * <p>
	 * @return копия текущего состояния
	 */
	public Map<EventType, Event> getCurrentState();

}