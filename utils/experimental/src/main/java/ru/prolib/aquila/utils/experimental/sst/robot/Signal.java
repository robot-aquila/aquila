package ru.prolib.aquila.utils.experimental.sst.robot;

import java.time.Instant;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventFactory;
import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;
import ru.prolib.aquila.core.SimpleEventFactory;

public class Signal {
	private final EventQueue queue;
	private final EventType onBullish, onBearish, onBreak;

	public Signal(EventQueue queue) {
		this.queue = queue;
		this.onBullish = new EventTypeImpl("BULLISH");
		this.onBearish = new EventTypeImpl("BEARISH");
		this.onBreak = new EventTypeImpl("BREAK");
	}
	
	public EventType onBullish() {
		return onBullish;
	}
	
	public EventType onBearish() {
		return onBearish;
	}
	
	public EventType onBreak() {
		return onBreak;
	}
	
	public void fireBullish(Instant time, Double price) {
		queue.enqueue(onBullish, new SignalEventFactory(time, price));
	}
	
	public void fireBearish(Instant time, Double price) {
		queue.enqueue(onBearish, new SignalEventFactory(time, price));
	}
	
	public void fireBreak() {
		queue.enqueue(onBreak, SimpleEventFactory.getInstance());
	}
	
	static class SignalEventFactory implements EventFactory {
		private final Instant time;
		private final Double price;
		
		public SignalEventFactory(Instant time, Double price) {
			this.time = time;
			this.price = price;
		}

		@Override
		public Event produceEvent(EventType type) {
			return new SignalEvent(type, time, price);
		}
		
	}

}
