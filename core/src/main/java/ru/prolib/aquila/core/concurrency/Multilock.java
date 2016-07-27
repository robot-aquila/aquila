package ru.prolib.aquila.core.concurrency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Multilock implements Lockable {
	private final LID lid;
	private final List<Lockable> objects;
	
	private static List<Lockable> toList(Lockable... lockables) {
		List<Lockable> objects = new ArrayList<>();
		for ( Lockable x : lockables ) {
			objects.add(x);
		}
		return objects;
	}
	
	public Multilock(List<? extends Lockable> objects) {
		this.lid = LID.createInstance();
		this.objects = new ArrayList<>(objects);
		Collections.sort(this.objects, LockableComparator.getInstance());
	}
	
	public Multilock(Lockable object1, Lockable object2) {
		this(toList(object1, object2));
	}

	public Multilock(Lockable object1, Lockable object2, Lockable object3) {
		this(toList(object1, object2, object3));
	}

	public Multilock(Lockable object1, Lockable object2, Lockable object3,
			Lockable object4)
	{
		this(toList(object1, object2, object3, object4));
	}

	public Multilock(Lockable object1, Lockable object2, Lockable object3,
			Lockable object4, Lockable object5)
	{
		this(toList(object1, object2, object3, object4, object5));
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

}
