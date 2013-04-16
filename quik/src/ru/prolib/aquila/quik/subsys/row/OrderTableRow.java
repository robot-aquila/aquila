package ru.prolib.aquila.quik.subsys.row;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Строка таблицы заявок.
 */
public class OrderTableRow {
	private Account account;
	private OrderDirection dir;
	private Long id;
	private Double price;
	private Long qty;
	private Long rest;
	private SecurityDescriptor descr;
	private OrderStatus status;
	private Date time;
	private Long transId;
	private OrderType type;
	
	public OrderTableRow() {
		super();
	}
	
	public synchronized Long getId() {
		return id;
	}
	
	public synchronized void setId(Long id) {
		this.id = id;
	}
	
	public synchronized Long getTransId() {
		return transId;
	}
	
	public synchronized void setTransId(Long transId) {
		this.transId = transId;
	}
	
	public synchronized OrderStatus getStatus() {
		return status;
	}
	
	public synchronized void setStatus(OrderStatus status) {
		this.status = status;
	}
	
	public synchronized SecurityDescriptor getSecurityDescriptor() {
		return descr;
	}
	
	public synchronized void setSecurityDescriptor(SecurityDescriptor descr) {
		this.descr = descr;
	}
	
	public synchronized Account getAccount() {
		return account;
	}
	
	public synchronized void setAccount(Account account) {
		this.account = account;
	}
	
	public synchronized OrderDirection getDirection() {
		return dir;
	}
	
	public synchronized void setDirection(OrderDirection dir) {
		this.dir = dir;
	}
	
	public synchronized Long getQty() {
		return qty;
	}
	
	public synchronized void setQty(Long qty) {
		this.qty = qty;
	}
	
	public synchronized Long getQtyRest() {
		return rest;
	}
	
	public synchronized void setQtyRest(Long qty) {
		this.rest = qty;
	}
	
	public synchronized Double getPrice() {
		return price;
	}
	
	public synchronized void setPrice(Double price) {
		this.price = price;
	}
	
	public synchronized Date getTime() {
		return time;
	}
	
	public synchronized void setTime(Date time) {
		this.time = time;
	}
	
	public synchronized OrderType getType() {
		return type;
	}
	
	public synchronized void setType(OrderType type) {
		this.type = type;
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == OrderTableRow.class ) {
			OrderTableRow o = (OrderTableRow) other;
			return new EqualsBuilder()
				.append(account, o.account)
				.append(descr, o.descr)
				.append(dir, o.dir)
				.append(id, o.id)
				.append(price, o.price)
				.append(qty, o.qty)
				.append(rest, o.rest)
				.append(status, o.status)
				.append(time, o.time)
				.append(transId, o.transId)
				.append(type, o.type)
				.isEquals();
		} else {
			return false;
		}
	}

}
