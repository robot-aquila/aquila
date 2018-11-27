package ru.prolib.aquila.probe.datasim.l1;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class L1UpdateTask implements Runnable {
	private final Symbol symbol;
	private final List<L1Update> updates;
	private final int sequenceID;
	private final L1UpdateConsumerEx consumer;
	
	public L1UpdateTask(List<L1Update> updates,
			int sequenceID,
			L1UpdateConsumerEx consumer)
	{
		this.symbol = updates.get(0).getSymbol();
		this.updates = updates;
		this.sequenceID = sequenceID;
		this.consumer = consumer;
	}
	
	public L1UpdateTask(L1Update update,
			int sequenceID,
			L1UpdateConsumerEx consumer) 
	{
		this.symbol = update.getSymbol();
		this.sequenceID = sequenceID;
		this.consumer = consumer;
		this.updates = new ArrayList<>();
		this.updates.add(update);
	}
	
	public Symbol getSymbol() {
		return symbol;
	}
	
	public List<L1Update> getUpdates() {
		return updates;
	}
	
	public int getSequenceID() {
		return sequenceID;
	}
	
	public L1UpdateConsumerEx getConsumer() {
		return consumer;
	}

	@Override
	public void run() {
		consumer.consume(updates, sequenceID);
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
			.append(o.updates, updates)
			.isEquals();
	}
	
	@Override
	public String toString() {
		return "L1Update[symbol=" + symbol + " numUpdates=" + updates.size() + "]";
	}

}
