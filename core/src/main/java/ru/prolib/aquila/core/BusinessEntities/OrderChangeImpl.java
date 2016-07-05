package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class OrderChangeImpl implements OrderChange {
	private boolean applied = false;
	private final EditableOrder order;
	private final Map<Integer, Object> tokens;
	private final OrderExecution execution;
	
	public OrderChangeImpl(EditableOrder order, Map<Integer, Object> tokens) {
		this(order, tokens, null);
	}
	
	public OrderChangeImpl(EditableOrder order, Map<Integer, Object> tokens,
			OrderExecution execution)
	{
		this.order = order;
		this.tokens = tokens;
		this.execution = execution;
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
	
	@Override
	public String getSystemMessage() {
		return (String) tokens.get(OrderField.SYSTEM_MESSAGE);
	}

	@Override
	public boolean isApplied() {
		return applied;
	}

	@Override
	public void apply() throws ContainerTransactionException {
		if ( applied ) {
			throw new ContainerTransactionException("Transaction already applied");
		}
		applied = true;
		try {
			if ( execution != null ) {
				order.addExecution(execution);
				order.update(tokens);
				order.fireExecution(execution);
			} else {
				order.update(tokens);
			}
		} catch ( OrderException e ) {
			throw new ContainerTransactionException(e);
		}
	}

	@Override
	public Order getOrder() {
		return order;
	}

	@Override
	public OrderExecution getExecution() {
		return execution;
	}
	
	public Map<Integer, Object> getTokens() {
		return tokens;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != OrderChangeImpl.class ) {
			return false;
		}
		OrderChangeImpl o = (OrderChangeImpl) other;
		return new EqualsBuilder()
			.append(applied, o.applied)
			.append(order, o.order)
			.append(tokens, o.tokens)
			.append(execution, o.execution)
			.isEquals();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[applied=" + applied
				+ " orderID=" + order.getID() + " tokens=" + tokens
				+ (execution == null ? "" : " " + execution) + "]";
	}

}
