package ru.prolib.aquila.core;

/**
 * Типовая реализация события.
 * <p>
 * 2012-04-13<br>
 * $Id: EventImpl.java 513 2013-02-11 01:17:18Z whirlwind $
 */
public class EventImpl implements Event {
	public static final String ID = "BasicEvent";
	private final EventType type;

	/**
	 * Конструктор
	 * @param type тип события
	 */
	public EventImpl(EventType type) {
		super();
		if ( type == null ) {
			throw new NullPointerException("Event type cannot be null");
		}
		this.type = type;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.Event#getType()
	 */
	@Override
	public EventType getType() {
		return type;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.Event#isType(ru.prolib.aquila.core.EventType)
	 */
	@Override
	public boolean isType(EventType type) {
		return this.type == type;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( this == other ) {
			return true;
		}
		if ( other != null && other.getClass() == EventImpl.class ) {
			EventImpl o = (EventImpl)other;
			return o.type == type;
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return type.toString() + "." + ID; 
	}

}
