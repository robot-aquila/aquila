package ru.prolib.aquila.utils.experimental.sst.msig;

import java.time.Instant;

import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;

public class MarketSignalEvent extends EventImpl {
	private final Instant time;
	private final CDecimal price, size;

	public MarketSignalEvent(EventType type, Instant time, CDecimal price, CDecimal size) {
		super(type);
		this.time = time;
		this.price = price;
		this.size = size;
	}
	
	public Instant getTime() {
		return time;
	}
	
	public CDecimal getPrice() {
		return price;
	}
	
	public CDecimal getSize() {
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
