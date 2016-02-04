package ru.prolib.aquila.core.BusinessEntities;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class MDUpdateRecordImpl implements MDUpdateRecord {
	private final Tick tick;
	private final MDTransactionType transactionType;
	
	public MDUpdateRecordImpl(Tick tick, MDTransactionType transactionType) {
		super();
		this.tick = tick;
		this.transactionType = transactionType;
	}

	@Override
	public Tick getTick() {
		return tick;
	}

	@Override
	public MDTransactionType getTransactionType() {
		return transactionType;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == null || !(other instanceof MDUpdateRecord) ) {
			return false;
		}
		MDUpdateRecord o = (MDUpdateRecord) other;
		return new EqualsBuilder()
			.append(tick, o.getTick())
			.append(transactionType, o.getTransactionType())
			.isEquals();
	}

}
