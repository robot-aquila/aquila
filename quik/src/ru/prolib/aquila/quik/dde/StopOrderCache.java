package ru.prolib.aquila.quik.dde;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Кеш строки таблицы стоп-заявок.
 */
public class StopOrderCache extends CacheEntry {
	private final Long id;
	private final Long transId;
	private final OrderStatus status;
	private final String secCode;
	private final String secClassCode;
	private final String accountCode;
	private final String clientCode;
	private final OrderDirection dir;
	private final Long qty;
	private final Double price;
	private final Double stopLimitPrice;
	private final Double takeProfitPrice;
	private final Price offset;
	private final Price spread;
	private final Long linkedOrderId;
	private final Date time;
	private final Date withdrawTime;
	private final OrderType type;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param id номер заявки
	 * @param transId номер транзакции (может быть null)
	 * @param status статус заявки
	 * @param secCode код инструмента
	 * @param secClassCode код класса инструмента
	 * @param accountCode код торгового счета
	 * @param clientCode код клиента
	 * @param dir направление заявки
	 * @param qty количество заявки
	 * @param price цена
	 * @param stopLimitPrice стоп-цена заявки
	 * @param takeProfitPrice тэйк-профит цена (может быть null для StopLimit)
	 * @param offset отступ от пиковой цены (может быть null)
	 * @param spread защитный спрэд (может быть null)
	 * @param linkedOrderId номер порожденной заявки (может быть null)
	 * @param time время регистрации стоп-заявки
	 * @param withdrawTime время отмены заявки (может быть null)
	 * @param type тип стоп-заявки
	 */
	public StopOrderCache(Long id, Long transId, OrderStatus status,
			String secCode, String secClassCode,
			String accountCode, String clientCode,
			OrderDirection dir, Long qty, Double price,
			Double stopLimitPrice, Double takeProfitPrice,
			Price offset, Price spread, Long linkedOrderId,
			Date time, Date withdrawTime, OrderType type)
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
		this.price = price;
		this.stopLimitPrice = stopLimitPrice;
		this.takeProfitPrice = takeProfitPrice;
		this.offset = offset;
		this.spread = spread;
		this.linkedOrderId = linkedOrderId;
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
	
	public Double getPrice() {
		return price;
	}
	
	public Double getStopLimitPrice() {
		return stopLimitPrice;
	}
	
	public Double getTakeProfitPrice() {
		return takeProfitPrice;
	}
	
	public Price getOffset() {
		return offset;
	}
	
	public Price getSpread() {
		return spread;
	}
	
	public Long getLinkedOrderId() {
		return linkedOrderId;
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
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null ) {
			return false;
		}
		if ( other.getClass() != StopOrderCache.class ) {
			return false;
		}
		StopOrderCache o = (StopOrderCache) other;
		return new EqualsBuilder()
			.append(o.accountCode, accountCode)
			.append(o.clientCode, clientCode)
			.append(o.dir, dir)
			.append(o.id, id)
			.append(o.linkedOrderId, linkedOrderId)
			.append(o.offset, offset)
			.append(o.price, price)
			.append(o.qty, qty)
			.append(o.secClassCode, secClassCode)
			.append(o.secCode, secCode)
			.append(o.spread, spread)
			.append(o.status, status)
			.append(o.stopLimitPrice, stopLimitPrice)
			.append(o.takeProfitPrice, takeProfitPrice)
			.append(o.time, time)
			.append(o.transId, transId)
			.append(o.type, type)
			.append(o.withdrawTime, withdrawTime)
			.isEquals();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "["
			+ "account=" + accountCode + ", "
			+ "clientCode=" + clientCode + ", "
			+ "dir=" + dir + ", "
			+ "linkedId=" + linkedOrderId + ", "
			+ "offset=" + offset + ", "
			+ "price=" + price + ", "
			+ "qty=" + qty + ", "
			+ "secCode=" + secCode + ", "
			+ "secClass=" + secClassCode + ", "
			+ "spread=" + spread + ", "
			+ "status=" + status + ", "
			+ "SLP=" + stopLimitPrice + ", "
			+ "TPP=" + takeProfitPrice + ", "
			+ "time=" + time + ", "
			+ "transId=" + transId + ", "
			+ "type=" + type + ", "
			+ "withdrawTime=" + withdrawTime
			+ "]";
	}

}
