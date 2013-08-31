package ru.prolib.aquila.core;

import java.util.LinkedHashMap;

/**
 * Композитное событие.
 * <p>
 * 2012-04-22<br>
 * $Id: CompositeEvent.java 219 2012-05-20 12:16:45Z whirlwind $
 */
@Deprecated
public class CompositeEvent extends EventImpl {
	private final LinkedHashMap<EventType, Event> state;

	/**
	 * Конструктор
	 * @param type тип события
	 * @param state состояние на момент события
	 */
	public CompositeEvent(EventType type,
			LinkedHashMap<EventType, Event> state)
	{
		super(type);
		if ( state == null ) {
			throw new NullPointerException("State cannot be null");
		}
		this.state = state;
	}
	
	/**
	 * Получить состояние на момент возникновения события
	 * <p>
	 * @return состояние  
	 */
	public LinkedHashMap<EventType, Event> getState() {
		return state;
	}

	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null ) {
			return false;
		}
		if ( other.getClass() == this.getClass() ) {
			CompositeEvent o = (CompositeEvent)other;
			return o.getType() == getType() &&
				   o.getState().equals(state);
		}
		return false;
	}

}
