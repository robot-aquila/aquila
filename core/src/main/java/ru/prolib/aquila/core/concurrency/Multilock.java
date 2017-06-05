package ru.prolib.aquila.core.concurrency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class Multilock implements Lockable {
	private final LID lid;
	private final List<Lockable> objects;
	
	private static Set<Lockable> toSet(Lockable... lockables) {
		Set<Lockable> objects = new HashSet<>();
		for ( Lockable x : lockables ) {
			objects.add(x);
		}
		return objects;
	}
	
	public Multilock(LID lid, Set<? extends Lockable> objects) {
		this.lid = lid;
		this.objects = new ArrayList<>(objects);
		Collections.sort(this.objects, LockableComparator.getInstance());
	}
	
	public Multilock(Set<? extends Lockable> objects) {
		this(LID.createInstance(), objects);
	}
	
	@Deprecated
	public Multilock(List<? extends Lockable> objects) {
		this(LID.createInstance(), new HashSet<>(objects));
	}
	
	public Multilock(Lockable object1, Lockable object2) {
		this(toSet(object1, object2));
	}

	public Multilock(Lockable object1, Lockable object2, Lockable object3) {
		this(toSet(object1, object2, object3));
	}

	public Multilock(Lockable object1, Lockable object2, Lockable object3,
			Lockable object4)
	{
		this(toSet(object1, object2, object3, object4));
	}

	public Multilock(Lockable object1, Lockable object2, Lockable object3,
			Lockable object4, Lockable object5)
	{
		this(toSet(object1, object2, object3, object4, object5));
	}

	@Override
	public LID getLID() {
		return lid;
	}

	@Override
	public void lock() {
		for ( int i = 0; i < objects.size(); i ++ ) {
			objects.get(i).lock();
		}
	}

	@Override
	public void unlock() {
		for ( int i = objects.size() - 1; i >= 0; i -- ) {
			objects.get(i).unlock();
		}
	}
	
	public List<Lockable> getObjects() {
		return Collections.unmodifiableList(objects);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( this == other ) {
			return true;
		}
		if ( other == null || other.getClass() != Multilock.class ) {
			return false;
		}
		Multilock o = (Multilock) other;
		return new EqualsBuilder()
				.append(o.lid, lid)
				.append(o.objects, objects)
				.isEquals();
	}

}
