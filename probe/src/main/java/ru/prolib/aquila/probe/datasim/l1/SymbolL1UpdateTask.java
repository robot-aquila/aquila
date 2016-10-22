package ru.prolib.aquila.probe.datasim.l1;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class SymbolL1UpdateTask implements Runnable {
	private final L1Update update;
	private final int sequenceID;
	private final SymbolL1UpdateConsumer consumer;
	
	public SymbolL1UpdateTask(L1Update update, int sequenceID, SymbolL1UpdateConsumer consumer) {
		this.update = update;
		this.sequenceID = sequenceID;
		this.consumer = consumer;
	}
	
	public Symbol getSymbol() {
		return update.getSymbol();
	}
	
	public L1Update getUpdate() {
		return update;
	}
	
	public int getSequenceID() {
		return sequenceID;
	}
	
	public SymbolL1UpdateConsumer getConsumer() {
		return consumer;
	}

	@Override
	public void run() {
		consumer.consume(update, sequenceID);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != SymbolL1UpdateTask.class ) {
			return false;
		}
		SymbolL1UpdateTask o = (SymbolL1UpdateTask) other;
		return new EqualsBuilder()
			.append(o.consumer, consumer)
			.append(o.sequenceID, sequenceID)
			.append(o.update, update)
			.isEquals();
	}

}
