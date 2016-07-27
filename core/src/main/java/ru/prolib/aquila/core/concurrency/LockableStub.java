package ru.prolib.aquila.core.concurrency;

import java.util.concurrent.locks.Lock;

public class LockableStub implements Lockable {
	private final LID lid;
	private final Lock lock;
	
	public LockableStub(Lock lock) {
		this.lid = LID.createInstance();
		this.lock = lock;
	}
	
	public LockableStub() {
		this(null);
	}

	@Override
	public LID getLID() {
		return lid;
	}

	@Override
	public void lock() {
		lock.lock();
	}

	@Override
	public void unlock() {
		lock.unlock();
	}

}
