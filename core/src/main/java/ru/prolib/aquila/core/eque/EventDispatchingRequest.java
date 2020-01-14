package ru.prolib.aquila.core.eque;

import ru.prolib.aquila.core.EventFactory;
import ru.prolib.aquila.core.EventType;

public class EventDispatchingRequest {
	public static final EventDispatchingRequest EXIT, FLUSH;
	
	static {
		EXIT = new EventDispatchingRequest(null, null);
		FLUSH = new EventDispatchingRequest(null, null);
	}
	
	public final EventType type;
	public final EventFactory factory;
	
	public EventDispatchingRequest(EventType type, EventFactory factory) {
		this.type = type;
		this.factory = factory;
	}
}