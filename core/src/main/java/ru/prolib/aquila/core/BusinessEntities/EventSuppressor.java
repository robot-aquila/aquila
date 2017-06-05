package ru.prolib.aquila.core.BusinessEntities;

import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.EventProducer;
import ru.prolib.aquila.core.concurrency.LID;
import ru.prolib.aquila.core.concurrency.Lockable;

public class EventSuppressor implements Lockable {
	private final LID lid;
	private final Set<? extends EventProducer> objects;
	
	public EventSuppressor(LID lid, Set<? extends EventProducer> objects) {
		this.lid = lid;
		this.objects = objects;
	}
	
	public EventSuppressor(Set<? extends EventProducer> objects) {
		this(LID.createInstance(), objects);
	}
	
	@Override
	public LID getLID() {
		return lid;
	}
	
	public Set<? extends EventProducer> getObjects() {
		return Collections.unmodifiableSet(objects);
	}

	@Override
	public void lock() {
		for ( EventProducer x : objects ) {
			x.suppressEvents();
		}
	}

	@Override
	public void unlock() {
		for ( EventProducer x : objects ) {
			x.restoreEvents();
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( this == other ) {
			return true;
		}
		if ( other == null || other.getClass() != EventSuppressor.class ) {
			return false;
		}
		EventSuppressor o = (EventSuppressor) other;
		return new EqualsBuilder()
				.append(lid, o.lid)
				.append(objects, o.objects)
				.isEquals();
	}

}
