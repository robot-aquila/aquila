package ru.prolib.aquila.qforts.impl;

import java.time.Instant;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.OrderStatus;

public class QFOrderStatusUpdate {
	protected OrderStatus initialStatus, finalStatus;
	protected Instant finalizationTime;
	protected String systemMessage;

	public QFOrderStatusUpdate() {
		
	}
	
	public QFOrderStatusUpdate setInitialStatus(OrderStatus value) {
		initialStatus = value;
		return this;
	}
	
	public QFOrderStatusUpdate setFinalStatus(OrderStatus value) {
		finalStatus = value;
		return this;
	}
	
	public QFOrderStatusUpdate setFinalizationTime(Instant value) {
		finalizationTime = value;
		return this;
	}
	
	public QFOrderStatusUpdate setSystemMessage(String value) {
		systemMessage = value;
		return this;
	}
	
	public OrderStatus getInitialStatus() {
		return initialStatus;
	}
	
	public OrderStatus getFinalStatus() {
		return finalStatus;
	}
	
	public Instant getFinalizationTime() {
		return finalizationTime;
	}
	
	public String getSystemMessage() {
		return systemMessage;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName()
				+ "[is=" + initialStatus
				+ " fs=" + finalStatus
				+ " ft=" + finalizationTime
				+ " sm=" + systemMessage + "]";
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != QFOrderStatusUpdate.class ) {
			return false;
		}
		QFOrderStatusUpdate o = (QFOrderStatusUpdate) other;
		return new EqualsBuilder()
			.append(o.initialStatus, initialStatus)
			.append(o.finalStatus, finalStatus)
			.append(o.finalizationTime, finalizationTime)
			.append(o.systemMessage, systemMessage)
			.isEquals();
	}
	
}
