package ru.prolib.aquila.quik.dde;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Кэш строки таблицы собственных сделок.
 */
public class TradeCache {
	private final Long id;
	private final Date time;
	private final Long orderId;
	private final Double price;
	private final Long qty;
	private final Double volume;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param id номер сделки
	 * @param time время сделки
	 * @param orderId номер заявки
	 * @param price цена сделки
	 * @param qty количество
	 * @param volume объем сделки
	 */
	public TradeCache(Long id, Date time, Long orderId,
			Double price, Long qty, Double volume)
	{
		super();
		this.id = id;
		this.time = time;
		this.orderId = orderId;
		this.price = price;
		this.qty = qty;
		this.volume = volume;
	}
	
	public Long getId() {
		return id;
	}
	
	public Date getTime() {
		return time;
	}
	
	public Long getOrderId() {
		return orderId;
	}
	
	public Double getPrice() {
		return price;
	}
	
	public Long getQty() {
		return qty;
	}
	
	public Double getVolume() {
		return volume;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null ) {
			return false;
		}
		if ( other.getClass() != TradeCache.class ) {
			return false;
		}
		TradeCache o = (TradeCache) other;
		return new EqualsBuilder()
			.append(id, o.id)
			.append(time, o.time)
			.append(orderId, o.orderId)
			.append(price, o.price)
			.append(qty, o.qty)
			.append(volume, o.volume)
			.isEquals();
	}

}
