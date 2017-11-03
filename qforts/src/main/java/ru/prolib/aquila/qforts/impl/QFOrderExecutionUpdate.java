package ru.prolib.aquila.qforts.impl;

import java.time.Instant;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.BusinessEntities.OrderStatus;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.utils.CDValueTriplet;

public class QFOrderExecutionUpdate {
	private static final String RUB = "RUB";
	protected final CDValueTriplet currentVolume, executedValue;
	protected OrderAction executionAction;
	protected long executionOrderID;
	protected CDecimal executionPrice, executionVolume, executionValue;
	protected Symbol executionSymbol;
	protected Instant executionTime;
	protected Instant finalizationTime;
	protected OrderStatus finalStatus, initialStatus;
	
	public QFOrderExecutionUpdate() {
		currentVolume = new CDValueTriplet(0);
		executedValue = new CDValueTriplet(2, RUB);
	}
	
	public QFOrderExecutionUpdate setChangeCurrentVolume(CDecimal value) {
		currentVolume.setChangeValue(value);
		return this;
	}
	
	public QFOrderExecutionUpdate setChangeExecutedValue(CDecimal value) {
		executedValue.setChangeValue(value);
		return this;
	}
	
	public QFOrderExecutionUpdate setFinalCurrentVolume(CDecimal value) {
		currentVolume.setFinalValue(value);
		return this;
	}
	
	public QFOrderExecutionUpdate setFinalExecutedValue(CDecimal value) {
		executedValue.setFinalValue(value);
		return this;
	}
	
	public QFOrderExecutionUpdate setFinalStatus(OrderStatus value) {
		finalStatus = value;
		return this;
	}
	
	public QFOrderExecutionUpdate setFinalizationTime(Instant value) {
		finalizationTime = value;
		return this;
	}
	
	public QFOrderExecutionUpdate setInitialCurrentVolume(CDecimal value) {
		currentVolume.setInitialValue(value);
		return this;
	}
	
	public QFOrderExecutionUpdate setInitialExecutedValue(CDecimal value) {
		executedValue.setInitialValue(value);
		return this;
	}
	
	public QFOrderExecutionUpdate setInitialStatus(OrderStatus value) {
		initialStatus = value;
		return this;
	}
	
	public QFOrderExecutionUpdate setExecutionOrderID(long value) {
		executionOrderID = value;
		return this;
	}
	
	public QFOrderExecutionUpdate setExecutionSymbol(Symbol symbol) {
		executionSymbol = symbol;
		return this;
	}
	
	public QFOrderExecutionUpdate setExecutionAction(OrderAction value) {
		executionAction = value;
		return this;
	}
	
	public QFOrderExecutionUpdate setExecutionTime(Instant value) {
		executionTime = value;
		return this;
	}
	
	public QFOrderExecutionUpdate setExecutionPrice(CDecimal value) {
		executionPrice = value;
		return this;
	}
	
	public QFOrderExecutionUpdate setExecutionVolume(CDecimal value) {
		executionVolume = value;
		return this;
	}
	
	public QFOrderExecutionUpdate setExecutionValue(CDecimal value) {
		executionValue = value;
		return this;
	}
	
	public CDecimal getChangeCurrentVolume() {
		return currentVolume.getChangeValue();
	}
	
	public CDecimal getChangeExecutedValue() {
		return executedValue.getChangeValue();
	}
	
	public CDecimal getInitialCurrentVolume() {
		return currentVolume.getInitialValue();
	}
	
	public CDecimal getInitialExecutedValue() {
		return executedValue.getInitialValue();
	}
	
	public OrderStatus getInitialStatus() {
		return initialStatus;
	}
	
	public CDecimal getFinalCurrentVolume() {
		return currentVolume.getFinalValue();
	}
	
	public CDecimal getFinalExecutedValue() {
		return executedValue.getFinalValue();
	}
	
	public OrderStatus getFinalStatus() {
		return finalStatus;
	}
	
	public Instant getFinalizationTime() {
		return finalizationTime;
	}
	
	public long getExecutionOrderID() {
		return executionOrderID;
	}

	public Symbol getExecutionSymbol() {
		return executionSymbol;
	}
	
	public OrderAction getExecutionAction() {
		return executionAction;
	}
	
	public Instant getExecutionTime() {
		return executionTime;
	}
	
	public CDecimal getExecutionPrice() {
		return executionPrice;
	}
	
	public CDecimal getExecutionVolume() {
		return executionVolume;
	}
	
	public CDecimal getExecutionValue() {
		return executionValue;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "["
			+ "exOrd=" + executionOrderID + " "
			+ "exTm=" + executionTime + " "
			+ "exAct=" + executionAction + " "
			+ "exSym=" + executionSymbol + " "
			+ "exPr=" + executionPrice + " "
			+ "exVol=" + executionVolume + " "
			+ "exVal=" + executionValue + " "
			+ "currentVolume" + currentVolume + " "
			+ "executedValue" + executedValue + " "
			+ "finTime=" + finalizationTime + " "
			+ "status[i=" + initialStatus + " f=" + finalStatus + "]"
			+ "]";
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != QFOrderExecutionUpdate.class ) {
			return false;
		}
		QFOrderExecutionUpdate o = (QFOrderExecutionUpdate) other;
		return new EqualsBuilder()
			.append(o.executionOrderID, executionOrderID)
			.append(o.executionVolume,	executionVolume)
			.append(o.executionValue,	executionValue)
			.append(o.executionAction,	executionAction)
			.append(o.executionPrice,	executionPrice)
			.append(o.executionSymbol,	executionSymbol)
			.append(o.executionTime,	executionTime)
			.append(o.executionValue,	executionValue)
			.append(o.currentVolume,	currentVolume)
			.append(o.executedValue,	executedValue)
			.append(o.finalizationTime,	finalizationTime)
			.append(o.finalStatus,		finalStatus)
			.append(o.initialStatus,	initialStatus)
			.isEquals();
	}
	
	public CDecimal getPositionVolumeChange() {
		return executionAction == OrderAction.BUY ? executionVolume : executionVolume.negate();
	}

}
