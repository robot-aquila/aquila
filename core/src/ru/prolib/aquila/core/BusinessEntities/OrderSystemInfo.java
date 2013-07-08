package ru.prolib.aquila.core.BusinessEntities;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Служебная информация заявки.
 */
public class OrderSystemInfo {
	private final Transaction registration, cancellation;
	
	public OrderSystemInfo() {
		super();
		registration = new Transaction();
		cancellation = new Transaction();
	}
	
	/**
	 * Получить информацию о транзакции регистрации заявки.
	 * <p>
	 * @return транзакция
	 */
	public Transaction getRegisteration() {
		return registration;
	}
	
	/**
	 * Получить информацию о транзакции отмены заявки.
	 * <p>
	 * @return транзакция
	 */
	public Transaction getCancellation() {
		return cancellation;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != OrderSystemInfo.class ) {
			return false;
		}
		OrderSystemInfo o = (OrderSystemInfo) other;
		return new EqualsBuilder()
			.append(o.registration, registration)
			.append(o.cancellation, cancellation)
			.isEquals();
	}

}
