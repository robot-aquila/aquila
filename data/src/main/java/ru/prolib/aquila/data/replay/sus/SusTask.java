package ru.prolib.aquila.data.replay.sus;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class SusTask implements Runnable {
	private final Symbol symbol;
	private final DeltaUpdate update;
	private final int sequenceID;
	private final SusTaskConsumer consumer;
	
	public SusTask(Symbol symbol, DeltaUpdate update, int sequenceID, SusTaskConsumer consumer) {
		this.symbol = symbol;
		this.update = update;
		this.sequenceID = sequenceID;
		this.consumer = consumer;
	}

	@Override
	public void run() {
		consumer.consume(this);
	}
	
	public Symbol getSymbol() {
		return symbol;
	}
	
	public DeltaUpdate getUpdate() {
		return update;
	}
	
	public int getSequenceID() {
		return sequenceID;
	}

	public SusTaskConsumer getConsumer() {
		return consumer;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != SusTask.class ) {
			return false;
		}
		SusTask o = (SusTask) other;
		return new EqualsBuilder()
			.append(consumer, o.consumer)
			.append(sequenceID, o.sequenceID)
			.append(symbol, o.symbol)
			.append(update, o.update)
			.isEquals();
	}
	
}
