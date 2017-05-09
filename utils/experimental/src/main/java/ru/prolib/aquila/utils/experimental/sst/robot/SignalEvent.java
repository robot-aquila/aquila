package ru.prolib.aquila.utils.experimental.sst.robot;

import java.time.Instant;

import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.EventType;

public class SignalEvent extends EventImpl {
	private final Instant time;
	private final Double price;

	public SignalEvent(EventType type, Instant time, Double price) {
		super(type);
		this.time = time;
		this.price = price;
	}
	
	public Instant getTime() {
		return time;
	}
	
	public Double getPrice() {
		return price;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + getType().getId() + " T:" + time + " P:" + price + "]";
	}

}
