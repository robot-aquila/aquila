package ru.prolib.aquila.core;

import java.util.Vector;

public class EventListenerStub implements EventListener {
	private Vector<Event> events;
	
	public EventListenerStub() {
		super();
		events = new Vector<Event>();
	}

	@Override
	public synchronized void onEvent(Event event) {
		events.add(event);
	}
	
	public synchronized int getEventCount() {
		return events.size();
	}
	
	public synchronized Event getEvent(int index) {
		return events.get(index);
	}
	
	public synchronized void clear() {
		events.clear();
	}

}
