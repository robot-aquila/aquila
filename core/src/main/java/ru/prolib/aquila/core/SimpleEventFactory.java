package ru.prolib.aquila.core;

public class SimpleEventFactory implements EventFactory {
	
	public SimpleEventFactory() {
		super();
	}

	@Override
	public Event produceEvent(EventType type) {
		return new EventImpl(type);
	}

}
