package ru.prolib.aquila.core.concurrency;

import java.util.Comparator;

public class LockableComparator implements Comparator<Lockable> {
	private static final LockableComparator instance = new LockableComparator();
	
	public static LockableComparator getInstance() {
		return instance;
	}
	
	private LockableComparator() {
		
	}

	@Override
	public int compare(Lockable o1, Lockable o2) {
		return o1.getLID().compareTo(o2.getLID());
	}

}
