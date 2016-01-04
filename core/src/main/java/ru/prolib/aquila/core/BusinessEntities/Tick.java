package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Tick data entity.
 */
public class Tick {
	private final TickType type;
	private final Instant timestamp;
	private final double price;
	private final long size;
	private final double value;
	
	private Tick(TickType type, Instant timestamp, double price, long size, double value) {
		super();
		this.type = type;
		this.timestamp = timestamp;
		this.price = price;
		this.size = size;
		this.value = value;
	}
	
	public static Tick of(TickType type, Instant timestamp, double price,
			long size, double value)
	{
		return new Tick(type, timestamp, price, size, value);
	}
	
	public static Tick of(TickType type, Instant timestamp, double price,
			long size)
	{
		return new Tick(type, timestamp, price, size, 0);
	}
	
	public static Tick of(Instant timestamp, double price, long size) {
		return of(TickType.TICK, timestamp, price, size, 0);
	}
	
	public static Tick of(Instant timestamp, double price) {
		return of(TickType.TICK, timestamp, price, 0, 0);
	}
	
	@Deprecated
	public static Tick of(LocalDateTime time, double price, long size) {
		return of(TickType.TICK, time.toInstant(ZoneOffset.UTC), price, size);
	}
	
	@Deprecated
	public static Tick of(LocalDateTime time, double price) {
		return of(time, price, 0);
	}
	
	public TickType getType() {
		return type;
	}
	
	/**
	 * Get time.
	 * <p>
	 * @return timestamp
	 */
	public Instant getTime() {
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
		return type + "["
			+ (timestamp == null ? "" : timestamp + " ")
			+ price
			+ (size == 0 ? "" : "x" + size)
			+ (value == 0 ? "" : " " + value)
			+ "]";
	}
	

}
