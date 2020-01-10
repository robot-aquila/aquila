package ru.prolib.aquila.core.concurrency;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SelectiveBarrier {
	private final Lock lock;
	private final Condition condition;
	private Set<Thread> allowedThreads;
	private boolean allowAll;
	
	public SelectiveBarrier(Set<Thread> allowed_threads) {
		this.lock = new ReentrantLock();
		this.condition = lock.newCondition();
		this.allowAll = true;
		this.allowedThreads = new HashSet<>(allowed_threads);
	}
	
	public SelectiveBarrier() {
		this(new HashSet<>());
	}
	
	public void setAllowedThreads(Collection<Thread> allowed_threads) {
		lock.lock();
		try {
			allowedThreads = new HashSet<>(allowed_threads);
			condition.signalAll();
		} finally {
			lock.unlock();
		}
	}
	
	public void setAllowAll(boolean allow_all) {
		lock.lock();
		try {
			allowAll = allow_all;
			condition.signalAll();
		} finally {
			lock.unlock();
		}
	}
	
	public void await(long time, TimeUnit unit) throws InterruptedException, TimeoutException {
		long timeout = unit.toNanos(time);
		lock.lock();
		try {
			Thread current_thread = Thread.currentThread();
			while ( ! allowAll && ! allowedThreads.contains(current_thread) ) {
				if ( timeout < 0L ) {
					throw new TimeoutException();
				}
				timeout = condition.awaitNanos(timeout);
			}
		} finally {
			lock.unlock();
		}
	}

}
