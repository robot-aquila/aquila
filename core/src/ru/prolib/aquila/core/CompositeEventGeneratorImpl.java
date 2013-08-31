package ru.prolib.aquila.core;

import java.util.LinkedHashMap;

/**
 * Типовой генератор композитного события.
 * <p>
 * Генерирует событие типа {@link CompositeEvent}.
 * <p> 
 * 2012-04-29<br>
 * $Id: CompositeEventGeneratorImpl.java 219 2012-05-20 12:16:45Z whirlwind $
 */
@Deprecated
public class CompositeEventGeneratorImpl implements CompositeEventGenerator {
	
	/**
	 * Создать объект.
	 */
	public CompositeEventGeneratorImpl() {
		super();
	}

	@Override
	public Event generateEvent(CompositeEventType type,
							   LinkedHashMap<EventType, Event> state,
							   Event event)
	{
		return new CompositeEvent(type, state);
	}

}
