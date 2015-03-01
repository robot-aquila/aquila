package ru.prolib.aquila.core;

/**
 * Типовая реализация события.
 * <p>
 * 2012-04-13<br>
 * $Id: EventImpl.java 513 2013-02-11 01:17:18Z whirlwind $
 */
public class EventImpl implements EventSI {
	public static final String ID = "BasicEvent";
	private final EventTypeSI type;

	/**
	 * Конструктор
	 * @param type тип события
	 */
	public EventImpl(EventTypeSI type) {
		super();
		this.type = type;
	}
	
	@Override
	public EventType getType() {
		return type;
	}
	
	@Override
	public EventTypeSI getTypeSI() {
		return type;
	}
	
	@Override
	public boolean isType(EventType type) {
		return this.type == type;
	}
	
	@Override
	public String toString() {
		return type.toString() + "." + ID; 
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != EventImpl.class ) {
			return false;
		}
		return type == ((EventImpl) other).type;
	}

}
