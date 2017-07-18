package ru.prolib.aquila.core.BusinessEntities;

import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.EventProducer;
import ru.prolib.aquila.core.concurrency.LID;
import ru.prolib.aquila.core.concurrency.Lockable;
import ru.prolib.aquila.core.concurrency.Multilock;

public class EventSuppressor implements Lockable {
	private final LID lid;
	private final Set<? extends BusinessEntity> objects;
	private final Lockable multilock;
	
	public EventSuppressor(LID lid, Set<? extends BusinessEntity> objects, Lockable multilock) {
		this.lid = lid;
		this.objects = objects;
		this.multilock = multilock;
	}
	
	public EventSuppressor(LID lid, Set<? extends BusinessEntity> objects) {
		this(lid, objects, new Multilock(objects));
	}
	
	public EventSuppressor(Set<? extends BusinessEntity> objects) {
		this(LID.createInstance(), objects);
	}
	
	@Override
	public LID getLID() {
		return lid;
	}
	
	public Set<? extends EventProducer> getObjects() {
		return Collections.unmodifiableSet(objects);
	}
	
	public Lockable getMultilock() {
		return multilock;
	}

	@Override
	public void lock() {
		multilock.lock();
		for ( EventProducer x : objects ) {
			x.suppressEvents();
		}
	}

	@Override
	public void unlock() {
		multilock.unlock();
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
				.append(multilock, o.multilock)
				.isEquals();
	}

}
