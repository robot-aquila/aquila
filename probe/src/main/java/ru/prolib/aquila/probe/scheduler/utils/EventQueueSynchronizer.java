package ru.prolib.aquila.probe.scheduler.utils;

import java.time.Instant;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventFactory;
import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;
import ru.prolib.aquila.probe.ThreadSynchronizer;

@Deprecated
public class EventQueueSynchronizer implements ThreadSynchronizer, EventListener {
	private final EventQueue queue;
	private final EventType onSynchronize;
	private final Lock lock;
	private final Condition newID;
	private long lastID, lastSentID;
	private boolean closed = false;
	
	EventQueueSynchronizer(EventQueue queue, EventType onSynchronize, long initialID, long initialSentID) {
		this.queue = queue;
		this.onSynchronize = onSynchronize;
		this.lastID = initialID;
		this.lastSentID = initialSentID;
		this.lock = new ReentrantLock();
		this.newID = lock.newCondition();
		onSynchronize.addListener(this);
	}
	
	public EventQueueSynchronizer(EventQueue queue) {
		this(queue, new EventTypeImpl("SYNCHRONIZE"), Long.MIN_VALUE, Long.MIN_VALUE);
	}
	
	public long getLastID() {
		lock.lock();
		try {
			return lastID;
		} finally {
			lock.unlock();
		}
	}
	
	public long getLastSentID() {
		lock.lock();
		try {
			return lastSentID;
		} finally {
			lock.unlock();
		}
	}
	
	public void close() {
		lock.lock();
		try {
			if ( ! closed ) {
				onSynchronize.removeListener(this);
				closed = true;
			}
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void beforeExecution(Instant currentTime) {

	}

	@Override
	public void afterExecution(Instant currentTime) {
		long x;
		lock.lock();
		try {
			if ( closed ) {
				return;
			}
			x = ++lastSentID;
			queue.enqueue(onSynchronize, new SynchronizeEventFactory(x));
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void waitForThread(Instant currentTime) throws InterruptedException {
		lock.lock();
		try {
			if ( closed ) {
				return;
			}
			// Wait until the queue passed an event with most actual ID
			while ( lastID < lastSentID ) {
				newID.await();
			}
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void onEvent(Event event) {
		lock.lock();
		try {
			lastID = ((SynchronizeEvent) event).getID();
			newID.signalAll();
		} finally {
			lock.unlock();
		}
	}
	
	public static class SynchronizeEvent extends EventImpl {
		private final long id;

		public SynchronizeEvent(EventType type, long id) {
			super(type);
			this.id = id;
		}
		
		public long getID() {
			return id;
		}
		
	}
	
	public static class SynchronizeEventFactory implements EventFactory {
		private final long id;
		
		public SynchronizeEventFactory(long id) {
			this.id = id;
		}
		
		@Override
		public Event produceEvent(EventType type) {
			return new SynchronizeEvent(type, id);
		}
		
		@Override
		public boolean equals(Object other) {
			if ( other == this ) {
				return true;
			}
			if ( other == null || other.getClass() != SynchronizeEventFactory.class ) {
				return false;
			}
			SynchronizeEventFactory o = (SynchronizeEventFactory) other;
			return o.id == id;
		}
		
	}

}
