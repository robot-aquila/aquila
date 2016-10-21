package ru.prolib.aquila.probe.datasim.symbol;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class SymbolUpdateTask implements Runnable {
	private final Symbol symbol;
	private final DeltaUpdate update;
	private final int sequenceID;
	private final SymbolUpdateConsumer consumer;
	
	public SymbolUpdateTask(Symbol symbol, DeltaUpdate update, int sequenceID, SymbolUpdateConsumer consumer) {
		this.symbol = symbol;
		this.update = update;
		this.sequenceID = sequenceID;
		this.consumer = consumer;
	}

	@Override
	public void run() {
		consumer.consume(symbol, update, sequenceID);
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

	public SymbolUpdateConsumer getConsumer() {
		return consumer;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != SymbolUpdateTask.class ) {
			return false;
		}
		SymbolUpdateTask o = (SymbolUpdateTask) other;
		return new EqualsBuilder()
			.append(consumer, o.consumer)
			.append(sequenceID, o.sequenceID)
			.append(symbol, o.symbol)
			.append(update, o.update)
			.isEquals();
	}
	
}
