package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class OrderExecutionImpl implements OrderExecution {
	private final Terminal terminal;
	private final long id;
	private final String externalID;
	private final Symbol symbol;
	private final OrderAction action;
	private final long orderID;
	private final Instant time;
	private final double price;
	private final long volume;
	private final double value;
	
	public OrderExecutionImpl(Terminal terminal, long id, String externalID,
			Symbol symbol, OrderAction action, long orderID, Instant time,
			double price, long volume, double value)
	{
		this.terminal = terminal;
		this.id = id;
		this.externalID = externalID;
		this.symbol = symbol;
		this.action = action;
		this.orderID = orderID;
		this.time = time;
		this.price = price;
		this.volume = volume;
		this.value = value;
	}

	@Override
	public long getID() {
		return id;
	}

	@Override
	public String getExternalID() {
		return externalID;
	}

	@Override
	public long getOrderID() {
		return orderID;
	}

	@Override
	public Symbol getSymbol() {
		return symbol;
	}

	@Override
	public Instant getTime() {
		return time;
	}

	@Override
	public OrderAction getAction() {
		return action;
	}

	@Override
	public double getPricePerUnit() {
		return price;
	}

	@Override
	public long getVolume() {
		return volume;
	}

	@Override
	public double getValue() {
		return value;
	}

	@Override
	public Terminal getTerminal() {
		return terminal;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != OrderExecutionImpl.class ) {
			return false;
		}
		OrderExecutionImpl o = (OrderExecutionImpl) other;
		return new EqualsBuilder()
			.append(terminal, o.terminal)
			.append(id, o.id)
			.append(externalID, o.externalID)
			.append(symbol, o.symbol)
			.append(action, o.action)
			.append(orderID, o.orderID)
			.append(time, o.time)
			.append(price, o.price)
			.append(volume, o.volume)
			.append(value, o.value)
			.isEquals();
	}
	
	@Override
	public String toString() {
		return "OrderExecution["
				+ "id=" + id + ", "
				+ "externalID=" + externalID + ", "
				+ "orderID=" + orderID + ", "
				+ time + " "
				+ action + " "
				+ symbol + "@" + price + "x" + volume
				+ " value=" + value + "]";
	}

}
