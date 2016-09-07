package ru.prolib.aquila.data.replay;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.TStamped;

/**
 * Task of consumption TStamped object.
 */
public class TStampedReplayConsume implements Runnable {
	private final TStampedReplay owner;
	private final long sequenceID;
	private final TStamped object;
	
	public TStampedReplayConsume(TStampedReplay owner, long sequenceID, TStamped object) {
		this.owner = owner;
		this.sequenceID = sequenceID;
		this.object = object;
	}
	
	public TStampedReplay getOwner() {
		return owner;
	}
	
	public long getSequenceID() {
		return sequenceID;
	}
	
	public TStamped getObject() {
		return object;
	}

	@Override
	public void run() {
		owner.consume(sequenceID, object);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName()
				+ "[srvID=" + owner.getServiceID()
				+ " seqID=" + sequenceID + " " + object + "]"; 
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TStampedReplayConsume.class ) {
			return false;
		}
		TStampedReplayConsume o = (TStampedReplayConsume) other;
		return new EqualsBuilder()
			.append(owner, o.owner)
			.append(sequenceID, o.sequenceID)
			.append(object, o.object)
			.isEquals();
	}
	
}