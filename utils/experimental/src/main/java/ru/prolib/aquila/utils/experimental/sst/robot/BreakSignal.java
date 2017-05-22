package ru.prolib.aquila.utils.experimental.sst.robot;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;
import ru.prolib.aquila.core.SimpleEventFactory;

public class BreakSignal {
	public static final String DEFAULT_ID = "SIGNAL";
	protected final String id;
	protected final EventQueue queue;
	protected final EventType onBreak;

	public BreakSignal(EventQueue queue, String id) {
		this.id = id;
		this.queue = queue;
		this.onBreak = new EventTypeImpl(id + ".BREAK");
	}
	
	public BreakSignal(EventQueue queue) {
		this(queue, DEFAULT_ID);
	}
	
	public String getID() {
		return id;
	}

	public EventType onBreak() {
		return onBreak;
	}
	
	public void fireBreak() {
		queue.enqueue(onBreak, SimpleEventFactory.getInstance());
	}
	
}
