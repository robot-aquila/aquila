package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Tick data entity.
 */
public class Tick implements TStamped {
	public static final Tick NULL_ASK = Tick.of(TickType.ASK, 0, 0);
	public static final Tick NULL_BID = Tick.of(TickType.BID, 0, 0);
	
	private final TickType type;
	private final long timestamp;
	private final double price;
	private final long size;
	private final double value;
	
	private Tick(TickType type, long timestamp, double price, long size, double value) {
		super();
		this.type = type;
		this.timestamp = timestamp;
		this.price = price;
		this.size = size;
		this.value = value;
	}
	
	public static Tick of(TickType type, long timestamp, double price,
			long size, double value)
	{
		return new Tick(type, timestamp, price, size, value);
	}
	
	public static Tick of(TickType type, double price, long size) {
		return of(type, 0, price, size, 0);
	}
	
	public static Tick of(TickType type, Instant time, double price,
			long size, double value)
	{
		return new Tick(type, time.toEpochMilli(), price, size, value);
	}
	
	public static Tick of(TickType type, Instant time, double price,
			long size)
	{
		return new Tick(type, time.toEpochMilli(), price, size, 0);
	}
	
	public static Tick of(Instant time, double price, long size) {
		return of(TickType.TRADE, time, price, size, 0);
	}
	
	public static Tick of(Instant time, double price) {
		return of(TickType.TRADE, time, price, 0, 0);
	}
	
	@Deprecated
	public static Tick of(LocalDateTime time, double price, long size) {
		return of(TickType.TRADE, time.toInstant(ZoneOffset.UTC), price, size);
	}
	
	@Deprecated
	public static Tick of(LocalDateTime time, double price) {
		return of(time, price, 0);
	}
	
	public TickType getType() {
		return type;
	}
	
	/**
	 * Get time of tick.
	 * <p>
	 * @return time
	 */
	@Override
	public Instant getTime() {
		return Instant.ofEpochMilli(timestamp);
	}
	
	/**
	 * Get timestamp of tick.
	 * <p>
	 * @return milliseconds that have elapsed since 1970-01-01T00:00:00Z
	 */
	public long getTimestamp() {
		return timestamp;
	}
	
	/**
	 * Get price.
	 * <p>
	 * @return price
	 */
	public double getPrice() {
		return price;
	}
	
	/**
	 * Get size.
	 * <p>
	 * @return size
	 */
	public long getSize() {
		return size;
	}
	
	/**
	 * Get value.
	 * <p>
	 * @return value
	 */
	public double getValue() {
		return value;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == Tick.class ) {
			Tick o = (Tick) other;
			return new EqualsBuilder()
				.append(timestamp, o.timestamp)
				.append(price, o.price)
				.append(size, o.size)
				.append(value, o.value)
				.append(type, o.type)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return type + "[" + getTime() + " " + price
			+ (size == 0 ? "" : "x" + size)
			+ (value == 0 ? "" : " " + value) + "]";
	}
	
	public Tick withPrice(double newPrice) {
		return Tick.of(type, timestamp, newPrice, size, value);
	}

}
