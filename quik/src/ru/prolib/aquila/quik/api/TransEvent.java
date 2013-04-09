package ru.prolib.aquila.quik.api;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.t2q.T2QTransStatus;

/**
 * Событие о поступлении ответа по транзакции.
 */
public class TransEvent extends EventImpl {
	private final T2QTransStatus status;
	private final long transId;
	private final Long orderId;
	private final String msg;

	public TransEvent(EventType type, T2QTransStatus status,
			long transId, Long orderId, String msg)
	{
		super(type);
		this.status = status;
		this.transId = transId;
		this.orderId = orderId;
		this.msg = msg;
	}
	
	/**
	 * Получить статус транзакции.
	 * <p>
	 * @return статус транзакции
	 */
	public T2QTransStatus getStatus() {
		return status;
	}
	
	/**
	 * Получить номер транзакции.
	 * <p>
	 * @return номер транзакции
	 */
	public long getTransId() {
		return transId;
	}
	
	/**
	 * Получить номер созданной заявки.
	 * <p>
	 * @return номер заявки или null, если транзакция не связана с заявкой
	 */
	public Long getOrderId() {
		return orderId;
	}
	
	/**
	 * Получить комментарий к транзакции.
	 * <p>
	 * @return комментарий
	 */
	public String getMessage() {
		return msg;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == TransEvent.class ) {
			TransEvent o = (TransEvent) other;
			return new EqualsBuilder()
				.append(getType(), o.getType())
				.append(status, o.status)
				.append(transId, o.transId)
				.append(orderId, o.orderId)
				.append(msg, o.msg)
				.isEquals();
		} else {
			return false;
		}
	}

}
