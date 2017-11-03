package ru.prolib.aquila.utils.experimental.sst.msig;

import java.time.Instant;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventFactory;
import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;

public class MarketSignal extends BreakSignal {
	protected final EventType onBullish, onBearish;
	private MarketSignalEvent lastEvent;

	public MarketSignal(EventQueue queue, String id) {
		super(queue, id);
		this.onBullish = new EventTypeImpl(id + ".BULLISH");
		this.onBearish = new EventTypeImpl(id + ".BEARISH");
	}
	
	public MarketSignal(EventQueue queue) {
		this(queue, DEFAULT_ID);
	}
	
	public MarketSignalEvent getLastEvent() {
		return lastEvent;
	}
	
	public EventType onBullish() {
		return onBullish;
	}
	
	public EventType onBearish() {
		return onBearish;
	}
	
	public void fireBullish(Instant time, Double price) {
		fire(onBullish, time, price, null);
	}
	
	public void fireBullish(Instant time, Long size) {
		fire(onBullish, time, null, size);
	}
	
	public void fireBullish(Instant time, Double price, Long size) {
		fire(onBullish, time, price, size);
	}
	
	public void fireBearish(Instant time, Double price) {
		fire(onBearish, time, price, null);
	}
	
	public void fireBearish(Instant time, Long size) {
		fire(onBearish, time, null, size);
	}
	
	public void fireBearish(Instant time, Double price, Long size) {
		fire(onBearish, time, price, size);
	}
	
	private void fire(EventType type, Instant time, Double price, Long size) {
		EventFactory eventFactory = new MarketSignalEventFactory(time, price, size);
		lastEvent = (MarketSignalEvent) eventFactory.produceEvent(type);
		queue.enqueue(type, eventFactory);
	}
	
	static class MarketSignalEventFactory implements EventFactory {
		private final Instant time;
		private final Double price;
		private final Long size;
		
		public MarketSignalEventFactory(Instant time, Double price, Long size) {
			this.time = time;
			this.price = price;
			this.size = size;
		}
		
		public MarketSignalEventFactory(Instant time, Double price) {
			this(time, price, null);
		}

		@Override
		public Event produceEvent(EventType type) {
			return new MarketSignalEvent(type, time, price, size);
		}
		
	}

}
