package ru.prolib.aquila.probe.datasim.l1;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class L1UpdateTask implements Runnable {
	private final L1Update update;
	private final int sequenceID;
	private final L1UpdateConsumerEx consumer;
	
	public L1UpdateTask(L1Update update, int sequenceID, L1UpdateConsumerEx consumer) {
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
	
	public L1UpdateConsumerEx getConsumer() {
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
		if ( other == null || other.getClass() != L1UpdateTask.class ) {
			return false;
		}
		L1UpdateTask o = (L1UpdateTask) other;
		return new EqualsBuilder()
			.append(o.consumer, consumer)
			.append(o.sequenceID, sequenceID)
			.append(o.update, update)
			.isEquals();
	}

}
