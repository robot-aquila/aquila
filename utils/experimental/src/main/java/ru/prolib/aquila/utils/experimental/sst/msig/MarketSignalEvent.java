package ru.prolib.aquila.utils.experimental.sst.msig;

import java.time.Instant;

import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.EventType;

public class MarketSignalEvent extends EventImpl {
	private final Instant time;
	private final Double price;
	private final Long size;

	public MarketSignalEvent(EventType type, Instant time, Double price, Long size) {
		super(type);
		this.time = time;
		this.price = price;
		this.size = size;
	}
	
	public Instant getTime() {
		return time;
	}
	
	public Double getPrice() {
		return price;
	}
	
	public Long getSize() {
		return size;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + getType().getId()
				+ " T:" + time
				+ " P:" + price
				+ " S:" + size
				+ "]";
	}

}
