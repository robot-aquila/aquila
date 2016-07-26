package ru.prolib.aquila.core;

public class SimpleEventFactory implements EventFactory {
	private static final SimpleEventFactory instance = new SimpleEventFactory();
	
	public static EventFactory getInstance() {
		return instance;
	}
	
	public SimpleEventFactory() {
		super();
	}

	@Override
	public Event produceEvent(EventType type) {
		return new EventImpl(type);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null | other.getClass() != SimpleEventFactory.class ) {
			return false;
		}
		return true;
	}

}
