package ru.prolib.aquila.data.storage;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;
import ru.prolib.aquila.core.SimpleEventFactory;
import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.core.BusinessEntities.L1UpdateConsumer;
import ru.prolib.aquila.core.BusinessEntities.L1UpdateImpl;
import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.data.ReaderFactory;
import ru.prolib.aquila.data.storage.file.SimpleCsvL1UpdateReaderFactory;

/**
 * This class is deprecated and will be removed.
 */
@Deprecated
public class SimpleL1Replay {
	private static final int MIN_QUEUE_SIZE = 100;
	private static final int MAX_QUEUE_SIZE = 200;
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(SimpleL1Replay.class);
	}
	
	public static class ConsumeTickTask implements Runnable {
		private final SimpleL1Replay consumer;
		private final L1Update update;
		private final long sequenceID;
		
		ConsumeTickTask(SimpleL1Replay consumer, L1Update update, long id) {
			super();
			this.consumer = consumer;
			this.update = update;
			this.sequenceID = id;
		}

		@Override
		public void run() {
			consumer.consumeUpdate(update, sequenceID);
		}
		
		@Override
		public boolean equals(Object other) {
			if ( other == this ) {
				return true;
			}
			if ( other == null || other.getClass() != ConsumeTickTask.class ) {
				return false;
			}
			ConsumeTickTask o = (ConsumeTickTask) other;
			return new EqualsBuilder()
				.append(consumer, o.consumer)
				.append(update, o.update)
				.append(sequenceID, o.sequenceID)
				.isEquals();
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName()
				+ "[consumer=" + consumer + ", symbol=" + update.getSymbol()
				+ ", tick=" + update.getTick() + ", sequenceID=" + sequenceID
				+ "]";
		}
		
		public L1Update getUpdate() {
			return update;
		}
		
	}
	
	private final Lock lock;
	private final EventQueue queue;
	private final EventType onStarted, onStopped;
	private final Scheduler scheduler;
	private final L1UpdateConsumer consumer;
	private final ReaderFactory<L1Update> readerFactory;
	private boolean started = false;
	private CloseableIterator<L1Update> reader;
	private long sequenceID = 0;
	private int queued = 0;
	private final int minQueueSize, maxQueueSize;
	/**
	 * Time difference between the time of data feed and time of scheduler.
	 */
	private Duration timeDiff = null;
	
	public SimpleL1Replay(EventQueue queue, Scheduler scheduler,
			L1UpdateConsumer consumer, ReaderFactory<L1Update> readerFactory,
			int minQueueSize, int maxQueueSize)
	{
		super();
		if ( minQueueSize <= 0 ) {
			throw new IllegalArgumentException("Min queue size should be greater than zero");
		}
		if ( maxQueueSize <= minQueueSize ) {
			throw new IllegalArgumentException("Max queue size should be greater than min queue size");
		}
		this.lock = new ReentrantLock();
		this.queue = queue;
		this.onStarted = new EventTypeImpl("STARTED");
		this.onStopped = new EventTypeImpl("STOPPED");
		this.scheduler = scheduler;
		this.consumer = consumer;
		this.readerFactory = readerFactory;
		this.minQueueSize = minQueueSize;
		this.maxQueueSize = maxQueueSize;
	}
	
	public SimpleL1Replay(EventQueue queue, Scheduler scheduler,
			L1UpdateConsumer consumer, ReaderFactory<L1Update> readerFactory)
	{
		this(queue, scheduler, consumer, readerFactory, MIN_QUEUE_SIZE, MAX_QUEUE_SIZE);
	}
	
	public SimpleL1Replay(EventQueue queue, Scheduler scheduler, L1UpdateConsumer consumer, File file) {
		this(queue, scheduler, consumer, new SimpleCsvL1UpdateReaderFactory(file));
	}
	
	public EventQueue getEventQueue() {
		return queue;
	}
	
	public Scheduler getScheduler() {
		return scheduler;
	}
	
	public L1UpdateConsumer getConsumer() {
		return consumer;
	}
	
	public ReaderFactory<? extends L1Update> getReaderFactory() {
		return readerFactory;
	}
	
	public int getMinQueueSize() {
		return minQueueSize;
	}
	
	public int getMaxQueueSize() {
		return maxQueueSize;
	}
	
	public EventType onStarted() {
		return onStarted;
	}
	
	public EventType onStopped() {
		return onStopped;
	}
	
	public void close() {
		stopReadingUpdates();
		onStarted.removeListeners();
		onStarted.removeAlternates();
		onStopped.removeListeners();
		onStopped.removeAlternates();
	}
	
	public boolean isStarted() {
		lock.lock();
		try {
			return started;
		} finally {
			lock.unlock();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void startReadingUpdates() throws IOException {
		lock.lock();
		try {
			if ( started ) {
				throw new IllegalStateException("Already started");
			}
			reader = (CloseableIterator<L1Update>) readerFactory.createReader();
			started = true;
			queue.enqueue(onStarted, new SimpleEventFactory());
			sequenceID ++;
			queued = 0;
			timeDiff = null;
			fillUpQueue();
			
		} finally {
			lock.unlock();
		}
	}
	
	private void closeReader() {
		if ( reader != null ) {
			try {
				reader.close();
			} catch ( IOException e ) {
				logger.warn("Error closing reader: ", e);				
			}
			reader = null;
		}
	}
	
	public void stopReadingUpdates() {
		lock.lock();
		try {
			if ( started ) {
				closeReader();
				started = false;
				queue.enqueue(onStopped, new SimpleEventFactory());
			}
			
		} finally {
			lock.unlock();
		}
	}
	
	private void fillUpQueue() {
		if ( queued >= minQueueSize ) {
			return;
		}
		if ( reader != null ) {
			while ( queued < maxQueueSize ) {
				try {
					if ( reader.next() ) {
						L1Update update = reader.item();
						Tick oldTick = update.getTick();
						Instant newTime = getScheduleTime(update.getSymbol(),
								oldTick.getTime());
						L1Update newUpdate = toNewTimeUpdate(update, newTime);
						scheduler.schedule(new ConsumeTickTask(this,
							newUpdate, sequenceID), newTime);
						queued ++;
						
					} else {
						closeReader();
						break;
					}
				} catch ( IOException e ) {
					logger.error("Unexpected error: ", e);
					closeReader();
					break;
				}
			}
		}
		if ( reader == null && queued == 0 ) {
			stopReadingUpdates();
		}
	}

	public void consumeUpdate(L1Update update, long sequenceID) {
		L1UpdateConsumer dummy = null;
		lock.lock();
		try {
			if ( sequenceID != this.sequenceID || ! started ) {
				return; // skip this update
			}
			queued --;
			dummy = consumer;			
		} finally {
			lock.unlock();
		}
		if ( dummy != null ) {
			dummy.consume(update);
			lock.lock();
			try {
				fillUpQueue();
			} finally {
				lock.unlock();
			}
		}
	}
	
	private L1Update toNewTimeUpdate(L1Update oldUpdate, Instant newTime) {
		Tick oldTick = oldUpdate.getTick();
		L1Update newUpdate = new L1UpdateImpl(oldUpdate.getSymbol(),
				Tick.of(oldTick.getType(), newTime,
				oldTick.getPrice(), oldTick.getSize(), oldTick.getValue()));
		return newUpdate;
	}
	
	/**
	 * Convert update time to schedule time.
	 * <p>
	 * @param symbol - symbol of update
	 * @param updateTime - update time
	 * @return schedule time of update
	 */
	private Instant getScheduleTime(Symbol symbol, Instant updateTime) {
		Instant currentTime = scheduler.getCurrentTime();		
		if ( timeDiff == null ) {
			timeDiff = Duration.between(updateTime, currentTime);
			return currentTime;
		}
		Instant newTime = updateTime.plus(timeDiff);
		return newTime.compareTo(currentTime) < 0 ? currentTime : newTime;
	}

}
