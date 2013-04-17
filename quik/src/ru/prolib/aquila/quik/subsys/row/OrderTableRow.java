package ru.prolib.aquila.quik.subsys.row;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Строка таблицы заявок.
 */
public class OrderTableRow {
	private final Account account;
	private final OrderDirection dir;
	private final Long id;
	private final Double price;
	private final Long qty;
	private final Long rest;
	private final SecurityDescriptor descr;
	private final OrderStatus status;
	private final Date time;
	private final Long transId;
	private final OrderType type;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param id номер заявки
	 * @param transId номер транзакции
	 * @param account торговый счет
	 * @param time время заявки
	 * @param dir направление
	 * @param descr дескриптор инструмента
	 * @param qty количество
	 * @param price цена
	 * @param qtyRest неисполненный остаток заявки
	 * @param status статус
	 * @param type тип заявки
	 */
	public OrderTableRow(Long id, Long transId, Account account,
			Date time,  OrderDirection dir, SecurityDescriptor descr,
			Long qty, Double price, Long qtyRest,
			OrderStatus status, OrderType type)
	{
		super();
		this.id = id;
		this.transId = transId;
		this.account = account;
		this.time = time;
		this.dir = dir;
		this.descr = descr;
		this.qty = qty;
		this.price = price;
		this.rest = qtyRest;
		this.status = status;
		this.type = type;
	}
	
	public Long getId() {
		return id;
	}
	
	public Long getTransId() {
		return transId;
	}
	
	public OrderStatus getStatus() {
		return status;
	}
	
	public SecurityDescriptor getSecurityDescriptor() {
		return descr;
	}
	
	public Account getAccount() {
		return account;
	}
	
	public OrderDirection getDirection() {
		return dir;
	}
	
	public Long getQty() {
		return qty;
	}
	
	public Long getQtyRest() {
		return rest;
	}
	
	public Double getPrice() {
		return price;
	}
	
	public Date getTime() {
		return time;
	}
	
	public OrderType getType() {
		return type;
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
