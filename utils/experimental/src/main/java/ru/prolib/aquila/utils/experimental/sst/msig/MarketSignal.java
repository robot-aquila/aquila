package ru.prolib.aquila.utils.experimental.sst.msig;

import java.time.Instant;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventFactory;
import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;

public class MarketSignal extends BreakSignal {
	protected final EventType onBullish, onBearish;

	public MarketSignal(EventQueue queue, String id) {
		super(queue, id);
		this.onBullish = new EventTypeImpl(id + ".BULLISH");
		this.onBearish = new EventTypeImpl(id + ".BEARISH");
		
	}
	
	public MarketSignal(EventQueue queue) {
		this(queue, DEFAULT_ID);
	}
	
	public EventType onBullish() {
		return onBullish;
	}
	
	public EventType onBearish() {
		return onBearish;
	}
	
	public void fireBullish(Instant time, Double price) {
		queue.enqueue(onBullish, new MarketSignalEventFactory(time, price));
	}
	
	public void fireBearish(Instant time, Double price) {
		queue.enqueue(onBearish, new MarketSignalEventFactory(time, price));
	}
	
	static class MarketSignalEventFactory implements EventFactory {
		private final Instant time;
		private final Double price;
		
		public MarketSignalEventFactory(Instant time, Double price) {
			this.time = time;
			this.price = price;
		}

		@Override
		public Event produceEvent(EventType type) {
			return new MarketSignalEvent(type, time, price);
		}
		
	}

}
