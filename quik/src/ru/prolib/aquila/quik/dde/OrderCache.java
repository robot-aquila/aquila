package ru.prolib.aquila.quik.dde;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Кеш строки таблицы заявок.
 */
public class OrderCache {
	private final Long id;
	private final Long transId;
	private final OrderStatus status;
	private final String secCode, secClassCode;
	private final String accountCode, clientCode;
	private final OrderDirection dir;
	private final Long qty;
	private final Long qtyRest;
	private final Double price;
	private final Date time;
	private final Date withdrawTime;
	private final OrderType type;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param id номер заявки
	 * @param transId номер транзакции
	 * @param status статус
	 * @param secCode код инструмента
	 * @param secClassCode код класса инструмента
	 * @param accountCode код торгового счета
	 * @param clientCode код клиента
	 * @param dir направление
	 * @param qty количество заявки
	 * @param qtyRest неисполненный остаток заявки
	 * @param price цена
	 * @param time время заявки
	 * @param withdrawTime время отмены заявки (может быть null)
	 * @param type тип заявки
	 */
	public OrderCache(Long id, Long transId, OrderStatus status,
			String secCode, String secClassCode,
			String accountCode, String clientCode,
			OrderDirection dir, Long qty, Long qtyRest, 
			Double price, Date time, Date withdrawTime, OrderType type)
	{
		super();
		this.id = id;
		this.transId = transId;
		this.status = status;
		this.secCode = secCode;
		this.secClassCode = secClassCode;
		this.accountCode = accountCode;
		this.clientCode = clientCode;
		this.dir = dir;
		this.qty = qty;
		this.qtyRest = qtyRest;
		this.price = price;
		this.time = time;
		this.withdrawTime = withdrawTime;
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
	
	public String getSecurityCode() {
		return secCode;
	}
	
	public String getSecurityClassCode() {
		return secClassCode;
	}
	
	public String getAccountCode() {
		return accountCode;
	}
	
	public String getClientCode() {
		return clientCode;
	}
	
	public OrderDirection getDirection() {
		return dir;
	}
	
	public Long getQty() {
		return qty;
	}
	
	public Long getQtyRest() {
		return qtyRest;
	}
	
	public Double getPrice() {
		return price;
	}
	
	public Date getTime() {
		return time;
	}
	
	public Date getWithdrawTime() {
		return withdrawTime;
	}
	
	public OrderType getType() {
		return type;
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == OrderCache.class ) {
			OrderCache o = (OrderCache) other;
			return new EqualsBuilder()
				.append(id, o.id)
				.append(transId, o.transId)
				.append(status, o.status)
				.append(secCode, o.secCode)
				.append(secClassCode, o.secClassCode)
				.append(accountCode, o.accountCode)
				.append(clientCode, o.clientCode)
				.append(dir, o.dir)
				.append(qty, o.qty)
				.append(qtyRest, o.qtyRest)
				.append(price, o.price)
				.append(time, o.time)
				.append(withdrawTime, o.withdrawTime)
				.append(type, o.type)
				.isEquals();
		} else {
			return false;
		}
	}

}
