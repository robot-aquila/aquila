package ru.prolib.aquila.ib.event;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.EventType;

/**
 * Событие: получена информация о смене статуса заявки.
 * <p>
 * @link <a href="http://www.interactivebrokers.com/en/software/api/apiguide/java/orderstatus.htm">orderStatus</a>
 * <p>
 * 2012-12-11<br>
 * $Id: IBEventOrderStatus.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBEventOrderStatus extends IBEventOrder {
	private final String status;
	private final int filled;
	private final int remaining;
	private final double avgFillPrice;
	private final int permId;
	private final int parentId;
	private final double lastFillPrice;
	private final int clientId;
	private final String whyHeld;

	/**
	 * Конструктор.
	 * <p>
	 * @param type тип события
	 * @param orderId номер заявки
	 * @param status статус заявки
	 * @param filled исполненное количество заявки
	 * @param remaining неисполненное количество заявки
	 * @param avgFillPrice средняя цена по исполненному количеству заявки
	 * @param permId служебный?
	 * @param parentId the parent order, used for bracket and trailing orders
	 * @param lastFillPrice цена последней сделки по заявке
	 * @param clientId идентификатор клиента
	 * @param whyHeld
	 */
	public IBEventOrderStatus(EventType type, int orderId, String status,
			int filled, int remaining, double avgFillPrice, int permId,
			int parentId, double lastFillPrice, int clientId, String whyHeld)
	{
		super(type, orderId);
		this.status = status;
		this.filled = filled;
		this.remaining = remaining;
		this.avgFillPrice = avgFillPrice;
		this.permId = permId;
		this.parentId = parentId;
		this.lastFillPrice = lastFillPrice;
		this.clientId = clientId;
		this.whyHeld = whyHeld;
	}
	
	/**
	 * Получить состояние короткой продажи???
	 * <p>
	 * This field is used to identify an order held when TWS is trying to
	 * locate shares for a short sell. The value used to indicate this is
	 * 'locate'.
	 * <p>
	 * @return состояние
	 */
	public String getWhyHeld() {
		return whyHeld;
	}
	
	/**
	 * Получить идентификатор клиента.
	 * <p>
	 * @return идентификатор клиента
	 */
	public int getClientId() {
		return clientId;
	}
	
	/**
	 * Получить цену последней сделки.
	 * <p>
	 * @return цена сделки
	 */
	public double getLastFillPrice() {
		return lastFillPrice;
	}
	
	/**
	 * Получить номер родительской заявки.
	 * <p>
	 * @return номер заявки
	 */
	public int getParentId() {
		return parentId;
	}
	
	/**
	 * Получить служебный.
	 * <p>
	 * @return служебный
	 */
	public int getPermId() {
		return permId;
	}
	
	/**
	 * Получить среднюю цену исполнения заявки.
	 * <p>
	 * @return средняя цена
	 */
	public double getAvgFillPrice() {
		return avgFillPrice;
	}
	
	/**
	 * Получить неисполненное количество заявки.
	 * <p>
	 * @return количество
	 */
	public int getRemaining() {
		return remaining;
	}
	
	/**
	 * Получить статус заявки.
	 * <p>
	 * @return статус
	 */
	public String getStatus() {
		return status;
	}
	
	/**
	 * Получить исполненное количество заявки.
	 * <p>
	 * @return количество
	 */
	public int getFilled() {
		return filled;
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == IBEventOrderStatus.class
			? fieldsEquals(other) : false;
	}
	
	protected boolean fieldsEquals(Object other) {
		IBEventOrderStatus o = (IBEventOrderStatus) other;
		return new EqualsBuilder()
			.appendSuper(super.fieldsEquals(other))
			.append(avgFillPrice, o.avgFillPrice)
			.append(clientId, o.clientId)
			.append(filled, o.filled)
			.append(lastFillPrice, o.lastFillPrice)
			.append(parentId, o.parentId)
			.append(permId, o.permId)
			.append(remaining, o.remaining)
			.append(status, o.status)
			.append(whyHeld, o.whyHeld)
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121211, 193057)
			.append(getType())
			.append(getOrderId())
			.append(status)
			.append(filled)
			.append(remaining)
			.append(avgFillPrice)
			.append(permId)
			.append(parentId)
			.append(lastFillPrice)
			.append(clientId)
			.append(whyHeld)
			.toHashCode();
	}

}
