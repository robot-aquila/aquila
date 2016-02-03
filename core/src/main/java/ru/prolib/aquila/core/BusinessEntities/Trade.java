package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Модель сделки.
 * <p>
 * 2012-06-01<br>
 * $Id: Trade.java 513 2013-02-11 01:17:18Z whirlwind $
 */
@Deprecated // TODO: rename to deal
public class Trade implements Comparable<Trade> {
	private volatile Terminal terminal;
	private volatile Long id;
	private volatile Symbol symbol;
	private volatile OrderAction direction;
	private volatile Instant time;
	private volatile double price;
	private volatile long qty;
	private volatile double volume;
	private volatile Long orderId;
	
	/**
	 * Создать экземпляр сделки.
	 * <p>
	 * @param terminal терминал, через который получена сделка
	 */
	public Trade(Terminal terminal) {
		super();
		this.terminal = terminal;
	}
	
	/**
	 * Конструктор.
	 */
	public Trade() {
		super();
	}
	
	/**
	 * Получить терминал сделки.
	 * <p>
	 * @return терминал или null, если сделка без связи с терминалом
	 */
	public Terminal getTerminal() {
		return terminal;
	}
	
	/**
	 * Связать сделку с терминалом.
	 * <p> 
	 * @param terminal экземпляр терминала
	 */
	public void setTerminal(Terminal terminal) {
		this.terminal = terminal;
	}
	
	/**
	 * Получить идентификатор сделки
	 * <p>
	 * @return идентификатор сделки или null, если идентификатор не определен
	 */
	public Long getId() {
		return id;
	}
	
	/**
	 * Установить идентификатор сделки.
	 * <p>
	 * @param id идентификатор сделки
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * Получить время сделки
	 * <p>
	 * @return время сделки или null, если время сделки не определено
	 */
	public Instant getTime() {
		return time;
	}
	
	/**
	 * Установить время сделки.
	 * <p>
	 * @param time время сделки
	 */
	public void setTime(Instant time) {
		this.time = time;
	}
	
	/**
	 * Получить направление сделки
	 * <p>
	 * @return направление сделки или null, если направление не определено
	 */
	public OrderAction getDirection() {
		return direction;
	}
	
	/**
	 * Установить направление сделки.
	 * <p>
	 * @param dir направление сделки
	 */
	public void setDirection(OrderAction dir) {
		this.direction = dir;
	}
	
	/**
	 * Получить цену сделки
	 * <p>
	 * @return цена сделки или null, если цена не определена
	 */
	public double getPrice() {
		return price;
	}
	
	/**
	 * Установить цену сделки.
	 * <p>
	 * @param price цена сделки
	 */
	public void setPrice(double price) {
		this.price = price;
	}
	
	/**
	 * Получить количество сделки
	 * <p>
	 * @return количество сделки или null, если количество не определено
	 */
	public long getQty() {
		return qty;
	}
	
	/**
	 * Установить количество сделки.
	 * <p>
	 * @param qty количество сделки
	 */
	public void setQty(long qty) {
		this.qty = qty;
	}
	
	/**
	 * Получить объем сделки.
	 * <p>
	 * @return объем сделки или null, если объем не определен
	 */
	public double getVolume() {
		return volume;
	}
	
	/**
	 * Установить объем сделки.
	 * <p>
	 * @param vol объем сделки
	 */
	public void setVolume(double vol) {
		volume = vol;
	}
	
	/**
	 * Получить номер заявки, которой принадлежит сделка.
	 * <p>
	 * @return номер заявки или null для анонимной сделки
	 */
	public Long getOrderId() {
		return orderId;
	}
	
	/**
	 * Установить номер заявки, которой принадлежит сделка.
	 * <p>
	 * @param orderId номер заявки
	 */
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	
	/**
	 * Получить инструмент, по которому совершена сделка
	 * <p>
	 * @return инструмент
	 * @throws SecurityException - TODO:
	 */
	public Security getSecurity() throws SecurityException {
		return terminal.getSecurity(symbol);
	}
	
	/**
	 * Установить дескриптор инструмента.
	 * <p>
	 * @param symbol дескриптор
	 */
	public void setSymbol(Symbol symbol) {
		this.symbol = symbol;
	}
	
	/**
	 * Получить дескриптор инструмента.
	 * <p>
	 * @return дескриптор инструмента
	 */
	public Symbol getSymbol() {
		return symbol;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == Trade.class ) {
			Trade o = (Trade) other;
			return new EqualsBuilder()
				.append(id, o.id)
				.append(direction, o.direction)
				.append(symbol, o.symbol)
				.append(time, o.time)
				.append(price, o.price)
				.append(qty, o.qty)
				.append(volume, o.volume)
				.append(orderId, o.orderId)
				.append(terminal, o.terminal)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return "Trade: "
		 	+ time
			+ " #" + id + " " +  direction + " "
			+ symbol + " " + qty + "x" + price
			+ " Vol=" + volume;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121031, 120517)
			.append(id)
			.append(symbol)
			.append(direction)
			.append(time)
			.append(price)
			.append(qty)
			.append(volume)
			.append(orderId)
			.append(terminal)
			.toHashCode();
	}

	@Override
	public int compareTo(Trade o) {
		if ( o == null ) {
			return 1;
		}
		int res = id.compareTo(o.id);
		if ( res != 0 ) return res;
		res = time.compareTo(o.time);
		return res;
	}

}
