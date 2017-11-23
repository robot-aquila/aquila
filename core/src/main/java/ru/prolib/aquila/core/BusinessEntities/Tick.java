package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Tick data entity.
 */
public class Tick implements TStamped {
	@Deprecated
	public static final Tick NULL_ASK = Tick.of(TickType.ASK, CDecimalBD.ZERO, CDecimalBD.ZERO);
	@Deprecated
	public static final Tick NULL_BID = Tick.of(TickType.BID, CDecimalBD.ZERO, CDecimalBD.ZERO);
	
	private final TickType type;
	private final Instant time;
	private final CDecimal price;
	private final CDecimal size;
	private final CDecimal value;
	
	/**
	 * Constructor.
	 * <p>
	 * @param type - tick type
	 * @param time - time of tick
	 * @param price - price
	 * @param size - size of tick
	 * @param value - tick value (for example in account currency)
	 */
	public Tick(TickType type, Instant time, CDecimal price, CDecimal size, CDecimal value) {
		super();
		this.type = type;
		this.time = time;
		this.price = price;
		this.size = size;
		this.value = value;
	}

	@Deprecated
	public static CDecimal getPrice(Double price, int scale) {
		return price==null?null:CDecimalBD.of(Double.toString(price)).withScale(scale);
	}
	
	@Deprecated
	public static CDecimal getSize(Long size) {
		return size==null?null:CDecimalBD.of(size);
	}

	@Deprecated
	public static CDecimal getPrice(Tick tick, int scale) {
		return tick.price;
	}
	
	@Deprecated
	public static CDecimal getSize(Tick tick) {
		return tick.size;
	}
	
	public static Tick of(TickType type, Instant time, CDecimal price,
			CDecimal size, CDecimal value)
	{
		return new Tick(type, time, price, size, value);
	}
	
	public static Tick of(TickType type, CDecimal price, CDecimal size) {
		return of(type, Instant.EPOCH, price, size, CDecimalBD.ZERO);
	}
	
	public static Tick of(TickType type, Instant time, CDecimal price,
			CDecimal size)
	{
		return new Tick(type, time, price, size, CDecimalBD.ZERO);
	}
	
	public static Tick of(Instant time, CDecimal price, CDecimal size) {
		return of(TickType.TRADE, time, price, size, CDecimalBD.ZERO);
	}
	
	public static Tick of(Instant time, CDecimal price) {
		return of(TickType.TRADE, time, price, CDecimalBD.ZERO, CDecimalBD.ZERO);
	}
	
	public static Tick of(TickType type, Instant time, String price, long size) {
		return of(type, time, CDecimalBD.of(price), CDecimalBD.of(size));
	}
	
	public static Tick ofAsk(Instant time, CDecimal price, CDecimal size) {
		return of(TickType.ASK, time, price, size);
	}
	
	public static Tick ofAsk(CDecimal price, CDecimal size) {
		return ofAsk(Instant.EPOCH, price, size);
	}
	
	public static Tick ofAsk(Instant time, String price, long size) {
		return of(TickType.ASK, time, price, size);
	}
	
	public static Tick ofBid(Instant time, CDecimal price, CDecimal size) {
		return of(TickType.BID, time, price, size);
	}
	
	public static Tick ofBid(CDecimal price, CDecimal size) {
		return ofBid(Instant.EPOCH, price, size);
	}
	
	public static Tick ofBid(Instant time, String price, long size) {
		return of(TickType.BID, time, price, size);
	}
	
	public static Tick ofTrade(Instant time, CDecimal price, CDecimal size) {
		return of(TickType.TRADE, time, price, size);
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
		return time;
	}
	
	/**
	 * Get price.
	 * <p>
	 * @return price
	 */
	public CDecimal getPrice() {
		return price;
	}
	
	/**
	 * Get size.
	 * <p>
	 * @return size
	 */
	public CDecimal getSize() {
		return size;
	}
	
	/**
	 * Get value.
	 * <p>
	 * @return value
	 */
	public CDecimal getValue() {
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
				.append(time, o.time)
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
			+ (size == null ? "" : "x" + size)
			+ (value == null ? "" : " " + value) + "]";
	}
	
	public Tick withPrice(CDecimal newPrice) {
		return Tick.of(type, time, newPrice, size, value);
	}
	
	public Tick withTime(Instant newTime) {
		return Tick.of(type, newTime, price, size, value);
	}

}
