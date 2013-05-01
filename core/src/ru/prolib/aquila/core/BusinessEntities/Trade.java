package ru.prolib.aquila.core.BusinessEntities;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Модель сделки.
 * <p>
 * 2012-06-01<br>
 * $Id: Trade.java 513 2013-02-11 01:17:18Z whirlwind $
 */
public class Trade {
	private final static SimpleDateFormat df;
	
	static {
		df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}
	
	private final Terminal terminal;
	private Long id;
	private SecurityDescriptor descr;
	private OrderDirection direction;
	private Date time;
	private Double price;
	private Long qty;
	private Double volume;
	private Long orderId;
	
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
	 * Получить терминал сделки.
	 * <p>
	 * @return терминал
	 */
	public Terminal getTerminal() {
		return terminal;
	}
	
	/**
	 * Получить идентификатор сделки
	 * <p>
	 * @return идентификатор сделки
	 */
	public synchronized Long getId() {
		return id;
	}
	
	public synchronized void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * Получить время сделки
	 * <p>
	 * @return время сделки
	 */
	public synchronized Date getTime() {
		return time;
	}
	
	public synchronized void setTime(Date time) {
		this.time = time;
	}
	
	/**
	 * Получить направление сделки
	 * <p>
	 * @return направление сделки
	 */
	public synchronized OrderDirection getDirection() {
		return direction;
	}
	
	public synchronized void setDirection(OrderDirection dir) {
		this.direction = dir;
	}
	
	/**
	 * Получить цену сделки
	 * <p>
	 * @return цена сделки
	 */
	public synchronized Double getPrice() {
		return price;
	}
	
	public synchronized void setPrice(Double price) {
		this.price = price;
	}
	
	/**
	 * Получить количество сделки
	 * <p>
	 * @return количество сделки
	 */
	public synchronized Long getQty() {
		return qty;
	}
	
	public synchronized void setQty(Long qty) {
		this.qty = qty;
	}
	
	/**
	 * Получить объем сделки.
	 * <p>
	 * @return объем
	 */
	public synchronized Double getVolume() {
		return volume;
	}
	
	public synchronized void setVolume(Double vol) {
		volume = vol;
	}
	
	/**
	 * Получить номер заявки, которой принадлежит сделка.
	 * <p>
	 * @return номер заявки или null для анонимной сделки
	 */
	public synchronized Long getOrderId() {
		return orderId;
	}
	
	/**
	 * Установить номер заявки, которой принадлежит сделка.
	 * <p>
	 * @param orderId номер заявки
	 */
	public synchronized void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	
	/**
	 * Получить инструмент, по которому совершена сделка
	 * <p>
	 * @return инструмент
	 */
	public synchronized Security getSecurity() throws SecurityException {
		return terminal.getSecurity(descr);
	}
	
	public synchronized void setSecurityDescriptor(SecurityDescriptor descr) {
		this.descr = descr;
	}
	
	/**
	 * Получить дескриптор инструмента.
	 * <p>
	 * @return дескриптор инструмента
	 */
	public synchronized SecurityDescriptor getSecurityDescriptor() {
		return descr;
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == Trade.class ) {
			Trade o = (Trade) other;
			return new EqualsBuilder()
				.append(id, o.id)
				.append(direction, o.direction)
				.append(descr, o.descr)
				.append(time, o.time)
				.append(price, o.price)
				.append(qty, o.qty)
				.append(volume, o.volume)
				.append(orderId, o.orderId)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public synchronized String toString() {
		return "Trade: "
		 	+ (time == null ? time : df.format(time))
			+ " #" + id + " " +  direction + " "
			+ descr + " " + qty + "x" + price
			+ " Vol=" + volume;
	}
	
	@Override
	public synchronized int hashCode() {
		return new HashCodeBuilder(20121031, 120517)
			.append(id)
			.append(descr)
			.append(direction)
			.append(time)
			.append(price)
			.append(qty)
			.append(volume)
			.append(orderId)
			.toHashCode();
	}

}
