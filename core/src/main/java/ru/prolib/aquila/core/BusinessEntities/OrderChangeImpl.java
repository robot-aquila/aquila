package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;
import java.util.Map;

public class OrderChangeImpl implements OrderChange {
	private final Map<Integer, Object> tokens;
	
	public OrderChangeImpl(Map<Integer, Object> tokens) {
		this.tokens = tokens;
	}

	@Override
	public boolean isStatusChanged() {
		return tokens.containsKey(OrderField.STATUS);
	}

	@Override
	public boolean isFinalized() {
		return isStatusChanged() && getStatus().isFinal();
	}

	@Override
	public OrderStatus getStatus() {
		return (OrderStatus) tokens.get(OrderField.STATUS);
	}

	@Override
	public Instant getDoneTime() {
		return (Instant) tokens.get(OrderField.TIME_DONE);
	}

	@Override
	public long getCurrentVolume() {
		return (long) tokens.get(OrderField.CURRENT_VOLUME);
	}

	@Override
	public double getExecutedValue() {
		return (double) tokens.get(OrderField.EXECUTED_VALUE);
	}

}
